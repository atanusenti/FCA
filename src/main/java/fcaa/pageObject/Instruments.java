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

public class Instruments extends MenuBar {

	WebDriver driver;

	public Instruments(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// -----------------Search Legal Instruments-------------------//

	@FindBy(xpath = "//input[@placeholder='Search Legal Instruments']")
	WebElement searchLegalInstruments;

	public void searchLegalInstruments() {
		searchLegalInstruments.click();
	}

	// ----------------Published Date List To Verify Sorting ---------------//
	
	// capture the Published Date date List
	@FindBy(xpath = "//div[contains(@class, 'listing')]//li[b[normalize-space()='Published Date:']]")
	List<WebElement> PublishedDate;

	// Get Date List //Get published date List
	public List<String> getDateList() throws InterruptedException {
		List<String> getDateList = getDate(PublishedDate);
		return getDateList;
	}

	// -------------------Title List to Verify Sorting--------------//
	// Capture Title
	@FindBy(xpath = "//a[@class='link_color sm:text-lg text-base font-semibold cursor-pointer']")
	List<WebElement> contentTitle;

	// Get Title List
	public List<String> getTitleList() throws InterruptedException {
		return titleList(contentTitle);
	}

	// ------------Display Count-------------//

	// Displaying Count
	@FindBy(xpath = "(//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1'])[1]")
	WebElement displayCount;

	public int[] verifyDataCount() throws InterruptedException {
		int[] count = verifyDataCountAcrossPages();
		return count;
	}

	// ------------------- Test Pagination functionality-----------------//

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
	@FindBy(xpath = "//span[text()='Instruments']/../..//div[@class='flex gap-2']//button[@class='solid_btn flex-1'][normalize-space()='Submit']")
	WebElement selectSubmit;

	// Click on the submit button
	public void selectSubmit() {
		scrollToElement(selectSubmit);
		selectSubmit.click();
	}

	// --------------Instrument Type-------------------//

	// Select Instrument Type
	@FindBy(xpath = "//div[contains(@class,'flex flex-column gap-3')]//label[contains(@for,'Non-legal')]")
	WebElement selectInstrumentType;

	public void selectInstrumentType() {
		selectInstrumentType.click();
		scrollToElement(selectSubmit);
		selectSubmit.click();
	}

	// Get Title with Instrument Type
	@FindBy(xpath = "//div[contains(@class,'relative')]//div[contains(@class,'flex-column-reverse md:flex-row flex flex-wrap gap-2 sm:gap-2 header mb-3 gap-2')]//p[contains(@class,'tag text-sm font-bold ng-star-inserted')]")
	List<WebElement> selectTitleWithInstrumentType;

	// Get Title with Instrument Type
	public List<String> getTitleListWithInstrumentType() throws InterruptedException {
		return titleList(selectTitleWithInstrumentType);
	}

	// ----------------------Effective Date--------------------------//

	// Select Effective Date Radio button
	@FindBy(xpath = "//p-radiobutton[@inputid='instrumentEffectiveDate4']")
	WebElement selectEffectiveDateRadio;

	// Select Effective Date Radio button
	public void selectEffectiveDateRadio() {
		scrollToElement(selectEffectiveDateRadio);
		selectEffectiveDateRadio.click();
	}

	// Enter Effective date range
	@FindBy(xpath = "//input[@name='effectiveDate']")
	WebElement putEffectiveDateRange;

	// Input Effective date Range
	public String putEffectiveDateRange(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putEffectiveDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		return dateRangeStr;
	}

	// Verify Effective Date radio button selected
	public boolean verifyEffectiveDateRadiobuttonSelected(String startDateStr, String endDateStr)
			throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putEffectiveDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		// Locate the radio button
		return selectEffectiveDateRadio.getAttribute("class").contains("p-radiobutton-checked");

	}

	// Effective date range
	@FindBy(xpath = "//p-calendar[@formcontrolname='effectiveDate']")
	WebElement EffectiveDateRange;

	// Verify Effective Date radio button selected
	public boolean verifyEffectiveDateRangeFieldCleared(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putEffectiveDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		selectEffectiveDateRadio.click();
		return EffectiveDateRange.getAttribute("class").contains("ng-invalid");
	}

	// Select Effective Date Radio Button with Label
	@FindBy(xpath = "//label[@for='instrumentEffectiveDate4']")
	WebElement clickEffactiveDateRadioText;

	// Select Effective Date Radio with the help of lebel Text
	public boolean clickEffactiveDateRadioText() {

		scrollToElement(clickEffactiveDateRadioText);
		clickEffactiveDateRadioText.click();
		return clickEffactiveDateRadioText.getAttribute("class").contains("p-highlight");
	}

	// Effective Date Range Data
	public String verifyEffectiveDataWithinDateRange(String startDateStr, String endDateStr)
			throws ParseException, InterruptedException {
		// Define date range
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = sdf.parse(startDateStr);
		Date endDate = sdf.parse(endDateStr);

		// Input the date range
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putEffectiveDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		scrollToElement(selectSubmit);
		// Click on the submit button
		selectSubmit.click();

		List<String> dateList = getDateList();

		if (dateList.isEmpty()) {
			return "No result found";
		}

		boolean allDatesInRange = true;
		for (String date : dateList) {
			Date dataDate = sdf.parse(date);

			// Assert that data is within the range
			if (dataDate.before(startDate) || dataDate.after(endDate)) {
				allDatesInRange = false; // Set flag to false if any date is out of range
				break; // Exit the loop early if we find a date out of range
			}
		}

		return allDatesInRange ? "Data is within the date range: " + dateRangeStr : "Data is not within the date range";
	}

	// ------------------------Published Date--------------------//

	// Select Published Date Radio button
	@FindBy(xpath = "//p-radiobutton[@inputid='instrumentPublished3']")
	WebElement selectPublishedDateRadio;

	// Published Date
	// Select Published Date Radio button
	public void selectPublishedDateRadio() {
		waitForWebElementToAppear(selectPublishedDateRadio);
		scrollToElement(selectPublishedDateRadio);
		selectPublishedDateRadio.click();
	}

	// Enter Published date range
	@FindBy(xpath = "//input[@name='publishedDate']")
	WebElement putPublishedDateRange;

	// Input Published date Range
	public String putPublishedDateRange(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		waitForWebElementToAppear(putPublishedDateRange);
		scrollToElement(putPublishedDateRange);
		putPublishedDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		return dateRangeStr;
	}

	// Verify Published Date radio button selected
	public boolean verifyPublishedDateRadiobuttonSelected(String startDateStr, String endDateStr)
			throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putPublishedDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		// Locate the radio button
		return selectPublishedDateRadio.getAttribute("class").contains("p-radiobutton-checked");

	}

	// Published date range
	@FindBy(xpath = "//p-calendar[@formcontrolname='publishedDate']")
	WebElement PublishedDateRange;

	// Verify published Date radio button selected
	public boolean verifyPublishedDateRangeFieldCleared(String startDateStr, String endDateStr) throws ParseException {
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putPublishedDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		selectPublishedDateRadio.click();
		return PublishedDateRange.getAttribute("class").contains("ng-invalid");
	}

	// Published Date Range Data

	public String verifyPublishedDataWithinDateRange(String startDateStr, String endDateStr)
			throws ParseException, InterruptedException {
		// Define date range
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = sdf.parse(startDateStr);
		Date endDate = sdf.parse(endDateStr);

		// Input the date range
		String dateRangeStr = startDateStr + " - " + endDateStr;
		putPublishedDateRange.sendKeys(dateRangeStr);
		displayCount.click();
		scrollToElement(selectSubmit);
		// Click on the submit button
		selectSubmit.click();

		List<String> dateList = getDateList();
		System.out.println(dateList);
		if (dateList.isEmpty()) {
			return "No result found";
		}

		boolean allDatesInRange = true;
		for (String date : dateList) {
			Date dataDate = sdf.parse(date);

			// Assert that data is within the range
			if (dataDate.before(startDate) || dataDate.after(endDate)) {
				allDatesInRange = false; // Set flag to false if any date is out of range
				break; // Exit the loop early if we find a date out of range
			}
		}

		return allDatesInRange ? "Data is within the date range: " + dateRangeStr : "Data is not within the date range";
	}

	// -----------------------Filter By Module--------------------//

	// Filter by module
	@FindBy(xpath = "//input[@id='instrumentFilterbyModule']")
	WebElement filterByModule;

	@FindBy(xpath = "//div[contains(@class,'p-autocomplete-panel p-component p-ripple-disabled ng-tns-c3756965041-6')]//ul[contains(@aria-label,'Option List')]")
	List<WebElement> selectOptionFromFilterByModule;

	// Select option From Filter By Module
	public Object selectOptionFromFilterByModule() {

		return selectOptionFromFilterByModule();
	}

	public void selectFilterByModuleSearchResult() {
		try {
			// Scroll to the element
			scrollToElement(filterByModule);

			// Type the search text
			filterByModule.sendKeys("fc");

			Thread.sleep(3000);

			// Iterate over the results and select the one you want
			for (WebElement result : selectOptionFromFilterByModule) {
				String resultText = result.getText();
				if (resultText.contains("FCA Handbook")) {
					result.click();
					break;
				}
			}
		} catch (Exception e) {
			// Handle exception
			e.printStackTrace();
		}
	}

	// Get Title with Sourcebook
	@FindBy(xpath = "//div[contains(@class,'relative')]//div[contains(@class,'flex-column-reverse md:flex-row flex flex-wrap gap-2 sm:gap-2 header mb-3 gap-2')]//p[contains(@class,'tag text-sm font-bold ng-star-inserted')]")
	List<WebElement> selectTitleWithSourceBook;

	// Get Title with Sourcebook
	public List<String> getTitleListWithSourceBook() throws InterruptedException {
		return titleList(selectTitleWithSourceBook);
	}

	// --------------------------------------------//
	// No Data found Text
	@FindBy(xpath = "//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1']")
	WebElement noResultFound;

	public void noResultFound() {
		noResultFound.getText();
	}
}
