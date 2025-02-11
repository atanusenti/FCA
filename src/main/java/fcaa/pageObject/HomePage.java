package fcaa.pageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import fcaa.AbstractComponnent.MenuBar;

public class HomePage extends MenuBar {
	
	WebDriver driver;
	public HomePage(WebDriver driver) {
		super(driver);
		this.driver=driver;
		PageFactory.initElements(driver, this);
		
		
	}
	

	public void goTo(String url) {
		driver.get(url);
	}
	
	

}
