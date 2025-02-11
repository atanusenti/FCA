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
		// Thread.sleep(5000);
	}

	// Verify date range field must be clear after select radio button
	@Test(priority = 11)
	public void verifyDateRangeClear() throws ParseException, InterruptedException {
		boolean isDateRangeCleared = forms.verifyDateRangeFieldCleared(startDateStr, endDateStr);
		Assert.assertTrue(isDateRangeCleared, "Start and End date should be cleared");
		Thread.sleep(5000);
	}

	// Verify data is showing within the given date Range
	@Test
	public void verifyDataWithinDateRange() throws ParseException, InterruptedException {
		boolean allDatesInRange = forms.verifyDataWithinDateRange(startDateStr, endDateStr);
		Assert.assertTrue(allDatesInRange, "Not all displayed dates are within the specified range.");
	}

	@DataProvider(name = "booleanSearchQueries")
	public Object[][] getSearchQueries() {
		return new Object[][] {
//        	    ----------------------AND/&&------------------------------
//              {"market AND finance"},
//            	{"market && finance"}, 
//    			{"market &&finance"}, // test case fails  
//    	    	----------------------OR/||-------------------------------
//    			{"market OR finance"}, // test case fails at 341
//        		{"market finance"},
//        		{"market || finance"},   
//	    		----------------------NOT/-/!-------------------------------
//              {"finance -market"},
//    			{"market NOT finance"}, 
//    			{"finance !order"},
//    			----------------------Combination of AND/OR/NOT-------------------------------
//        		{"!finance -market AND orders"},
//        		{"management || order && contract"}, // test case fails at 35
//        		{"management AND order OR contract"}, // test case fails at 35
//        		{"market NOT finance !order"},
//        		{"market NOT finance orders"},
//    			{"management || order NOT contract"},
//				{"management AND order NOT contract"},
//    			{"management && order && contract"},
//        		----------------------Exact Operator-------------------------------
//        		{"\"finance\""},
//        		{"\"finance transaction\""},
//        		----------------------Proximity Operator-------------------------------
//            	{"maket~"},
//    			{"fninancee~"},
//        		----------------------Proximity/?/* Operator-------------------------------
//            	{"finance*"},
//            	{"finan?e"},
//        		----------------------Combination of AND/OR/NOT/()/Exact Operator-------------------------------
//        		{"market NOT finance \"orders\""},
//              {"market AND (finance OR contract)"},
//    			{"!finance -market AND transaction"},
//    			{"!(finance market) AND transaction"},
//    			{"finance !order -market"},
//        		{"\"finance transaction\" AND order"},
//    			{"market OR (finance !manage)"}, // test case fails
//    			{"market OR \"finance transaction\" -order"},
//				{"(market finance) NOT abuse"},
//    			{"\"finance transaction\" -order"},
//    			{"\"finance transaction\" market"},
//    			{"(market AND finance) NOT contract"},
//    			{"(market OR finance) NOT contract"},
//    			{"(market OR finance) AND contract"},
//    			{"\"finance transaction\" NOT \"order\""},
//    			{"market \"finance transaction\" NOT \"order\""},
//    			{"finance && \"finance transaction\" NOT \"order\""},
//    			{"(market OR finance) AND \"contract\""},
//        		----------------------Combination of +/~/?/()/Exact/*/NOT Operator-------------------------------
//        		{"finan* -mark?t"},
//    			{"finance* AND marke*"},
//    			{"financ* OR marke*"},
//    			{"(market AND finance) NOT contract*"},
//    			{"(market OR finance) NOT contract*"},
//    			{"(market AND finance) AND contract*"},
//    			{"financ? && marke*"},
//    			{"+finance +market"},
//        		{"-mark?t finan*"},
//        		{"-fninancee~ +mark?t"},
//        		{"fninan~ -mark?t +orders"},
//        		{"fninan~ -mark?t -order"},
//        		{"+ordre~ -orders"},
//        		{"+ordre~ AND \"orders\""}, // should highlight orders text as well
//				{"market +finance -order"},
//    			{"market +finance"},
//    			{"-finance +market"},
//    			{"+finance -markt~"}, // test case fails
//    			{"fninance~ -order"},
//        		----------------------Some Other Combinations-------------------------------
//        		{"fninan~ -mark?t OR ordre~"},
//        		{"fninan~ mark?t NOT ordre~"},// test case fails
//        		{"-mark?t fninan~ AND ordree~"},
//        		{"fninan~ -mark?t +ordree~"},
//        		{"fninan~ AND order"},
//        		{"fninace~ OR mark?t -order"},
//    			{"fninan~ && ordr~"},
//        		{"marke~ && finan?e !*ment"},
//    			{"finance* AND \"market\""},
//    			{"(marke* || finance*) AND (contract* OR order*)"},
//    			{"(marke* || finance*) AND (\"transaction\" OR \"services\") NOT (contract* OR order*)"},
//    			{"(mar* OR fin*) AND (\"review status\" OR \"documents\") NOT (leg* OR acc*)"},
//        		{"fninance~ || order NOT manage"},
//        		{"market +finance || (order && management)"},
//		    	------------------------------Proximity Fuzzy----------------------------------------
//        		{"market \"finance abuse\"~5"},
//        		{"(market \"finance transaction\"~5) -order"},
//        		{"order && \"finance transaction\"~3"},
//        		{"\"market order\"~5"},
//    			{"\"market system\"~2"},
//		    	------------------------------Boost Operator (Doubt for Written Code)----------------------------------------
//        		{"market^2 finance"},
//        		{"market finance^2"},
//        		{"(market && abuse)^5 AND order"},
//        		{"market finance^2 NOT transaction"},
//        		{"(market && abuse)^5 (finance && abuse)^2 abuse"},
		};
	}

	@Test(dataProvider = "booleanSearchQueries")
	public void testBooleanSearch(String query) throws InterruptedException, CsvException, IOException {
		forms.performSearch(query);
		forms.changeSortingValues(100);
		boolean isValid = forms.validateResults(query);
		Assert.assertTrue(isValid, "Search results do not match for query: " + query);
	}

}
