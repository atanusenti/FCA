package fcaa.pageObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import fcaa.AbstractComponnent.MenuBar;

public class LatestNews extends MenuBar {
	WebDriver driver;

	public LatestNews(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	By toastMessage = By.xpath("//ul/li[@class='px-0 text-sm ng-star-inserted']");

	@FindBy(xpath = "//input[@placeholder='Search Latest News']")
	WebElement searchLatestNews;

	@FindBy(xpath = "//input[@placeholder='Search Latest News']")
	WebElement searchLatestNewsText;

	// capture the published date
	@FindBy(xpath = "//ul/li[@class='px-0 text-sm ng-star-inserted']")
	List<WebElement> publishedDate;

	// Capture Title
	@FindBy(xpath = "//a[@class='link_color sm:text-lg text-base font-semibold cursor-pointer']")
	List<WebElement> contentTitle;

	// Select Month Radio button
	@FindBy(xpath = "//label[text()='This Month']")
	WebElement selectMonthRadio;

	@FindBy(xpath = "//*[@formcontrolname=\"monthFilter\"]//div[contains(@class, 'p-radiobutton p-component')]")
	WebElement monthRadioBtn;

	// Click on the submit button
	@FindBy(xpath = "(//button[@class='solid_btn flex-1'][normalize-space()='Submit'])[1]")
	WebElement selectSubmit;

	// Enter date range
	@FindBy(xpath = "(//input[@placeholder='DD/MM/YYYY - DD/MM/YYYY'])[1]")
	WebElement putDateRange;

	// Put Date Range
	@FindBy(xpath = "(//p-calendar[@placeholder='DD/MM/YYYY - DD/MM/YYYY'])[1]")
	WebElement putDateRangeCalendar;

	// Displaying Count
	@FindBy(xpath = "(//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1'])[1]")
	WebElement displayCount;

	// Click on the Link
	@FindBy(xpath = "(//a[@class='link_color sm:text-lg text-base font-semibold cursor-pointer'])[1]")
	WebElement clickOnTheLink;

	// Get Link Text from the new tab
	@FindBy(xpath = "//h1[normalize-space()='Final Brexit instruments and TTP directions']")
	WebElement getLinkText;

	// Go Back Button
	@FindBy(xpath = "(//button[@class='back_btn flex gap-2'])[1]")
	WebElement goBackBtn;

	// Click on the PDF
	@FindBy(xpath = "//a[@href='https://www.fca.org.uk/publication/handbook/handbook-notice-83.pdf']")
	WebElement clickOnThePdfLink;

	public void searchLatestNews() {
		searchLatestNews.click();
	}

	public String getSearchLatestNews() {
		// waitForElementToAppear(By.xpath("//input[@placeholder='Search Latest
		// News']"));
		return searchLatestNewsText.getAttribute("placeholder");
	}

	// Get Date List //Get published date List
	public List<String> getDateList() throws InterruptedException {
		List<String> getDateList = getDate(publishedDate);
		return getDateList;
	}

	// Get Title List //Capture Title before sorting
	public List<String> getTitleList() throws InterruptedException {
		List<String> titleList = titleList(contentTitle);
		return titleList;
	}

	// Select Month Radio button
	public void selectMonthRadio() {
		selectMonthRadio.click();
	}

	// Click on the submit button
	public void selectSubmit() {
		selectSubmit.click();
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

	// Verify radio button selected
	public boolean verifyRadiobuttonSelected(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		// Locate the radio button
		return monthRadioBtn.getAttribute("class").contains("p-radiobutton-checked");

	}

	// Input date Range
	public String inputDateRange(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		return dateRangeStr;
	}

	// Verify radio button selected
	public boolean verifyDateRangeFieldCleared(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		selectMonthRadio.click();
		return putDateRangeCalendar.getAttribute("class").contains("ng-invalid");

	}

	public int[] verifyDataCount() throws InterruptedException {
		int[] count = verifyDataCountAcrossPages();
		return count;
	}

	public String getLinkText() {
		String LinkText = clickOnTheLink.getText();
		System.out.println(LinkText);
		return LinkText;

	}

	public String LinkVerification() throws InterruptedException {
		try {
			// Click the link
			clickOnTheLink.click();

			// Switch to the new tab
			switchToTheNewTab();

//            // Wait for the new tab to load
//            Thread.sleep(5000);

			// Fetch title text from the new tab
			String newTabText = getLinkText.getText();

			// Close the new tab
			driver.close();

			// Switch back to the original tab
			driver.switchTo().window(driver.getWindowHandles().iterator().next());

			// Return the fetched text
			return newTabText;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}
	}

	public String[] verifyGoBackBtn() throws InterruptedException {
		try {
			// Click the link
			clickOnTheLink.click();

			// Switch to the new tab
			switchToTheNewTab();

			// Fetch title text from the new tab
			String oldUrl = driver.getCurrentUrl();
			// System.out.println(oldUrl);
			goBackBtn.click();

			String currentUrl = driver.getCurrentUrl();
			// System.out.println(currentUrl);

			// Close the new tab
			driver.close();

			return new String[] { oldUrl, currentUrl };
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { "Error", "Error" };
		}
	}

	public boolean verifyOpenPdfInNewTab() throws InterruptedException {
		try {
			// Click the link
			clickOnTheLink.click();

			// Switch to the new tab
			switchToTheNewTab();

			// Click the link to open the PDF
			clickOnThePdfLink.click();

			// Switch to the new tab
			switchToTheNewTab();

			// Get the current URL
			String currentUrl = driver.getCurrentUrl();
			System.out.println(currentUrl);

			// Check if the URL ends with ".pdf" or if the content type is "application/pdf"
			boolean isPdf = currentUrl.endsWith(".pdf") || driver.getPageSource().contains("application/pdf");

			// Return the result
			return isPdf;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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

}
