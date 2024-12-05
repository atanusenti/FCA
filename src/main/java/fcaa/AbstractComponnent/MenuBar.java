package fcaa.AbstractComponnent;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import fcaa.pageObject.Forms;
import fcaa.pageObject.Instruments;
import fcaa.pageObject.LatestNews;
import fcaa.pageObject.Level3Materials;

public class MenuBar extends Utility{
	
	WebDriver driver;
	
	public MenuBar(WebDriver driver) {
		super(driver);
		this.driver=driver;
		PageFactory.initElements(driver, this);	
	}
	
	@FindBy(xpath="//span[text()='Home']")
	WebElement home;
	
	@FindBy(xpath="//span[text()='FCA Handbook']")
	WebElement handBook;
	
	@FindBy(xpath="//span[text()='Latest News']")
	WebElement latestNews;
	
	@FindBy(xpath="//span[text()='Glossary']")
	WebElement glossary;
	
	@FindBy(xpath="//span[text()='Instruments']")
	WebElement instruments;
	
	@FindBy(xpath="//span[text()='Forms']")
	WebElement forms;
	
	@FindBy(xpath="//span[text()='Guides']")
	WebElement guides;
	
	@FindBy(xpath="//span[text()='Technical Standards']")
	WebElement technicalStandards;

	@FindBy(xpath="//span[text()='Level 3 Materials']")
	WebElement level3Materials;
	
	@FindBy(xpath="//span[text()='Policy and Guidance']")
	WebElement policandGuidance;
	
	@FindBy(xpath="//span[text()='Fines and Enforcements']")
	WebElement finesandEnforcements;
	
	@FindBy(xpath="//span[text()='Advanced Search']")
	WebElement advancedSearch;
	

	
public void goToHome() {
		
	home.click();

		
	}

public void goToHandBook() {
	
	handBook.click();
}

public LatestNews goToLatestNews() {
	
	latestNews.click();
	LatestNews latestNews = new LatestNews(driver);
	return latestNews;
}

public void goToGlossary() {
	
	glossary.click();
}


public Instruments goToInstruments() {
	
	instruments.click();
	Instruments instruments = new Instruments(driver);
	return instruments;
}

public Forms goToForms() {
	
	forms.click();
	Forms forms = new Forms(driver);
	return forms;
}

public void goToTechnicalStandards() {
	
	technicalStandards.click();
}

public Level3Materials goToLevel3Materials() {
	
	level3Materials.click();
	Level3Materials level3Materials = new Level3Materials(driver);
	return level3Materials;
}

public void goToPolicandGuidance() {
	
	policandGuidance.click();
}

public void goToFinesandEnforcements() {
	
	finesandEnforcements.click();
}

public void goToadvancedSearch() {
	
	advancedSearch.click();
}


}


