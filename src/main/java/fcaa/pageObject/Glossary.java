package fcaa.pageObject;



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import fcaa.AbstractComponnent.MenuBar;

public class Glossary extends MenuBar{

	WebDriver driver;

	public Glossary(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	// -----------------Search Legal Instruments-------------------//

		// Latest News Search functionality

		@FindBy(xpath = "//input[@placeholder='Search Glossary']")
		WebElement searchGlossary;

		public void searchLatestNews() {
			searchGlossary.click();
		}
	
}
