package fcaa.pageObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;

import com.opencsv.exceptions.CsvException;

import fcaa.AbstractComponnent.BooleanSearch;
import fcaa.AbstractComponnent.FileExtractor;
import fcaa.AbstractComponnent.MenuBar;

public class Forms extends MenuBar {

	WebDriver driver;

	public Forms(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// -----------------Search Legal Instruments-------------------//

	@FindBy(xpath = "//input[@placeholder='Search Forms']")
	WebElement searchForms;

	@FindBy(xpath = "//button[text()=' Search ']")
	WebElement searchBtn;

	public void searchForms() {
		searchForms.click();
	}

	public void performSearch(String query) {
		searchForms.clear();
		searchForms.sendKeys(query);
		searchBtn.click();
	}

	// ----------------Published Date List To Verify Sorting ---------------//

	// capture the effective date
	@FindBy(xpath = "//div[contains(@class, 'listing')]//li[b[normalize-space()='Effective Date:']]")
	List<WebElement> effectiveDate;

	// Get Date List //Get published date List
	public List<String> getDateList() throws InterruptedException {
		List<String> getDateList = getDate(effectiveDate);
		return getDateList;
	}

	// -------------------Title List to Verify Sorting--------------//

	// Capture Title
	@FindBy(xpath = "//a[@class='link_color form_title inline sm:text-lg text-base font-semibold cursor-pointer']")
	List<WebElement> contentTitle;

	// Get Title List
	public List<String> getTitleList() throws InterruptedException {
		return titleList(contentTitle);
	}

	// -------------Display Count----------------//

	// Displaying Count
	@FindBy(xpath = "(//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1'])[1]")
	WebElement displayCount;

	public int[] verifyDataCount() throws InterruptedException {
		int[] count = verifyDataCountAcrossPages();
		return count;
	}

	// ------------------- Test Pagination functionality-----------------//

	// Test Pagination functionality
	public int[] checkFirstPageBtn() throws InterruptedException {
		int[] paginationPageNum = clickFirstPageButton();
		return paginationPageNum;
	}

	public int[] checkPreviousPageBtn() throws InterruptedException {
		int[] paginationPageNum = clickPreviousPageButton();
		return paginationPageNum;
	}

	public int[] checkNextPageBtn() throws InterruptedException {
		int[] paginationPageNum = clickNextPageButton();
		return paginationPageNum;
	}

	public int[] checkLastPageBtn() throws InterruptedException {
		return clickLastPageButton();
	}

	// ---------------Click On The Submit Button--------------//

	// Click on the submit button
	@FindBy(xpath = "//div[@class='flex flex-column gap-1']//button[@class='solid_btn flex-1'][normalize-space()='Submit']")
	WebElement selectSubmit;

	// Click on the submit button
	public void selectSubmit() {
		scrollToElement(selectSubmit);
		selectSubmit.click();
	}

	// ----------------------Effective Date--------------------------//

	// Select Month Radio button
	@FindBy(xpath = "//label[@for='radio4']")
	WebElement selectMonthRadio;

	// Select Month Radio button
	public void selectMonthRadio() {
		selectMonthRadio.click();
	}

	// Put Date Range
	@FindBy(xpath = "(//p-calendar[@placeholder='DD/MM/YYYY - DD/MM/YYYY'])[1]")
	WebElement putDateRangeCalendar;

	// Enter date range
	@FindBy(xpath = "//input[@id='formEffectiveDate']")
	WebElement putDateRange;

	// Input date Range
	public String inputDateRange(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		return dateRangeStr;
	}

	// Verify radio button selected
	public boolean verifyRadiobuttonSelected(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		// Locate the radio button
		return selectMonthRadio.getAttribute("class").contains("p-radiobutton-checked");

	}

	// Verify radio button selected
	public boolean verifyDateRangeFieldCleared(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		selectMonthRadio.click();
		return putDateRangeCalendar.getAttribute("class").contains("ng-invalid");

	}

	public boolean verifyDataWithinDateRange(String startDateStr, String endDateStr)
			throws ParseException, InterruptedException {
		// Define date range
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = sdf.parse(startDateStr);
		Date endDate = sdf.parse(endDateStr);

		// Input the date range
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();

		// Click on the submit button
		selectSubmit.click();

		List<String> dateList = getDateList();

		boolean allDatesInRange = true;
		for (String date : dateList) {
			Date dataDate = sdf.parse(date);

			// Assert that data is within the range
			if (dataDate.before(startDate) || dataDate.after(endDate)) {
				allDatesInRange = false; // Set flag to false if any date is out of range
				break; // Exit the loop early if we find a date out of range
			}
		}
		return allDatesInRange;
	}

	// Verifying the Boolean Search feature
	@FindBy(xpath = "//div[contains(@class, 'forms_page')]//a[contains(@class, 'form_title')]")
	private List<WebElement> formListItemsTitles;

	BooleanSearch booleanSearch = new BooleanSearch();
	FileExtractor fileExtractor = new FileExtractor();

	// Validate search results
	public boolean validateResults(String query) throws InterruptedException, CsvException, IOException {
		boolean hasNextPage = true;
		int actualDataPosition = 1;

		while (hasNextPage) {
			for (int i = 0; i < formListItemsTitles.size(); i++) {
				String url = formListItemsTitles.get(i).getAttribute("href");
				String text = fileExtractor.readFile(url, actualDataPosition + i).toLowerCase();

				// Match query against text
				if (!booleanSearch.evaluateQuery(query, text, actualDataPosition + i)) {
					System.out.println(text);
					Reporter.log("Error in Data present at : " + actualDataPosition + i, true);
					return false; // Test fails if any result doesn't match
				}
			}
			hasNextPage = goToNextPageAndIsNextPageAvailable();
			actualDataPosition += formListItemsTitles.size();
		}
		return true; // All results matched
	}
}