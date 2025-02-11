package fcaa.AbstractComponnent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.google.common.base.Supplier;

public class Utility {

	WebDriver driver;
	WebDriverWait wait;

	public Utility(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// <-------------------Method For The Page Scroll----------------->//

	// Scroll to the bottom of the page
	public void scrollToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
	}

	// Scroll to the specific element
	public void scrollToElement(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		waitForWebElementToAppear(element);
	}

	// <-------------------Method For Handle Switch New Tab-------------------->//

	// Switch to the new tab

	public void switchToTheNewTab() {
		// Get all window handles
		Set<String> windowHandles = driver.getWindowHandles();
		Iterator<String> iterator = windowHandles.iterator();

		// Get the original window handle
		String originalWindow = driver.getWindowHandle();

		// Iterate through the window handles
		while (iterator.hasNext()) {
			String handle = iterator.next();
			if (!handle.equals(originalWindow)) {
				// Switch to the new tab
				driver.switchTo().window(handle);

			}
		}
	}

	// <-----------------Method For The Wait------------------>//

	// Wait Functionality

	// Wait for Element Visible
	public void waitForElementToAppear(By findBy) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(findBy));
	}

	// Wait for Element To Appear
	public void waitForWebElementToAppear(WebElement findBy) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOf(findBy));
	}

	// Helper method to wait for an element to be clickable
	public WebElement waitForLocatorToBeClickable(By locator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Timeout duration set to 10 seconds
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	// Helper method to wait for an element to be clickable
	public void waitForElementToBeClickable(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Timeout duration set to 10 seconds

		if (!element.isDisplayed()) {
			wait.until(ExpectedConditions.elementToBeClickable(element));
		}
	}

	// Helper method to wait for an element to be visible
	public WebElement waitForLocatorToBeVisible(By locator) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Timeout duration set to 10 seconds
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// Helper method to wait for an element to be visible
	public WebElement waitForElementToBeVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Timeout duration set to 10 seconds
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	// <-----------------WebElements For The Pagination------------->

	// First Page Button WebElement
	@FindBy(css = "button[aria-label='First Page']")
	WebElement firstPageButton;

	// Previous Page Button WebElement
	@FindBy(css = "button[aria-label='Previous Page']")
	WebElement previousPageButton;

	// Next Page Button WebElement
	@FindBy(css = "button[aria-label='Next Page']")
	WebElement nextPageButton;

	// Last Page Button WebElement
	@FindBy(css = "button[aria-label='Last Page']")
	WebElement lastPageButton;

	// Page Number WebElement
	@FindBy(xpath = "//span[@class='p-paginator-pages ng-star-inserted']//button[contains(@class, 'p-highlight')]")
	WebElement pageNumber;

	// Pagination Div
	@FindBy(xpath = "//anglerighticon[@class='p-element p-icon-wrapper ng-star-inserted']")
	WebElement pageinationDiv;

	// <---------------Method For Return Date List After Trim-------------->//

	// Date Trim //Return date List from the date List String

	public List<String> getDate(List<WebElement> element) throws InterruptedException {
		List<String> dateList = new ArrayList<>();

		changeSortingValues(100);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		while (!element.isEmpty()) {
			// Collect the date texts from the current page
			dateList.addAll(element.stream().map(n -> n.getText().split(":")[1].trim()).collect(Collectors.toList()));
			scrollToElement(nextPageButton);
			Thread.sleep(3000);
			if (nextPageButton.isEnabled() && nextPageButton.isDisplayed()) {
				nextPageButton.click();
			}

			else {
				Reporter.log("Next Page button is disabled", true);
				break; // Exit the loop if the button is not usable
			}
		}
		return dateList;

	}

	// <------------Method For Rearrange Date List in the Ascending Order---------->

	// Date Sort in ascending Order //Smallest to Largest

	public List<String> ascendingDates(List<String> dateList) {
		Collections.sort(dateList, new Comparator<String>() {
			DateFormat date = new SimpleDateFormat("dd/MM/yyyy");

			@Override
			public int compare(String d1, String d2) {
				try {
					return date.parse(d1).compareTo(date.parse(d2));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
		return dateList;

	}

	// <----------Method For Rearrange Date List in the Descending Order---------->

	// Date Sort DecendingOrder //Largest to Smallest

	public List<String> decendingDates(List<String> dateList) {
		Collections.sort(dateList, new Comparator<String>() {
			DateFormat date = new SimpleDateFormat("dd/MM/yyyy");

			@Override
			public int compare(String d1, String d2) {

				try {
					return date.parse(d2).compareTo(date.parse(d1));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}

			}

		});
		return dateList;

	}

	// <--------------Method For Get Title List ------------------>

	// Method to get titles from WebElements
	public List<String> titleList(List<WebElement> elements) throws InterruptedException {
		List<String> titleList = new ArrayList<>();
		changeSortingValues(100);

		// Loop through elements until no more pages
		while (!elements.isEmpty()) {
			// Collect titles from the current page
			titleList.addAll(elements.stream().map(WebElement::getText).collect(Collectors.toList()));

			// Scroll to the next page button
			scrollToElement(nextPageButton);
			Thread.sleep(7000); // Waiting for the page to load

			// Wait for the next page button to appear
			waitForWebElementToAppear(nextPageButton);

			// Check if the next page button is enabled and displayed, then click
			if (nextPageButton.isEnabled() && nextPageButton.isDisplayed()) {
				nextPageButton.click();
			} else {
				Reporter.log("Next Page button is disabled or not found", true);
				break; // Exit loop if the next page button is disabled or doesn't exist
			}
		}

		return titleList; // Return collected titles
	}

	// <------------Method For The Handle Pagenation Functionality------------>//

	// Check Pagination Functionality

	// First Page button functionality
	public int[] clickFirstPageButton() throws InterruptedException {
		Thread.sleep(5000);
		scrollToElement(firstPageButton);
		Thread.sleep(3000);
		clickNextPageButton();
		// Check if the "First Page" button is enabled
		if (firstPageButton.isEnabled()) {
			// Click the "First Page" button
			scrollToElement(firstPageButton);
			firstPageButton.click();
			// Convert the page number text to an integer array
			// Thread.sleep(7000);
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getAttribute("aria-label")) };
			return paginationPageNum;
		} else {
			Reporter.log("First Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	// Previous Page button functionality
	public int[] clickPreviousPageButton() throws InterruptedException {
		Thread.sleep(5000);
		scrollToElement(firstPageButton);
		Thread.sleep(3000);
		clickNextPageButton();

		// Check if the "Previous Page" button is enabled
		if (previousPageButton.isEnabled()) {
			scrollToElement(firstPageButton);
			// Click the "Previous Page" button
			previousPageButton.click();
			// Convert the page number text to an integer array
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getAttribute("aria-label")) };
			return paginationPageNum;

		} else {
			Reporter.log("Previous Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	// Next Page button functionality
	public int[] clickNextPageButton() throws InterruptedException {
		Thread.sleep(5000);
		scrollToElement(firstPageButton);
		Thread.sleep(3000);
		// Check if the "Next Page" button is enabled
		if (nextPageButton.isEnabled()) {
			// Click the "Next Page" button
			nextPageButton.click();
			Thread.sleep(3000);
			// Convert the page number text to an integer array
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getAttribute("aria-label")) };
			return paginationPageNum;

		} else {
			Reporter.log("Next Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	public boolean goToNextPageAndIsNextPageAvailable() throws InterruptedException {
		if (nextPageButton.isDisplayed() && nextPageButton.isEnabled()) {
			scrollToElement(nextPageButton);
			Thread.sleep(1000);
			nextPageButton.click();
			return true;
		} else {
			return false;
		}
	}

	// Last Page button functionality
	public int[] clickLastPageButton() throws InterruptedException {
		Thread.sleep(5000);
		scrollToElement(firstPageButton);
		Thread.sleep(3000);
		// Check if the "Last Page" button is enabled
		if (lastPageButton.isEnabled()) {
			// Click the "Last Page" button
			lastPageButton.click();
			Thread.sleep(5000);
			scrollToElement(firstPageButton);
			Thread.sleep(3000);

			// Check if the "Last Page" button is disabled after clicking
			if (!lastPageButton.isEnabled()) {
				// Convert the page number text to an integer array
				int[] paginationPageNum = { Integer.parseInt(pageNumber.getAttribute("aria-label")) };
				return paginationPageNum;
			} else {
				Reporter.log("Last Page button is still enabled", true);
				return new int[0]; // Return an empty array in case of failure
			}
		} else {
			Reporter.log("Last Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	// changing the sort value
	public void changeSortingValues(int sortValue) {
		if (getTotalCountText() >= 20) {
			try {
				WebElement sortDropdown = waitForLocatorToBeClickable(By.xpath(
						"//div[contains(@class,'paginator_wrap')]//div[contains(@aria-label,'dropdown trigger')]"));

				scrollToElement(sortDropdown);
				Thread.sleep(2000);
				waitForElementToBeVisible(sortDropdown);
				sortDropdown.click();

				WebElement sortOption = waitForLocatorToBeVisible(By.xpath("//li[@aria-label='" + sortValue + "']"));
				sortOption.click();

			} catch (Exception e) {
				System.out.println("Error during sorting: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public int getTotalCountText() {
		String text = displayCount.getText(); // Get full text
		String totalCountStr = text.split("of")[1].trim().split(" ")[0]; // Extract number after "of"
		int totalCount = Integer.parseInt(totalCountStr);
		return totalCount;
	}

	// Sorting functionality

	// Click on the Sort button
	@FindBy(xpath = "//*[@formcontrolname='sortingValue']//div[@aria-label='dropdown trigger']")
	WebElement clickOnSort;

	// Select Oldest from the sorting List
	@FindBy(xpath = "//span[normalize-space()='Oldest']")
	WebElement selectOldest;

	// Select Newest from the sorting List
	@FindBy(xpath = "//span[normalize-space()='Newest']")
	WebElement selectNewest;

	// Select Title Ascending from the sorting List
	@FindBy(xpath = "//span[normalize-space()='Title Ascending']")
	WebElement selectAscending;

	// Select Title Descending from the sorting List
	@FindBy(xpath = "//span[normalize-space()='Title Descending']")
	WebElement selectDescending;

	// Click on the Sort button
	public void clickOnSort() throws InterruptedException {
		scrollToElement(clickOnSort);
		clickOnSort.click();
	}

	// Select Oldest from the sorting List
	public void selectOldest() {
		selectOldest.click();
	}

	// Select Newest from the sorting List
	public void selectNewest() {
		selectNewest.click();
	}

	// Select Title Ascending from the sorting List
	public void selectAscending() {
		selectAscending.click();
	}

	// Select Title descending from the sorting List
	public void selectDescending() {
		selectDescending.click();
	}

	// Method to sort titles Ascending
	public List<String> testAscendingList(List<String> titleList) {
		List<String> quotedTitles = new ArrayList<>();
		List<String> specialCharTitles = new ArrayList<>();
		List<String> dateTitles = new ArrayList<>();
		List<String> regularTitles = new ArrayList<>();

		// Regular expression for date in the format dd/mm/yyyy
		Pattern datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		for (String title : titleList) {
			// Check if title is enclosed in double quotes
			if (title.startsWith("\"") && title.endsWith("\"")) {
				quotedTitles.add(title);
			}
			// Check if title starts with a special character
			else if (!title.isEmpty() && !Character.isLetterOrDigit(title.charAt(0))) {
				specialCharTitles.add(title);
			}
			// Check if title contains a date (dd/mm/yyyy)
			else if (datePattern.matcher(title).find()) {
				dateTitles.add(title);
			}
			// Otherwise, classify as a regular title
			else {
				regularTitles.add(title);
			}
		}

		// Sort each category
		Collections.sort(quotedTitles);
		Collections.sort(specialCharTitles);
		Collections.sort(dateTitles, (o1, o2) -> {
			try {
				Date date1 = dateFormat.parse(o1);
				Date date2 = dateFormat.parse(o2);
				return date1.compareTo(date2);
			} catch (ParseException e) {
				return 0; // In case of parse error, leave unchanged
			}
		});
		Collections.sort(regularTitles);

		// Combine sorted lists
		List<String> sortedTitles = new ArrayList<>();
		sortedTitles.addAll(quotedTitles);
		sortedTitles.addAll(specialCharTitles);
		sortedTitles.addAll(dateTitles);
		sortedTitles.addAll(regularTitles);

		return sortedTitles;
	}

	// Method to sort titles Descending
	public List<String> testDescendingList(List<String> titleList) {
		List<String> quotedTitles = new ArrayList<>();
		List<String> specialCharTitles = new ArrayList<>();
		List<String> dateTitles = new ArrayList<>();
		List<String> regularTitles = new ArrayList<>();

		// Regular expression for date in the format dd/mm/yyyy
		Pattern datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		for (String title : titleList) {
			// Check if title is enclosed in double quotes
			if (!title.isEmpty() && title.startsWith("\"") && title.endsWith("\"")) {
				quotedTitles.add(title);
			}
			// Check if title contains a date (dd/mm/yyyy)
			else if (datePattern.matcher(title).find()) {
				dateTitles.add(title);
			}
			// Check if title starts with a special character (non-alphanumeric character)
			else if (!title.isEmpty() && !Character.isLetterOrDigit(title.charAt(0))) {
				specialCharTitles.add(title);
			}
			// Otherwise, classify as a regular title
			else {
				regularTitles.add(title);
			}
		}

		// Sort each category
		Collections.sort(regularTitles, Collections.reverseOrder());
		Collections.sort(dateTitles, (o1, o2) -> {
			try {
				Date date1 = dateFormat.parse(o1);
				Date date2 = dateFormat.parse(o2);
				return date1.compareTo(date2);
			} catch (ParseException e) {
				return 0; // In case of parse error, leave unchanged
			}
		});
		Collections.sort(specialCharTitles, Collections.reverseOrder());
		Collections.sort(quotedTitles, Collections.reverseOrder());

		// Combine sorted lists
		List<String> sortedTitles = new ArrayList<>();
		sortedTitles.addAll(regularTitles);
		sortedTitles.addAll(dateTitles);
		sortedTitles.addAll(specialCharTitles);
		sortedTitles.addAll(quotedTitles);

		return sortedTitles;
	}

	// Method to get the total number of documents
	public int getTotalDocuments(WebElement element) {
		int totalDocCount = Integer.parseInt(element.getText().split(" ")[5]);
		return totalDocCount;

	}

	// Displaying Count //Total Document count on the Page
	@FindBy(xpath = "//div[contains(@class, 'toolbar') and contains(text(), 'Displaying')]")
	WebElement displayCount;

	// Fetch Total Data from the current Page
	@FindBy(xpath = "//div[@class='p-card-body']")
	List<WebElement> getDataCurrentPage;

	// Fetch data count from the all web Pages
	public int[] verifyDataCountAcrossPages() throws InterruptedException {

		int totalCount = 0;
		boolean hasNextPage = true;
		// Fetch the displayed total count (adjust selector as necessary)
		int displayedCount = getTotalDocuments(displayCount);
		changeSortingValues(100);
		while (hasNextPage) {
			// Fetch data from the current page
			totalCount += getDataCurrentPage.size(); // Count items on the current page
			// Check for the next page button and navigate to it

			try {
				scrollToElement(nextPageButton);
				Thread.sleep(3000);
				waitForWebElementToAppear(nextPageButton);
				if (nextPageButton.isDisplayed() && nextPageButton.isEnabled()) {
					nextPageButton.click();
				} else {
					hasNextPage = false; // No more pages
				}
			} catch (Exception e) {
				hasNextPage = false; // No next page button found
			}
		}
		return new int[] { totalCount, displayedCount };

	}

}
