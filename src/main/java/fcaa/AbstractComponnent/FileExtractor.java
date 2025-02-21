package fcaa.AbstractComponnent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.tika.Tika;
import org.testng.Reporter;

public class FileExtractor {
	public static int i = 0;

	public String readFile(String fileUrl, int dataPosition) throws IOException, InterruptedException {
		Path tempFile = Files.createTempFile("downloaded_", ".tmp");
		String extractedText = "";
		try {
			// Extract actual file URL from Office Online Viewer
			String actualFileUrl = extractActualFileUrl(fileUrl)
					.orElseThrow(() -> new IOException("Invalid URL: Unable to extract the actual file URL."));

			// Convert string to URI and open connection
			URI uri = new URI(actualFileUrl);
			URLConnection connection = uri.toURL().openConnection(); // Use URI -> URL conversion
//	        connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Mimic browser to prevent blocking

			// Download the file
			try (InputStream in = connection.getInputStream()) {
				Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
			} catch (FileNotFoundException e) {
				Reporter.log("❌ Error in finding the Documnet at : " + dataPosition + "", true);
				throw e;
			}

			// Detect MIME type using Tika
			Tika tika = new Tika();
			String mimeType = tika.detect(tempFile.toFile());

			if ((mimeType.equals("application/x-tika-ooxml") || mimeType.equals("application/x-tika-msoffice")) && (actualFileUrl.contains("xlsx") || actualFileUrl.contains("xls"))) {
				try (FileInputStream fis = new FileInputStream(tempFile.toFile());
						XSSFWorkbook workbook = new XSSFWorkbook(fis)) { // Assign to a variable
					mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
				} catch (Exception e) {
					// If it fails, it's not .xlsx; fallback to .xls or another format
					mimeType = "application/vnd.ms-excel";
				}

			}
			
			if (mimeType.equals("application/zip") && actualFileUrl.contains("docx")) {
                mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if(mimeType.equals("application/zip") && actualFileUrl.endsWith(".doc")) {
            	mimeType = "application/x-tika-msoffice";
            }

			System.out.println("Final MIME Type: " + mimeType);

			// Process file based on MIME type
			extractedText = switch (mimeType) {
			case "application/pdf" -> extractTextFromPDF(tempFile);
			case "application/x-tika-msoffice" -> extractTextFromDOC(tempFile);
			case "application/x-tika-ooxml",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
				extractTextFromDOCX(tempFile);
			case "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->
				extractTextFromExcel(tempFile);
			case "text/html" -> extractTextFromDOC(tempFile);
			case "application/zip" -> extractTextFromZIP(tempFile);
			case "text/csv" -> extractTextFromCSV(tempFile);
			default -> {
				System.out.println("Unsupported file format: " + mimeType + " at : " + dataPosition + "");
				yield "";
			}
			};

		} catch (URISyntaxException e) {
			throw new IOException("Invalid URL syntax: " + fileUrl, e);
		} finally {
			Files.deleteIfExists(tempFile);
		}

		return extractedText;
	}

	// Extract actual file URL from Office Online Viewer
	private Optional<String> extractActualFileUrl(String fileUrl) {
		if (!fileUrl.contains("view.officeapps.live.com/op/view.aspx?src=")) {
			return Optional.of(fileUrl.replace(" ", "%20")); // Fix spaces in normal URLs
		}

		String[] parts = fileUrl.split("src=");
		if (parts.length < 2)
			return Optional.empty();

		String encodedUrl = parts[1].split("&")[0];
		String decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);

		// Fix spaces by replacing them with %20
		decodedUrl = decodedUrl.replace(" ", "%20");
		return Optional.of(decodedUrl);
	}

	private String extractTextFromPDF(Path tempFile) {
		StringBuilder extractedText = new StringBuilder();
		try (PDDocument document = PDDocument.load(tempFile.toFile())) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);
			extractedText.append(cleanText(text));
		} catch (IOException e) {
			System.out.println("❌ Error extracting PDF: " + e.getMessage());
		}
		return extractedText.toString();
	}

	private String extractTextFromZIP(Path tempFile) throws IOException {
		StringBuilder extractedText = new StringBuilder();

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempFile.toFile()))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory() && entry.getName().matches(".*\\.(txt|csv|json|xml|log)$")) {
					// Process only text-based files
					extractedText.append("\n--- ").append(entry.getName()).append(" ---\n");
					extractedText.append(new String(zis.readAllBytes(), StandardCharsets.UTF_8)).append("\n");
				}
				zis.closeEntry();
			}
		} catch (IOException e) {
			System.out.println("❌ Error extracting ZIP: " + e.getMessage());
			throw e;
		}

		return cleanText(extractedText.toString());
	}

	private String extractTextFromExcel(Path tempFile) throws IOException {
	    StringBuilder extractedText = new StringBuilder();

	    try (FileInputStream fis = new FileInputStream(tempFile.toFile());
	         Workbook workbook = WorkbookFactory.create(fis)) { // Ensure Workbook is closed properly
	         
	        for (Sheet sheet : workbook) {
	            extractedText.append("\n--- Sheet: ").append(sheet.getSheetName()).append(" ---\n");

	            for (Row row : sheet) {
	                List<String> cellTexts = new ArrayList<>();
	                for (Cell cell : row) {
	                    cellTexts.add(cell.toString().trim()); // Get cell value as text
	                }
	                extractedText.append(String.join(" | ", cellTexts)).append("\n");
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("❌ Error extracting Excel: " + e.getMessage());
	        throw e;
	    }

	    return cleanText(extractedText.toString());
	}

	private String extractTextFromDOCX(Path tempFile) throws IOException, InterruptedException {
		StringBuilder extractedText = new StringBuilder();
		try (FileInputStream fis = new FileInputStream(tempFile.toFile()); XWPFDocument docx = new XWPFDocument(fis)) {

			// Extract paragraphs
			for (XWPFParagraph para : docx.getParagraphs()) {
				extractedText.append(cleanText(para.getText())).append("\n");
			}

			// Extract tables
			for (XWPFTable table : docx.getTables()) {
				for (XWPFTableRow row : table.getRows()) {
					String rowText = row.getTableCells().stream().map(cell -> cleanText(cell.getText()))
							.filter(text -> !text.isEmpty()) // Remove empty fields
							.collect(Collectors.joining(" | "));
					if (!rowText.isEmpty()) {
						extractedText.append(rowText).append("\n");
					}
				}
			}

		} catch (IOException e) {
			System.out.println("❌ Error extracting DOCX: " + e.getMessage());
			throw e;
		}
		return extractedText.toString();
	}

	private String extractTextFromDOC(Path tempFile) throws IOException {
		StringBuilder extractedText = new StringBuilder();
		try (FileInputStream fis = new FileInputStream(tempFile.toFile());
				HWPFDocument doc = new HWPFDocument(fis);
				WordExtractor extractor = new WordExtractor(doc)) {

			extractedText.append(cleanText(String.join("\n", extractor.getParagraphText()))).append("\n");

		} catch (IOException e) {
			System.out.println("❌ Error extracting DOC: " + e.getMessage());
			throw e;
		}
		return extractedText.toString();
	}

	private String extractTextFromCSV(Path tempFile) throws IOException {
		StringBuilder extractedText = new StringBuilder();
		try (Reader reader = Files.newBufferedReader(tempFile);
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

			for (CSVRecord record : csvParser) {
				String rowText = record.stream().map(this::cleanText).filter(text -> !text.isEmpty()) // Remove empty
																										// fields
						.collect(Collectors.joining(" | "));
				if (!rowText.isEmpty()) {
					extractedText.append(rowText).append("\n");
				}
			}

		} catch (IOException e) {
			System.out.println("❌ Error extracting CSV: " + e.getMessage());
			throw e;
		}
		return extractedText.toString();
	}

	private String cleanText(String text) {
		if (text == null || text.trim().isEmpty())
			return "";
		// Normalize multiple spaces but keep valid line breaks
		text = text.replaceAll("[ ]{2,}", " ");
		// Remove completely empty lines
		text = text.replaceAll("(?m)^[ \t]*\r?\n", "");
		// Remove headers and footers (detect common patterns like page numbers, version
		// info, etc.)
		text = text.replaceAll("(?m)^.*(Page \\d+|v\\.\\d+ \\(\\w+ \\d{4}\\)).*$", "");
		// Remove empty boxed text (lines with only tab characters or non-printable
		// symbols)
		text = text.replaceAll("(?m)^[\\t\\x07 ]+$", "");
		text = text.replaceAll("[\\[\\]/()]", " ").replaceAll("\\s+", " ");
		return text.trim();
	}

}
