package fcaa.AbstractComponnent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class Utility {

	WebDriver driver;
	WebDriverWait wait;

	public Utility(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// Sorting functionality

	// Click on the Sort button
	@FindBy(xpath = "//*[@formcontrolname='sortingValue']//div[@aria-label='dropdown trigger']")
	WebElement clickOnSort;

	// Select Oldest from the sorting List
	@FindBy(xpath = "(//span[normalize-space()='Oldest'])[1]")
	WebElement selectOldest;

	// Select Newest from the sorting List
	@FindBy(xpath = "(//span[normalize-space()='Newest'])[1]")
	WebElement selectNewest;

	// Select Title Ascending from the sorting List
	@FindBy(xpath = "(//span[normalize-space()='Title Ascending'])[1]")
	WebElement selectascending;

	// Select Title Descending from the sorting List
	@FindBy(xpath = "(//span[normalize-space()='Title Descending'])[1]")
	WebElement selectdescending;

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
	public void selectascending() {
		selectascending.click();
	}

	// Select Title descending from the sorting List
	public void selectdescending() {
		selectdescending.click();
	}

	// Method to handle sorting values
	public void changeSortingValues() throws InterruptedException {

		WebElement sortDropdown = driver.findElement(By.xpath("(//div[@class='p-dropdown-trigger'])[2]"));
		scrollToElement(sortDropdown);
		Thread.sleep(3000);
		sortDropdown.click();

		WebElement sortOption = driver.findElement(By.xpath("//li[@aria-label='100']"));
		sortOption.click();
	}

	// Date Strim //Return date List from the date List String
	public List<String> getDate(List<WebElement> element) throws InterruptedException {
		List<String> dateList = new ArrayList<>();
		changeSortingValues();
		while (!element.isEmpty()) {
			dateList.addAll(element.stream().map(n -> n.getText().split(":")[1].trim()).collect(Collectors.toList()));
			scrollToElement(nextPageButton);
			Thread.sleep(3000);
			waitForWebElementToAppear(nextPageButton);
			if (nextPageButton.isEnabled() && nextPageButton.isDisplayed()) {
				nextPageButton.click();
			}

			else {
				Reporter.log("Next Page button is disabled");
				break;
			}
		}
		return dateList;

	}

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

	// Title Stream //Return Title List from the titleList string
	// Method to get titles from WebElements
	public List<String> titleList(List<WebElement> elements) throws InterruptedException {
		List<String> titleList = new ArrayList<>();
		changeSortingValues();
		while (!elements.isEmpty()) {
			// Collect titles from the current elements
			titleList.addAll(elements.stream().map(WebElement::getText).collect(Collectors.toList()));

			// Scroll to the next page button
			scrollToElement(nextPageButton);
			Thread.sleep(3000);
			// Wait for the next page button to appear
			waitForWebElementToAppear(nextPageButton);

			if (nextPageButton.isEnabled() && nextPageButton.isDisplayed()) {
				nextPageButton.click();
			} else {
				Reporter.log("Next Page button is disabled");
				break;
			}
		}

		return titleList;
	}

	// Title Sorter

	// Helper method to check if a character is a special character
	private boolean isSpecialCharacter(char c) {
		return !Character.isLetterOrDigit(c);
	}

	// Custom comparison logic
	private int customCompare(String s1, String s2) {
		char c1 = s1.charAt(0);
		char c2 = s2.charAt(0);

		if (isSpecialCharacter(c1) && !isSpecialCharacter(c2)) {
			return -1;
		} else if (!isSpecialCharacter(c1) && isSpecialCharacter(c2)) {
			return 1;
		} else if (Character.isDigit(c1) && !Character.isDigit(c2)) {
			return -1;
		} else if (!Character.isDigit(c1) && Character.isDigit(c2)) {
			return 1;
		} else {
			return s1.compareToIgnoreCase(s2);
		}
	}

	// Method to sort titles in ascending order with priority: special characters,
	// numeric, alphabets//A to Z
	public List<String> ascendingTitle(List<String> titleList) {
		Collections.sort(titleList, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return customCompare(s1, s2);
			}
		});
		return titleList;
	}

	// Method to sort titles in descending order with priority: alphabets, numeric,
	// special characters//Z to A
	public List<String> descendingTitle(List<String> titleList) {
		Collections.sort(titleList, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return customCompare(s2, s1);
			}
		});
		return titleList;
	}

	// Method to get the total number of documents
	public int getTotalDocuments(WebElement element) {
		int totalDocCount = Integer.parseInt(element.getText().split(" ")[5]);
		return totalDocCount;

	}

	// Displaying Count //Total Document count on the Page
	@FindBy(xpath = "(//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1'])[1]")
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
		changeSortingValues();
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

	// Check Pagination Functionality

	// First Page Button Webelement
	@FindBy(css = "button[aria-label='First Page']")
	WebElement firstPageButton;

	// Previous Page Button Webelement
	@FindBy(css = "button[aria-label='Previous Page']")
	WebElement previousPageButton;

	// Next Page Button Webelement
	@FindBy(css = "button[aria-label='Next Page']")
	WebElement nextPageButton;

	// Last Page Button Webelement
	@FindBy(css = "button[aria-label='Last Page']")
	WebElement lastPageButton;

	// Page Number Webelement
	@FindBy(xpath = "//button[@class='p-ripple p-element p-paginator-page p-paginator-element p-link ng-star-inserted p-highlight']")
	WebElement pageNumber;
	
	//Last Page Number
	@FindBy(xpath = "//button[@class='p-ripple p-element p-paginator-page p-paginator-element p-link p-highlight ng-star-inserted']")
    WebElement lastPageNumber;
	
	// Pagination Div
	@FindBy(xpath = "//anglerighticon[@class='p-element p-icon-wrapper ng-star-inserted']")
	WebElement pageinationDiv;

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
			Thread.sleep(7000);
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
			// Convert the page number text to an integer array
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getAttribute("aria-label")) };
			return paginationPageNum;

		} else {
			Reporter.log("Next Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
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
				int[] paginationPageNum = { Integer.parseInt(lastPageNumber.getAttribute("aria-label")) };
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
	
	
	
	

	// Scroll to the bottom of the page
	public void scrollToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
	}

	// Scroll to the specific element
	public void scrollToElement(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		waitForWebElementToAppear(element);
	}

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

	// Wait for Element To Clickable
	public void waitForWebElementToClickable(WebElement findBy) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.elementToBeClickable(findBy));
	}

}
