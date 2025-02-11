package fcaa.AbstractComponnent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.tika.Tika;
import org.testng.Assert;

import com.opencsv.exceptions.CsvException;

public class BooleanSearch {

	public boolean evaluateQuery(String query, String text) {
		query = query.trim();

		// Handle boosted terms (^)
		if (query.contains("^")) {
			return evaluateBoostedTerms(query, text);
		}

		// Handle parentheses (highest precedence)
		query = handleParentheses(query, text);

		// Handle individual terms or default matches
		if (query.equals("1"))
			return true; // Boolean true
		if (query.equals("0"))
			return false; // Boolean false

		// Handle logical AND (&&)
		if (query.contains(" AND ") || query.contains("&&")) {
			return evaluateLogicalOperator(query, text, "AND|&&");
		}

		// Handle logical OR (|| or implicit OR with spaces)
		if ((query.contains(" OR ") || query.contains("||") || query.matches(".*\\s+.*")
				|| query.matches(".*[~?]\\s.*"))
				&& !(query.matches("^\"[^\"]+\"$") || query.matches("^\"[^\"]+\"\\s*$")
						|| query.matches("^\"[^\"]+\"~\\d+$"))
				&& !query.contains("NOT ")) {
			return evaluateLogicalOperator(query, text, "OR|\\|\\|");
		}

		// Handle logical NOT (-, !)
		if (query.contains("NOT ") || query.contains("-") || query.contains("!")) {
			return evaluateLogicalNot(query, text);
		}

		// Handle proximity terms (~)
		if (query.contains("~")) {
			return evaluateProximityOrFuzzy(query, text);
		}

		// Handle wildcard or regex quantifiers (*, ?)
		if (query.contains("*") || query.contains("?")) {
			return evaluateTerm(query, text);
		}

		// Handle exact phrase match ("" - double quotes)
		if (query.startsWith("\"") && query.endsWith("\"")) {
			return evaluateTerm(query, text);
		}

		// Handle mandatory terms (+)
		if (query.contains("+")) {
			return evaluateMandatoryTerms(query, text);
		}

		// Default: evaluate term or individual term matching
		return evaluateTerm(query, text);
	}

	private String handleParentheses(String query, String text) {
		while (query.contains("(")) {
			int openIndex = query.lastIndexOf("(");
			int closeIndex = query.indexOf(")", openIndex);
			if (closeIndex == -1) {
				throw new IllegalArgumentException("Unmatched parentheses in query.");
			}

			// Evaluate the nested expression
			String nestedQuery = query.substring(openIndex + 1, closeIndex).trim();
			boolean nestedResult = evaluateQuery(nestedQuery, text);
			query = query.substring(0, openIndex) + (nestedResult ? "1" : "0") + query.substring(closeIndex + 1);
		}
		return query;
	}

	private boolean evaluateLogicalNot(String query, String text) {
		String[] parts;

		if (query.contains("NOT")) {
			List<String> partList = new ArrayList<>();
			// Regex to correctly extract terms while preserving "NOT term"
			String regex = "\"[^\"]+\"|\\bNOT\\s+\"[^\"]+\"|\\bNOT\\s+\\S+|\\S+";
	        Matcher matcher = Pattern.compile(regex).matcher(query);

			while (matcher.find()) {
				String match = matcher.group().trim();
				if (!match.isEmpty()) {
					partList.add(match);
				}
			}

			parts = partList.toArray(new String[0]);
		} else {
			parts = query.split("\\s+");
		}

		boolean result = false;
		for (String part : parts) {
			part = part.trim();

			if (part.isEmpty() || part.equals("\"\"")) {
				continue;
			}

			// Skip already resolved boolean literals
			if (part.equals("1")) {
				continue;
			}
			if (part.equals("0")) {
				return false; // If literal "0" is encountered, treat as false immediately
			}

			// Handle negation terms (NOT, -, !)
			if (part.startsWith("NOT ")) {
				String term = part.substring(4).trim(); // Remove "NOT "
				if (evaluateQuery(term, text)) {
					Assert.fail("Negation term is found in the search result");
					return false; // Negation fails if the term is present
				} else {
					result = true;
				}
			} else if (part.startsWith("-") || part.startsWith("!")) {
				String term = part.replaceFirst("^[-!]+", "").trim();
				if (evaluateQuery(term, text)) {
					Assert.fail("Negation term is found in the search result");
					return false; // Negation fails if the term is present
				} else {
					result = true;
				}
			} else {
				boolean isPositiveTermFound = evaluateQuery(part, text);
				result = result || isPositiveTermFound;
			}
		}
		return result; // Return true if any positive term is present
	}

	private boolean evaluateLogicalOperator(String query, String text, String operatorRegex) {
		// Check for AND/OR operators explicitly
		boolean hasExplicitLogicalOperators = query.contains(" AND ") || query.contains("&&") || query.contains(" OR ")
				|| query.contains("||");

		String[] parts;

		if (query.contains("^") && ((query.matches(".*\\)\\^\\d+\\s+.*") || query.contains("&& ("))
				|| (query.matches(".*\\)\\^\\d+\\s+AND\\s+.*") || query.contains("AND (")))) {
			String regex = "\\s+(?i)(AND|&&)\\s+(?![^()]*\\))";
			// Split the query while keeping expressions inside parentheses together
			parts = query.split(regex);
		} else if (operatorRegex.contains("AND")) {
			parts = query.split("(?i)\\s+AND\\s+|\\s*&&\\s*"); // Split on AND or &&
		} else if (operatorRegex.contains("OR")) {
			if (!hasExplicitLogicalOperators) {
				// Split by space but exclude exact operator
				parts = query.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)\\s+");
			} else {
				// If explicit logical operators are used, only split on OR/|| operators
				parts = query.split("(?i)\\s+OR\\s+|\\s*\\|\\|\\s*");
			}
		} else {
			throw new IllegalArgumentException("Invalid operator regex: " + operatorRegex);
		}

		if (operatorRegex.contains("AND")) {
			for (String part : parts) {
				if (!evaluateQuery(part.trim(), text)) {
					return false; // If any part is false, the whole AND condition is false
				}
			}
			return true; // All parts are true
		} else if (operatorRegex.contains("OR")) {
			boolean result = false;
			for (String part : parts) {
				boolean evaluationResult = evaluateQuery(part.trim(), text);
				result = result || evaluationResult;
			}
			return result;
		}
		return false;
	}

	private boolean evaluateBoostedTerms(String query, String text) {
		List<String> parts = new ArrayList<>();
		List<String> unboostedTerms = new ArrayList<>();

		boolean isSpaceOutsideParenthesis = query.matches(".*\\)\\^\\d+\\s+.*") || query.matches(".*\\s+\\(.*");

		if (query.contains("(")
				&& (query.matches(".*\\)\\s*(AND|&&)\\s+.*") || query.matches(".*\\s+(AND|&&)\\s*\\(.*"))
				&& isSpaceOutsideParenthesis) {
			return evaluateLogicalOperator(query, text, "AND|&&");
		}

//		if(query.contains("(") && (query.matches(".*\\)\\s*(OR|\\|\\|)\\s+.*") || query.matches(".*\\s+(OR|\\|\\|)\\s*\\(.*")) && isSpaceOutsideParenthesis) {
//		   return evaluateLogicalOperator(query, text, "OR|\\|\\|");
//		}

		if (query.contains(" NOT ")) {
			return evaluateLogicalNot(query, text);
		}

		// Regular expression to split by space or AND/&& outside of parentheses
		if (query.contains("(")) {
			String regex = "(?<=\\)\\^\\d+)\\s+|\\s+(?=\\()";
			parts = new ArrayList<>(List.of(query.split(regex)));
		} else {
			// Simple split by space if no parentheses are found
			parts = new ArrayList<>(List.of(query.split("\\s+")));
		}
		parts.removeIf(String::isEmpty);

		int maxBoostScore = 0; // Keep track of the highest boost score
		boolean hasUnboostedMatch = false; // If any unboosted term is found

		for (String part : parts) {
			int boostScore = 1;
			String baseTerm = part;

			if (part.contains("^")) {
				String[] parts2 = part.split("\\^");
				baseTerm = parts2[0].trim();
				boostScore = Integer.parseInt(parts2[1]);
			} else {
				unboostedTerms.add(part.trim()); // Store unboosted term for later check
			}

			boolean isMatch = false;
			// Handle grouped terms `(market && abuse)^5`
			if (baseTerm.startsWith("(") && baseTerm.endsWith(")")) {
				baseTerm = baseTerm.substring(1, baseTerm.length() - 1);
				String[] subTerms = baseTerm.split("\\s*&&\\s*");

				boolean allSubTermsMatch = true;
				for (String subTerm : subTerms) {
					if (!evaluateQuery(subTerm.trim(), text)) {
						allSubTermsMatch = false;
						break;
					}
				}
				isMatch = allSubTermsMatch;
			} else {
				isMatch = evaluateQuery(baseTerm, text);
			}
			if (isMatch) {
				maxBoostScore = Math.max(maxBoostScore, boostScore);
			}
		}

		// Check if any unboosted term is present in text
		for (String term : unboostedTerms) {
			if (evaluateQuery(term, text)) {
				hasUnboostedMatch = true;
				break;
			}
		}

		return hasUnboostedMatch || maxBoostScore > 0;
	}

	private boolean evaluateMandatoryTerms(String query, String text) {
		String[] terms = query.split("\\s+");
		for (String term : terms) {
			if (term.startsWith("+")) {
				if (!evaluateQuery(term.substring(1).trim(), text)) {
					System.out.println(text);
					Assert.fail("Mandatory term not found in the search result");
					return false; // Mandatory term not found
				}
			}
		}
		return true;
	}

	private boolean evaluateTerm(String term, String text) {
		term = term.trim();

		// Handle exact phrases (enclosed in double quotes)
		if (term.startsWith("\"") && term.endsWith("\"")) {
			String exactPhrase = term.substring(1, term.length() - 1).trim(); // Remove quotes only from start and end
		    // Check for exact word match using regex to avoid substring issues
		    return Pattern.compile("\\b" + Pattern.quote(exactPhrase) + "\\b").matcher(text).find();
		}

		// Handle wildcards (* and ?)
		if (term.contains("*") || term.contains("?")) {
			String regex = "\\b" + term.replace("*", ".*").replace("?", ".") + "\\b";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			return pattern.matcher(text).find();
		}

//		return text.contains(term);
		return containsTerm(term, text);
	}

	private boolean containsTerm(String term, String text) {
		String regex = "\\b" + Pattern.quote(term.trim().toLowerCase()) + "\\b";
		return Pattern.compile(regex).matcher(text).find();
	}

	// Evaluate proximity or fuzzy matching (~)
	private boolean evaluateProximityOrFuzzy(String query, String text) {

		// Handle proximity terms (~) as well as OR
		if (query.contains("~") && (query.contains(" OR ") || query.contains("||"))) {
			return evaluateLogicalOperator(query, text, "OR|\\|\\|");
		}

		if (query.matches(".*[~?]\\s.*") && !(query.matches("^\"[^\"]+\"$") || query.matches("^\"[^\"]+\"\\s*$"))) {
			return evaluateLogicalOperator(query, text, "OR|\\|\\|");
		}

		// Existing proximity handling
		if (query.matches(".*\".*\"~\\d+.*")) {
			return evaluateProximityQuery(query, text); // Handle proximity query
		}

		if (query.startsWith("+") && query.endsWith("~")) {
			return evaluateMandatoryTerms(query, text);
		}

		if ((query.startsWith("*") && query.endsWith("~")) || ((query.startsWith("?") && query.endsWith("~")))) {
			// Extract actual term by removing '*' from start and '~' from end
			String processedQuery = query.substring(1, query.length() - 1); // Remove * and ~

			// Convert wildcard (*) to regex format
			String regex = "(?i)\\b\\w*" + processedQuery.replace("*", ".*").replace("?", ".") + "\\w*\\b";

			// Compile regex and find matches in the text
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);

			List<String> candidateWords = new ArrayList<>();

			while (matcher.find()) {
				candidateWords.add(matcher.group()); // Store words matching wildcard pattern
			}

			// If no candidate words match, return false
			if (candidateWords.isEmpty()) {
				return false;
			}

			boolean result = true;
			// Check for approximate match (~) on identified candidate words
			for (String candidate : candidateWords) {
				boolean isFound = containsApproximateMatch(candidate, text);
				result = result && isFound;
			}

			return result; // No approximate match found
		}

		// Fuzzy match: validate term structure
		String[] terms = query.split("~");
		String term = terms[0].trim();
		return containsApproximateMatch(term, text);
	}

	// Proximity matching logic
	private static boolean evaluateProximityQuery(String query, String text) {
		// Extract words and proximity from query
		Pattern pattern = Pattern.compile("\"(.*?)\"~?(\\d*)");
		Matcher matcher = pattern.matcher(query);

		if (!matcher.find()) {
			System.out.println("Error: Query format is incorrect.");
			return false;
		}

		String[] phraseWords = matcher.group(1).split("\\s+");
		int proximity = matcher.group(2).isEmpty() ? 1 : Integer.parseInt(matcher.group(2));

		if (phraseWords.length != 2) {
			System.out.println("Error: Query should contain exactly two words.");
			return false;
		}

		String word1 = phraseWords[0].toLowerCase();
		String word2 = phraseWords[1].toLowerCase();

		// Normalize text
		text = text.replaceAll("[’']", "").replaceAll("-", " ").replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

		String[] words = text.split("\\s+"); // Ensure correct word splitting

		List<Integer> word1Indexes = new ArrayList<>();
		List<Integer> word2Indexes = new ArrayList<>();

		// Step 1: Store all positions of both words
		for (int i = 0; i < words.length; i++) {
			if (words[i].equals(word1)) {
				word1Indexes.add(i);
			}
			if (words[i].equals(word2)) {
				word2Indexes.add(i);
			}
		}

		// Step 2: Check if any pair of (word1, word2) is within proximity range
		for (int i : word1Indexes) {
			for (int j : word2Indexes) {
				if (Math.abs(i - j) - 1 <= proximity) { // Ensure words are within range
					return true;
				}
			}
		}

		return false;
	}

	// Handles wildcard (~) fuzzy matching
	private boolean containsApproximateMatch(String term, String text) {
		// Sanitize the input term and text, ensuring only alphanumeric characters are
		// included
		String sanitizedTerm = term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(); // Remove non-alphanumeric,lower case
		String[] words = text.split("\\s+");

		for (String word : words) {
			String sanitizedWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

			if (sanitizedWord.isEmpty()) {
				continue;
			}

			if (calculateLevenshteinDistance(sanitizedTerm, sanitizedWord) <= 3) {
				return true;
			}
		}

		return false; // Return false if no match is found
	}

	// Levenshtein distance algorithm for approximate matching
	private int calculateLevenshteinDistance(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			for (int j = 0; j <= len2; j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				} else {
					dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
				}
			}
		}
		return dp[len1][len2];
	}
	
	public String readFile(String fileUrl) throws CsvException, IOException {
		Path tempFile = null;
		String extractedText = "";

		try {
			// Create a temporary file (auto-deletes on exit)
			tempFile = Files.createTempFile("downloaded_", ".tmp");

			// Download the file from the URL
			try (InputStream in = URI.create(fileUrl).toURL().openStream()) {
				Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
			}

			// Use Tika to detect the MIME type of the file
			Tika tika = new Tika();
			String mimeType = tika.detect(tempFile.toFile());

			if (mimeType != null) {
				switch (mimeType) {
				case "application/pdf":
					extractedText = extractTextFromPDF(tempFile);
//					System.out.println(extractedText);
					break;
				case "application/x-tika-msoffice":
					extractedText = extractTextFromDOC(tempFile); // Only for .doc (OLE2 format)
//					System.out.println(extractedText);
					break;
				case "application/x-tika-ooxml":
					extractedText = extractTextFromDOCX(tempFile);
//					System.out.println(extractedText);
					break;
				case "application/vnd.ms-excel":
					extractedText = extractTextFromExcel(tempFile);
					break;
				case "application/zip":
					extractedText = extractTextFromZIP(tempFile);
					break;
				case "text/csv":
					extractedText = extractTextFromCSV(tempFile);
					break;
				default:
					System.out.println("Unsupported file format: " + mimeType);
					break;
				}
			} else {
				System.out.println("Unable to determine MIME type for the file.");
			}

			// Delete the temp file after processing
			Files.deleteIfExists(tempFile);

		} catch (IOException e) {
			System.out.println("❌ Error: " + e.getMessage());
			throw e;
		}
		return extractedText;
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

		try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
			Workbook workbook = WorkbookFactory.create(fis);

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

	private String extractTextFromDOCX(Path tempFile) throws IOException {
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
		return text.trim();
	}

}
