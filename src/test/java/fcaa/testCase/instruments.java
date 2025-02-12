package fcaa.testCase;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.Instruments;
import fcaa.testComponent.BaseClass;

public class instruments extends BaseClass {

	String startDateStr = "01/01/2024";
	String endDateStr = "31/12/2024";

	// Latest News Object
	MenuBar menuBar;
	Instruments instruments;

	@BeforeMethod
	public void setUpInstru() {
		menuBar = new MenuBar(driver);
		instruments = menuBar.goToInstruments();
	}

	// <-------- Test cases for the Instruments Home Page ----------->//

	// Verify Search functionality
	@Test(priority = 1)
	public void testsearchbox() {
		instruments.searchLegalInstruments();

	}

	// --------------Sorting Functionality--------------//

	// Verify Newest functionality
	@Test(priority = 2)
	public void testNewest() throws InterruptedException, ParseException {
		instruments.clickOnSort();
		instruments.selectOldest();
		List<String> dateBeforeSort = instruments.getDateList();
		instruments.clickOnSort();
		instruments.selectNewest();
		List<String> dateAfterSort = instruments.getDateList();
		instruments.ascendingDates(dateAfterSort);
		Assert.assertEquals(dateAfterSort, dateBeforeSort);
		System.out.println(dateAfterSort);
		System.out.println(dateAfterSort);
	}

	// Verify Oldest functionality
	@Test(priority = 3)
	public void testOldest() throws InterruptedException {
		List<String> dateBeforeSort = instruments.getDateList();
		instruments.clickOnSort();
		instruments.selectOldest();
		List<String> dateAfterSort = instruments.getDateList();
		dateAfterSort = instruments.decendingDates(dateAfterSort);
		Assert.assertEquals(dateAfterSort, dateBeforeSort);
		System.out.println(dateBeforeSort);
		System.out.println(dateAfterSort);
	}

	// Verify Title Ascending
	@Test(priority = 4)
	public void testTitleAscending() throws InterruptedException {
		instruments.clickOnSort();
		instruments.selectDescending();
		List<String> itemsInDescendingOrder = instruments.getTitleList();
		instruments.clickOnSort();
		instruments.selectAscending();
		List<String> itemsInAscendingOrder = instruments.getTitleList();
		System.out.println(itemsInAscendingOrder);
		Collections.reverse(itemsInDescendingOrder);
		System.out.println(itemsInDescendingOrder);
		// Now compare the reversed list with the ascending order list
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));

	}

	// Verify Title descending
	@Test(priority = 5)
	public void testTitleDescending() throws InterruptedException {
		instruments.clickOnSort();
		instruments.selectAscending();
		List<String> itemsInAscendingOrder = instruments.getTitleList();
		instruments.clickOnSort();
		instruments.selectDescending();
		List<String> itemsInDescendingOrder = instruments.getTitleList();
		Collections.reverse(itemsInDescendingOrder);
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));
	}

	// -------------Display Count----------------//

	// Assert that the counted total matches the displayed total
	@Test(priority = 6)
	public void verifyTotalCount() throws InterruptedException {
		int[] count = instruments.verifyDataCount();
		int totalCount = count[0];
		int displayedCount = count[1];
		// System.out.println(displayedCount);
		Assert.assertEquals(totalCount, displayedCount, "Total data count does not match the displayed count!");
	}

	// ---------------Pagination Functionality-------------//

	// Test First Page Button
	@Test(priority = 7)
	private void verifyFirstPageNumber() throws InterruptedException {
		int[] paginationPageNum = instruments.checkFirstPageBtn();
		int expectedPageNumber = 1;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"First page number is not displayed correctly");
		}
	}

	// Test Previous Page Button
	@Test(priority = 8)
	private void verifyPreviousPageNumber() throws InterruptedException {
		int[] paginationPageNum = instruments.checkPreviousPageBtn();
		int expectedPageNumber = 1;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Previous page number is not displayed correctly");
		}
	}

	// Test Next Page Button
	@Test(priority = 9)
	private void verifyNextPageNumber() throws InterruptedException {
		int[] paginationPageNum = instruments.checkNextPageBtn();
		int expectedPageNumber = 2;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}
	}

	// Test Last Page Button
	@Test(priority = 10)
	private void verifyLastPageNumber() throws InterruptedException {
		int[] paginationPageNum = instruments.checkLastPageBtn();
		if (paginationPageNum.length > 0) {
			int expectedPageNumber = paginationPageNum[0];
			// System.out.println(expectedPageNumber);
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}

	}

	// ------------------ Test cases for the left side menu ---------------------//

	// -----------------Instrument Type--------------//

	// Verify Instrument Type data is showing selected type
	@Test
	public void verifyTitleListWithInstrumentType() throws InterruptedException {
		instruments.selectInstrumentType();
		List<String> titleWithInstrumentType = instruments.getTitleListWithInstrumentType();
		// System.out.println(titleWithInstrumentType);
		// Perform the assertion that each title contains the expected 'Non-legal
		// instrument type' tag
		Assert.assertTrue(titleWithInstrumentType.contains("Non-legal"),
				"Title does not contain the expected tag 'Non-legal instrument type'. Found: "
						+ titleWithInstrumentType);
	}

	// -------------------Effective Date--------------//

	// Verify Effective Date radio button
	@Test(priority = 11)
	public void testEffectiveDateRadio() throws InterruptedException {
		instruments.selectEffectiveDateRadio();
		instruments.selectSubmit();
	}

	// Verify Effective date range
	@Test(priority = 12)
	public void testEffectivedateRange() throws ParseException, InterruptedException {
		instruments.putEffectiveDateRange(startDateStr, endDateStr);
		instruments.selectSubmit();
	}

	// Verify Effective Date radio button Unselect functionality after input date
	// range
	@Test(priority = 13)
	public void verifyEffectiveDateRadioButtonUnselect() throws ParseException, InterruptedException {
		instruments.selectEffectiveDateRadio();
		boolean isRadioButtonSelected = instruments.verifyEffectiveDateRadiobuttonSelected(startDateStr, endDateStr);
		Assert.assertFalse(isRadioButtonSelected,
				"Effective Date Radio button should be unselected after entering date range.");
	}

	// Verify Effective date range field must be clear after select radio button
	@Test(priority = 14)
	public void verifyEffectiveDateRangeClear() throws ParseException, InterruptedException {
		boolean isDateRangeCleared = instruments.verifyEffectiveDateRangeFieldCleared(startDateStr, endDateStr);
		Assert.assertTrue(isDateRangeCleared, "Effective Date Start and End date should be cleared");
	}

	// Verify Effective Date Radio button select after click on the text
	@Test(priority = 15)
	public void clickEffactiveDateRadioText() {
		instruments.clickEffactiveDateRadioText();
	}

	// Verify Effective Date data is showing within the given date Range
	@Test
	public void verifyEffectiveDataWithinDateRange() throws ParseException, InterruptedException {
		String resultMessage = instruments.verifyEffectiveDataWithinDateRange(startDateStr, endDateStr);
		Assert.assertTrue(resultMessage.contains("within the date range"), resultMessage);
	}

	// --------------------Published Date---------------//

	// Verify Published Date radio button
	@Test(priority = 16)
	public void testPublishedDateRadio() throws InterruptedException {
		instruments.selectPublishedDateRadio();
		Thread.sleep(3000);
		instruments.selectSubmit();
	}

	// Verify Published date range
	@Test(priority = 17)
	public void testPublisheddateRange() throws ParseException, InterruptedException {
		instruments.putPublishedDateRange(startDateStr, endDateStr);
		instruments.selectSubmit();
	}

	// Verify Published Date radio button unselect functionality after input date range
	@Test(priority = 18)
	public void verifyPublishedDateRadioButtonUnselect() throws ParseException, InterruptedException {
		instruments.selectPublishedDateRadio();
		boolean isRadioButtonSelected = instruments.verifyPublishedDateRadiobuttonSelected(startDateStr, endDateStr);
		Assert.assertFalse(isRadioButtonSelected,
				"Effective Date Radio button should be unselected after entering date range.");
	}

	// Verify Published date range field must be clear after select radio button
	@Test(priority = 19)
	public void verifyPublishedDateRangeClear() throws ParseException, InterruptedException {
		boolean isDateRangeCleared = instruments.verifyPublishedDateRangeFieldCleared(startDateStr, endDateStr);
		Assert.assertTrue(isDateRangeCleared, "Effective Date Start and End date should be cleared");
	}

	// Verify Published Date data is showing within the given date Range
	@Test
	public void verifypublishedDataWithinDateRange() throws ParseException, InterruptedException {
		String resultMessage = instruments.verifyPublishedDataWithinDateRange(startDateStr, endDateStr);
		Assert.assertTrue(resultMessage.contains("within the date range"), resultMessage);
	}

	// ---------------Filter By Module------------//

	// Verify Filter By module
	@Test
	public void verifyFilterByModule() throws InterruptedException {
		instruments.selectFilterByModuleSearchResult();
		instruments.selectSubmit();
	}

}
