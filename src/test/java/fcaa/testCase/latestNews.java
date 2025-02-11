package fcaa.testCase;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.LatestNews;
import fcaa.testComponent.BaseClass;

@Listeners(fcaa.testComponent.ListenerImplementation.class)
public class latestNews extends BaseClass {
	String startDateStr = "20/12/2020";
	String endDateStr = "24/12/2020";

	// Latest News Object
	MenuBar menuBar;
	LatestNews latestNews;

	@BeforeMethod
	public void setUpLatestNews() {
		menuBar = new MenuBar(driver);
		latestNews = menuBar.goToLatestNews();
	}

	// <--------------Test cases for the latest News Home page---------------->

	// Verify Search functionality
	@Test(priority = 1)
	public void testsearchbox() throws InterruptedException {
		String searchPlaceHolder = latestNews.getSearchLatestNews();
		Assert.assertTrue(searchPlaceHolder.equalsIgnoreCase("Search Latest News"));
	}

	// --------------Sorting Functionality--------------//

	// Verify Newest functionality
	@Test(priority = 2)
	public void testNewest() throws InterruptedException, ParseException {
		latestNews.clickOnSort();
		latestNews.selectOldest();
		List<String> dateBeforeSort = latestNews.getDateList();
		latestNews.clickOnSort();
		latestNews.selectNewest();
		List<String> dateAfterSort = latestNews.getDateList();
		latestNews.ascendingDates(dateAfterSort);
		Assert.assertEquals(dateAfterSort, dateBeforeSort);
//			System.out.println(dateAfterSort);
//			System.out.println(dateAfterSort);
	}

	// Verify Oldest functionality
	@Test(priority = 3)
	public void testOldest() throws InterruptedException {
		List<String> dateBeforeSort = latestNews.getDateList();
		latestNews.clickOnSort();
		latestNews.selectOldest();
		List<String> dateAfterSort = latestNews.getDateList();
		latestNews.decendingDates(dateAfterSort);
		Assert.assertEquals(dateAfterSort, dateBeforeSort);
		//System.out.println(dateBeforeSort);
		//System.out.println(dateAfterSort);
	}

	// Verify Title Ascending
	@Test(priority = 4)
	public void testTitleAscending() throws InterruptedException {
		latestNews.clickOnSort();
		latestNews.selectDescending();
		List<String> itemsInDescendingOrder = latestNews.getTitleList();
		latestNews.clickOnSort();
		latestNews.selectAscending();
		List<String> itemsInAscendingOrder = latestNews.getTitleList();
		//System.out.println(itemsInAscendingOrder);
		Collections.reverse(itemsInDescendingOrder);
		//System.out.println(itemsInDescendingOrder);
		// Now compare the reversed list with the ascending order list
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));

	}

	// Verify Title descending
	@Test(priority = 5)
	public void testTitleDescending() throws InterruptedException {
		latestNews.clickOnSort();
		latestNews.selectAscending();
		List<String> itemsInAscendingOrder = latestNews.getTitleList();
		latestNews.clickOnSort();
		latestNews.selectDescending();
		List<String> itemsInDescendingOrder = latestNews.getTitleList();
		Collections.reverse(itemsInDescendingOrder);
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));
	}

	// -------------Display Count----------------//

	// Assert that the counted total matches the displayed total
	@Test(priority = 6)
	public void verifyTotalCount() throws InterruptedException {
		int[] count = latestNews.verifyDataCount();
		int totalCount = count[0];
		int displayedCount = count[1];
		Assert.assertEquals(totalCount, displayedCount, "Total data count does not match the displayed count!");
	}

	// ---------------Pagination Functionality-------------//

	// Test First Page Button
	@Test(priority = 7)
	private void verifyFirstPageNumber() throws InterruptedException {
		int[] paginationPageNum = latestNews.checkFirstPageBtn();
		String expectedPageNumber = "1";
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"First page number is not displayed correctly");
		}
	}

	// Test Previous Page Button
	@Test(priority = 8)
	private void verifyPreviousPageNumber() throws InterruptedException {
		int[] paginationPageNum = latestNews.checkPreviousPageBtn();
		String expectedPageNumber = "1";
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Previous page number is not displayed correctly");
		}
	}

	// Test Next Page Button
	@Test(priority = 9)
	private void verifyNextPageNumber() throws InterruptedException {
		int[] paginationPageNum = latestNews.checkNextPageBtn();
		String expectedPageNumber = "2";
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}
	}

	// Test Last Page Button
	@Test(priority = 10)
	private void verifyLastPageNumber() throws InterruptedException {
		int[] paginationPageNum = latestNews.checkLastPageBtn();
		if (paginationPageNum.length > 0) {
			int expectedPageNumber = paginationPageNum[0];
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}

	}

	// <---------------Test New Tab Text--------------->

	// Test Title Open in the New Tab and Validate New Tab Text
	@Test(priority = 11)
	public void verifyLinkText() throws InterruptedException {
		String actuualText = latestNews.getLinkText();
		String ExpectedText = latestNews.LinkVerification();
		Assert.assertEquals(actuualText, ExpectedText, "The title does not match the expected value!");
	}

	// <---------------Test Go Back Button--------------->

	// Test Go Back button working Properly
	@Test(priority = 12)
	public void verifygoBack() throws InterruptedException {
		String[] urls = latestNews.verifyGoBackBtn();
		String oldUrl = urls[0];
		String currentUrl = urls[1];
		Assert.assertNotEquals("The current URL does not match the expected URL.", oldUrl, currentUrl);
	}

	// <---------------Test PDF file Open--------------->

	// Test Pdf file open in the new Tab
	@Test(priority = 13)
	public void testVerifyOpenPdfInNewTab() throws InterruptedException {
		boolean isPdfOpened = latestNews.verifyOpenPdfInNewTab();
		Assert.assertTrue(isPdfOpened);
	}

	// <------------------ Test cases for the left side menu -------------------->
	
	//<-----------Published Date----------->

	// Verify radio button
	@Test(priority = 14)
	public void testMonthRadio() throws InterruptedException {
		latestNews.selectMonthRadio();
		latestNews.selectSubmit();
	}

	// Verify date range
	@Test(priority = 15)
	public void testdateRange() throws ParseException, InterruptedException {
		latestNews.inputDateRange(startDateStr, endDateStr);
		latestNews.selectSubmit();
	}

	// Verify data is showing within the given date Range
	@Test(priority = 16)
	public void verifyDataWithinDateRange() throws ParseException, InterruptedException {
		boolean allDatesInRange = latestNews.verifyDataWithinDateRange(startDateStr, endDateStr);
		Assert.assertTrue(allDatesInRange, "Not all displayed dates are within the specified range.");
	}

	// Verify radio button unselect functionality after input date range
	@Test(priority = 17)
	public void verifyRadioButtonUnselect() throws ParseException, InterruptedException {
		latestNews.selectMonthRadio();
		boolean isRadioButtonSelected = latestNews.verifyRadiobuttonSelected(startDateStr, endDateStr);
		Assert.assertFalse(isRadioButtonSelected, "Radio button should be unselected after entering date range.");
	}

	// Verify date range field must be clear after select radio button
	@Test(priority = 18)
	public void verifyDateRangeClear() throws ParseException, InterruptedException {
		boolean isDateRangeCleared = latestNews.verifyDateRangeFieldCleared(startDateStr, endDateStr);
		Assert.assertTrue(isDateRangeCleared, "Start and End date should be cleared");
	}
}
