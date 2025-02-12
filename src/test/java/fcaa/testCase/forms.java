package fcaa.testCase;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.opencsv.exceptions.CsvException;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.Forms;
import fcaa.resources.SearchQueryDataProvider;
import fcaa.testComponent.BaseClass;

@Listeners(fcaa.testComponent.ListenerImplementation.class)

public class forms extends BaseClass {

	String startDateStr = "01/01/2024";
	String endDateStr = "31/12/2024";

	// Latest News Object
	MenuBar menuBar;
	Forms forms;

	@BeforeMethod
	public void setUpForms() {
		menuBar = new MenuBar(driver);
		forms = menuBar.goToForms();
	}

	// <------------------ Test cases for the Forms Home Page ------------------>

	// Verify Search functionality
	@Test(priority = 1)
	public void testsearchbox() {
		forms.searchForms();

	}

	// --------------Sorting Functionality--------------//

	// Verify Newest functionality
	@Test(priority = 3)
	public void testNewest() throws InterruptedException, ParseException {
		forms.clickOnSort();
		forms.selectOldest();
		List<String> dateBeforeSort = forms.getDateList();
		forms.clickOnSort();
		forms.selectNewest();
		List<String> dateAfterSort = forms.getDateList();
		forms.ascendingDates(dateAfterSort);
		Assert.assertEquals(dateAfterSort, dateBeforeSort);
		System.out.println(dateAfterSort);
		System.out.println(dateAfterSort);
	}

	// Verify Oldest functionality
	@Test(priority = 2)
	public void testOldest() throws InterruptedException {
		List<String> dateBeforeSort = forms.getDateList();
		forms.clickOnSort();
		forms.selectOldest();
		List<String> dateAfterSort = forms.getDateList();
		dateAfterSort = forms.decendingDates(dateAfterSort);
		Assert.assertEquals(dateAfterSort, dateBeforeSort);
		System.out.println(dateBeforeSort);
		System.out.println(dateAfterSort);
	}

	// Verify Title descending
	@Test(priority = 4)
	public void testTitleDescending() throws InterruptedException {
		forms.clickOnSort();
		forms.selectAscending();
		List<String> itemsInAscendingOrder = forms.getTitleList();
		forms.clickOnSort();
		forms.selectDescending();
		List<String> itemsInDescendingOrder = forms.getTitleList();
		Collections.reverse(itemsInDescendingOrder);
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));
	}

	// Verify Title Ascending
	@Test(priority = 5)
	public void testTitleAscending() throws InterruptedException {
		forms.clickOnSort();
		forms.selectDescending();
		List<String> itemsInDescendingOrder = forms.getTitleList();
		forms.clickOnSort();
		forms.selectAscending();
		List<String> itemsInAscendingOrder = forms.getTitleList();
		System.out.println(itemsInAscendingOrder);
		Collections.reverse(itemsInDescendingOrder);
		System.out.println(itemsInDescendingOrder);
		// Now compare the reversed list with the ascending order list
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));

	}

	// -------------Display Count----------------//

	// Assert that the counted total matches the displayed total
	@Test(priority = 12)
	public void verifyTotalCount() throws InterruptedException {
		int[] count = forms.verifyDataCount();
		int totalCount = count[0];
		int displayedCount = count[1];
		// System.out.println(displayedCount);
		Assert.assertEquals(totalCount, displayedCount, "Total data count does not match the displayed count!");
	}

	// ---------------Pagination Functionality-------------//

	// Test First Page Button
	@Test(priority = 16)
	private void verifyFirstPageNumber() throws InterruptedException {
		int[] paginationPageNum = forms.checkFirstPageBtn();
		int expectedPageNumber = 1;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"First page number is not displayed correctly");
		}
	}

	// Test Previous Page Button
	@Test(priority = 17)
	private void verifyPreviousPageNumber() throws InterruptedException {
		int[] paginationPageNum = forms.checkPreviousPageBtn();
		int expectedPageNumber = 1;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Previous page number is not displayed correctly");
		}
	}

	// Test Next Page Button
	@Test(priority = 18)
	private void verifyNextPageNumber() throws InterruptedException {
		int[] paginationPageNum = forms.checkNextPageBtn();
		int expectedPageNumber = 2;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}
	}

	// Test Last Page Button
	@Test(priority = 19)
	private void verifyLastPageNumber() throws InterruptedException {
		int[] paginationPageNum = forms.checkLastPageBtn();
		if (paginationPageNum.length > 0) {
			int expectedPageNumber = paginationPageNum[0];
			System.out.println(expectedPageNumber);
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}

	}

	// <------------------ Test cases for the left side menu --------------------->

	// -------------------Effective Date--------------//

	// Verify radio button
	@Test(priority = 6)
	public void testMonthRadio() throws InterruptedException {
		forms.selectMonthRadio();
		forms.selectSubmit();
	}

	// Verify date range
	@Test(priority = 8)
	public void testdateRange() throws ParseException, InterruptedException {
		forms.inputDateRange(startDateStr, endDateStr);
		forms.selectSubmit();
	}

	// Verify radio button unselect functionality after input date range
	@Test(priority = 10)
	public void verifyRadioButtonUnselect() throws ParseException, InterruptedException {
		forms.selectMonthRadio();
		boolean isRadioButtonSelected = forms.verifyRadiobuttonSelected(startDateStr, endDateStr);
		Assert.assertFalse(isRadioButtonSelected, "Radio button should be unselected after entering date range.");
	}

	// Verify date range field must be clear after select radio button
	@Test(priority = 11)
	public void verifyDateRangeClear() throws ParseException, InterruptedException {
		boolean isDateRangeCleared = forms.verifyDateRangeFieldCleared(startDateStr, endDateStr);
		Assert.assertTrue(isDateRangeCleared, "Start and End date should be cleared");
	}

	// Verify data is showing within the given date Range
	@Test
	public void verifyDataWithinDateRange() throws ParseException, InterruptedException {
		boolean allDatesInRange = forms.verifyDataWithinDateRange(startDateStr, endDateStr);
		Assert.assertTrue(allDatesInRange, "Not all displayed dates are within the specified range.");
	}

	@Test(dataProvider = "booleanSearchQueries", dataProviderClass = SearchQueryDataProvider.class)
	public void testBooleanSearch(String query) throws InterruptedException, CsvException, IOException {
		forms.performSearch(query);
		forms.changeSortingValues(100);
		boolean isValid = forms.validateResults(query);
		Assert.assertTrue(isValid, "Search results do not match for query: " + query);
	}
}
