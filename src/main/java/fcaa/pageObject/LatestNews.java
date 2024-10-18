package fcaa.pageObject;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import fcaa.AbstractComponnent.MenuBar;

public class LatestNews extends MenuBar {
	//String validdateRange = ;
	
	WebDriver driver;
	public LatestNews(WebDriver driver) {
		super(driver);
		this.driver=driver;
		PageFactory.initElements(driver, this);
		
		
	}
	By toastMessage = By.xpath("//ul/li[@class='px-0 text-sm ng-star-inserted']");
	
    @FindBy(xpath="//input[@placeholder='Search Latest News']")
    WebElement searchLatestNews;
    
    @FindBy(xpath="//input[@placeholder='Search Latest News']")
    WebElement searchLatestNewsText;
    
    
  //Sort(Compare New & Old)
  	//before filter capture the published date
    @FindBy(xpath="//ul/li[@class='px-0 text-sm ng-star-inserted']")
    List<WebElement> beforefilterdate;
  
    //Click on the Sort button
    @FindBy(xpath="(//div[@aria-label='dropdown trigger'])[1]")
    WebElement clickOnSort; 
    
    //Select Oldest from the sorting List
    @FindBy(xpath="(//span[normalize-space()='Oldest'])[1]")
    WebElement selectOldest; 
    
  //After filter capture the date
    @FindBy(xpath="//ul/li[@class='px-0 text-sm ng-star-inserted']")
    List<WebElement> afterfilterdate; 
    
  //Select Newest from the sorting List
    @FindBy(xpath="(//span[normalize-space()='Newest'])[1]")
    WebElement selectNewest;
    
    //Capture Title before sorting
    @FindBy(xpath = "//a[@class='link_color sm:text-lg text-base font-semibold cursor-pointer']")
    List<WebElement> beforefiltertitle;
    
    //Select Title Ascending from the sorting List
    @FindBy(xpath="(//span[normalize-space()='Title Ascending'])[1]")
    WebElement selectascending; 
    
    //Select Title Descending from the sorting List
    @FindBy(xpath="(//span[normalize-space()='Title Descending'])[1]")
    WebElement selectdescending; 
    
  //After filter capture the Title
    @FindBy(xpath = "//a[@class='link_color sm:text-lg text-base font-semibold cursor-pointer']")
    List<WebElement> afterfiltertitle;
    
    // Select Month Radio button
    @FindBy(xpath="//label[text()='This Month']")
    WebElement selectMonthRadio;
    
    //Click on the submit button
    @FindBy(xpath="(//button[@class='solid_btn flex-1'][normalize-space()='Submit'])[1]")
    WebElement selectSubmit;
    
    //Enter date range
	@FindBy(xpath="(//input[@placeholder='DD/MM/YYYY - DD/MM/YYYY'])[1]")
	WebElement putDateRange;
	
	//Displaying text
	@FindBy(xpath="(//div[@class='toolbar text-sm py-2 px-2 border-round-lg flex-1'])[1]")
	WebElement displayText;
	
	
    
    
    
    
    public void searchLatestNews() {
    	searchLatestNews.click();
    }
    public String getSearchLatestNews() {
    	//waitForElementToAppear(By.xpath("//input[@placeholder='Search Latest News']"));
    	return searchLatestNewsText.getAttribute("placeholder");
    }
    
    //Get Date List //before filter capture the published date
    public List<String> getDateList() {
    	List<String> getDateList = getDate(beforefilterdate);
    	return getDateList;
    }
    
  //Click on the Sort button
    public void clickOnSort() {
    	waitForElementToAppear(toastMessage);
    	clickOnSort.click();
    }
    
  //Select Oldest from the sorting List 
    public void selectOldest() {
    	selectOldest.click();
    }
    
  //Select Newest from the sorting List
    public void selectNewest() {
    	selectNewest.click();
    }
    
  //Get Title List //Capture Title before sorting
    public List<String> getTitleList() {
    	List<String> getTitle = getTitle(beforefiltertitle);
    	return getTitle;
    }  
    
   //Select Title Ascending from the sorting List
    public void selectascending() {
    	selectascending.click();
    }

    
  //Select Title descending from the sorting List
    public void selectdescending() {
    	selectdescending.click();
    }
    
    // Select Month Radio button
    public void selectMonthRadio() {
    	selectMonthRadio.click();
    }
    
    //Click on the submit button
    public void selectSubmit() {
	selectSubmit.click();
}
    
    //Enter date range
	public void putDateRange() {
	putDateRange.sendKeys("20/12/2020 - 24/12/2020");
    
}
	//Displaying text
	public void displayText() {
		displayText.click();
	}
	
}
