package fcaa.pageObject;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import fcaa.AbstractComponnent.MenuBar;

public class Level3Materials extends MenuBar {
	WebDriver driver;

	public Level3Materials(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// -------------------Title List to Verify Sorting--------------//

	// Capture Title
	@FindBy(xpath = "//a[@class='link_color sm:text-lg text-base cursor-pointer font-semibold']")
	List<WebElement> contentTitle;

	// Get Title List
	public List<String> getTitleList() throws InterruptedException {
		return titleList(contentTitle);
	}

	// ------------Display Count-------------//

	public int[] verifyDataCount() throws InterruptedException {
		int[] count = verifyDataCountAcrossPages();
		return count;
	}

	// ------------------- Test Pagination functionality-----------------//

	// Test Pagination functionality

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

}
