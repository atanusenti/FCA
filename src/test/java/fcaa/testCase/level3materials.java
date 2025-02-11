package fcaa.testCase;

import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.Level3Materials;
import fcaa.testComponent.BaseClass;

public class level3materials extends BaseClass {

	// Latest News Object
	MenuBar menuBar;
	Level3Materials level3materials;

	@BeforeMethod
	public void setUpLevel3Materials() {
		menuBar = new MenuBar(driver);
		level3materials = menuBar.goToLevel3Materials();
	}

	// <-------- Test cases for the Level 3 Materials Home Page ----------->//

	// --------------Sorting Functionality--------------//

	// Verify Title Ascending
	@Test(priority = 5)
	public void testTitleAscending() throws InterruptedException {
		level3materials.clickOnSort();
		level3materials.selectDescending();
		List<String> itemsInDescendingOrder = level3materials.getTitleList();
		level3materials.clickOnSort();
		level3materials.selectAscending();
		List<String> itemsInAscendingOrder = level3materials.getTitleList();
		System.out.println(itemsInAscendingOrder);
		Collections.reverse(itemsInDescendingOrder);
		System.out.println(itemsInDescendingOrder);
		// Now compare the reversed list with the ascending order list
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));

	}

	// Verify Title descending
	@Test(priority = 4)
	public void testTitleDescending() throws InterruptedException {
		List<String> itemsInAscendingOrder = level3materials.getTitleList();
		System.out.println(itemsInAscendingOrder);
		level3materials.clickOnSort();
		level3materials.selectDescending();
		List<String> itemsInDescendingOrder = level3materials.getTitleList();
		System.out.println(itemsInDescendingOrder);
		Collections.reverse(itemsInDescendingOrder);
		System.out.println(itemsInDescendingOrder);
		Assert.assertTrue(itemsInAscendingOrder.equals(itemsInDescendingOrder));
	}

	// -------------Display Count----------------//

	// Assert that the counted total matches the displayed total
	@Test(priority = 12)
	public void verifyTotalCount() throws InterruptedException {
		int[] count = level3materials.verifyDataCount();
		int totalCount = count[0];
		int displayedCount = count[1];
		// System.out.println(displayedCount);
		Assert.assertEquals(totalCount, displayedCount, "Total data count does not match the displayed count!");
	}

	// ---------------Pagination Functionality-------------//

	// Test First Page Button
	@Test(priority = 16)
	private void verifyFirstPageNumber() throws InterruptedException {
		int[] paginationPageNum = level3materials.checkFirstPageBtn();
		int expectedPageNumber = 1;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"First page number is not displayed correctly");
		}
	}

	// Test Previous Page Button
	@Test(priority = 17)
	private void verifyPreviousPageNumber() throws InterruptedException {
		int[] paginationPageNum = level3materials.checkPreviousPageBtn();
		int expectedPageNumber = 1;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Previous page number is not displayed correctly");
		}
	}

	// Test Next Page Button
	@Test(priority = 18)
	private void verifyNextPageNumber() throws InterruptedException {
		int[] paginationPageNum = level3materials.checkNextPageBtn();
		int expectedPageNumber = 2;
		if (paginationPageNum.length > 0) {
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}
	}

	// Test Last Page Button
	@Test(priority = 19)
	private void verifyLastPageNumber() throws InterruptedException {
		int[] paginationPageNum = level3materials.checkLastPageBtn();
		if (paginationPageNum.length > 0) {
			int expectedPageNumber = paginationPageNum[0];
			System.out.println(expectedPageNumber);
			Assert.assertEquals(paginationPageNum[0], expectedPageNumber,
					"Next page number is not displayed correctly");
		}

	}

}
