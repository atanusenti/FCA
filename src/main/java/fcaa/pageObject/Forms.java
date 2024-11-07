package fcaa.pageObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import fcaa.AbstractComponnent.MenuBar;

public class Forms extends MenuBar {

	WebDriver driver;

	public Forms(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//input[@placeholder='Search Forms']")
	WebElement searchForms;

	// capture the effective date
	@FindBy(xpath = "//div//ul//li[@class='px-0 md:px-3 text-sm ng-star-inserted'][3]")
	List<WebElement> effectiveDate;

	// Capture Title
	@FindBy(xpath = "//a[@class='link_color form_title inline sm:text-lg text-base font-semibold cursor-pointer']")
	List<WebElement> contentTitle;

	public void searchForms() {
		searchForms.click();
	}

	// Get Date List //Get published date List
	public List<String> getDateList() throws InterruptedException {
		List<String> getDateList = getDate(effectiveDate);
		return getDateList;
	}

	// Get Title List
	public List<String> getTitleList() throws InterruptedException {
		List<String> titleList = titleList(contentTitle);
		return titleList;
	}

	// Select Month Radio button
	@FindBy(xpath = "//label[@for='radio4']")
	WebElement selectMonthRadio;

	// Click on the submit button
	@FindBy(xpath = "//div[@class='flex flex-column gap-1']//button[@class='solid_btn flex-1'][normalize-space()='Submit']")
	WebElement selectSubmit;

	// Click on the submit button
	public void selectSubmit() {
		selectSubmit.click();
	}

	// Select Month Radio button
	public void selectMonthRadio() {
		selectMonthRadio.click();
	}

	// Enter date range
	@FindBy(xpath = "//input[@id='formEffectiveDate']")
	WebElement putDateRange;

	// Displaying Count
	@FindBy(xpath = "(//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1'])[1]")
	WebElement displayCount;

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

	// Put Date Range
	@FindBy(xpath = "(//p-calendar[@placeholder='DD/MM/YYYY - DD/MM/YYYY'])[1]")
	WebElement putDateRangeCalendar;

	// Verify radio button selected
	public boolean verifyDateRangeFieldCleared(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		selectMonthRadio.click();
		return putDateRangeCalendar.getAttribute("class").contains("ng-invalid");

	}

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
	
	public int[] verifyDataCount() throws InterruptedException {
		int[] count = verifyDataCountAcrossPages();
		return count;
	}
	
	public boolean verifyDataWithinDateRange(String startDateStr, String endDateStr) throws ParseException, InterruptedException {
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

}