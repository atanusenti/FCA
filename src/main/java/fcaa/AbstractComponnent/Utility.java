package fcaa.AbstractComponnent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
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

	// Date Strim //Return date List from the date List String
	public List<String> getDate(List<WebElement> element) {
		List<String> dateList = element.stream().map(n -> n.getText().split(":")[1].trim())
				.collect(Collectors.toList());
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
	public List<String> getTitle(List<WebElement> elements) {
		return elements.stream().map(WebElement::getText).collect(Collectors.toList());
	}

	// Method to sort titles in ascending order //A to Z
	public List<String> ascendingTitle(List<String> titleList) {
		Collections.sort(titleList, Comparator.naturalOrder());
		return titleList;
	}

	// Method to sort titles in descending order //Z to A
	public List<String> descendingTitle(List<String> titleList) {
		Collections.sort(titleList, Comparator.reverseOrder());
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

	// Click on the Next button
	@FindBy(css = "button[aria-label='Next Page']")
	WebElement nextpagebtn;

	// Fetch data count from the all web Pages
	public int[] verifyDataCountAcrossPages() {

		int totalCount = 0;
		boolean hasNextPage = true;
		// Fetch the displayed total count (adjust selector as necessary)
		int displayedCount = getTotalDocuments(displayCount);
		while (hasNextPage) {
			// Fetch data from the current page
			totalCount += getDataCurrentPage.size(); // Count items on the current page
			// Check for the next page button and navigate to it
			try {
				if (nextpagebtn.isDisplayed() && nextpagebtn.isEnabled()) {
					nextpagebtn.click();
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
	private WebElement firstPageButton;

	// Previous Page Button Webelement
	@FindBy(css = "button[aria-label='Previous Page']")
	private WebElement previousPageButton;

	// Next Page Button Webelement
	@FindBy(css = "button[aria-label='Next Page']")
	private WebElement nextPageButton;

	// Last Page Button Webelement
	@FindBy(css = "button[aria-label='Last Page']")
	private WebElement lastPageButton;

	// Page Number Webelement
	@FindBy(xpath = "//button[@class='p-ripple p-element p-paginator-page p-paginator-element p-link p-highlight ng-star-inserted']")
	private WebElement pageNumber;

	// First Page button functionality
	public int[] clickFirstPageButton() {

		clickNextPageButton();

		// Check if the "First Page" button is enabled
		if (firstPageButton.isEnabled()) {
			// Click the "First Page" button
			firstPageButton.click();
			// Convert the page number text to an integer array
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getText()) };
			return paginationPageNum;

		} else {
			Reporter.log("First Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	// Previous Page button functionality
	public int[] clickPreviousPageButton() {
		clickNextPageButton();

		// Check if the "Previous Page" button is enabled
		if (previousPageButton.isEnabled()) {
			// Click the "Previous Page" button
			previousPageButton.click();
			// Convert the page number text to an integer array
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getText()) };
			return paginationPageNum;

		} else {
			Reporter.log("Previous Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	// Next Page button functionality
	public int[] clickNextPageButton() {

		// Check if the "Next Page" button is enabled
		if (nextPageButton.isEnabled()) {
			// Click the "Next Page" button
			nextPageButton.click();
			// Convert the page number text to an integer array
			int[] paginationPageNum = { Integer.parseInt(pageNumber.getText()) };
			return paginationPageNum;

		} else {
			Reporter.log("Next Page button is disabled", true);
			return new int[0]; // Return an empty array in case of failure
		}
	}

	// Last Page button functionality
	public int[] clickLastPageButton() {
		// Check if the "Last Page" button is enabled
		if (lastPageButton.isEnabled()) {
			// Click the "Last Page" button
			lastPageButton.click();

			// Check if the "Last Page" button is disabled after clicking
			if (!lastPageButton.isEnabled()) {
				// Convert the page number text to an integer array
				int[] paginationPageNum = { Integer.parseInt(pageNumber.getText()) };
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

}
