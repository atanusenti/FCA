package fcaa.pageObject;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;

import fcaa.AbstractComponnent.BooleanSearch;
import fcaa.AbstractComponnent.MenuBar;

public class Glossary extends MenuBar {

	WebDriver driver;

	public Glossary(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// -----------------Search Legal Instruments-------------------//

	// Latest News Search functionality

	@FindBy(xpath = "//input[@placeholder='Search Glossary']")
	private WebElement searchBox;

	@FindBy(xpath = "//button[text()=' Search ']")
	private WebElement searchBtn;

	@FindBy(xpath = "//button[text()=' Clear ']")
	private WebElement searchClearBtn;

	@FindBy(xpath = "//a[contains(@href,'/glossary/glossary-details')]")
	private List<WebElement> glossaryTitles;

	@FindBy(xpath = "//div[contains(@class,'details_wrap')]/div")
	private List<WebElement> glossaryContents;

	@FindBy(xpath = "//div[contains(@class,'p-card-content')]")
	private List<WebElement> glossaryListData;

	@FindBy(css = "button[aria-label='Next Page']")
	WebElement nextPageButton;

	public void searchLatestNews() {
		searchBox.click();
	}

	public void performSearch(String query) {
		searchBox.clear();
		searchBox.sendKeys(query);
		searchBtn.click();
	}

	BooleanSearch booleanSearch = new BooleanSearch();

	// ---------------------------------------------------------------------------

	// Validate search results
	public boolean validateResults(String query) throws InterruptedException {
		if(!glossaryListData.isEmpty()) {
			boolean hasNextPage = true;
			int actualDataPosition = 0;

			while (hasNextPage) {
				for (int i = 0; i < glossaryListData.size(); i++) {
					String title = glossaryTitles.get(i).getText().toLowerCase();
					String content = glossaryContents.get(i).getText().toLowerCase();
					String updatedText = title.concat(" ").concat(content);

					// Match query against text
					if (!booleanSearch.evaluateQuery(query, updatedText)) {
						System.out.println(updatedText);
						Reporter.log("Error in Data present at : " + (actualDataPosition + (i + 1)), true);
						return false; // Test fails if any result doesn't match
					}
				}
				hasNextPage = goToNextPageAndIsNextPageAvailable();
				actualDataPosition += glossaryListData.size();
			}
			return true; // All results matched
		}
		else {
			Reporter.log("No Glossary Data Available", true);
			return true;
		}
	}
	
	public void changeTheSortingValues(int sortValue) throws InterruptedException {
		if(!glossaryListData.isEmpty()) {
			changeSortingValues(sortValue);
		}
		else {
			Reporter.log("No Glossary Data Available", true);
		}
	}
}
