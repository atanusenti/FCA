package fcaa.pageObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    
    @FindBy(xpath = "//*[@formcontrolname=\"monthFilter\"]//div[contains(@class, 'p-radiobutton p-component')]")
    WebElement monthRadioBtn;
    
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
    
	
	
    public boolean verifyDataWithinDateRange(String startDateStr,String endDateStr) throws ParseException {
        // Define date range
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse(startDateStr);
        Date endDate = sdf.parse(endDateStr);

        // Input the date range
        String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		
		displayText.click();

		// Click on the submit button
		selectSubmit.click();
		
		List<String> dateList = getDateList();
		
		boolean allDatesInRange = true;
        for (String date : dateList) {
//            String dateText = row.findElement(By.cssSelector(".dateColumn")).getText(); // Adjust selector as needed
            Date dataDate = sdf.parse(date);

            // Assert that dataDate is within the range
            if (dataDate.before(startDate) || dataDate.after(endDate)) {
                allDatesInRange = false; // Set flag to false if any date is out of range
                break; // Exit the loop early if we find a date out of range
            }
        }
        return allDatesInRange;
    }
    
    
    public boolean verifyRadioButtonUnselected(String startDateStr,String endDateStr) throws ParseException {

        // Input the date range
        String dateRangeStr = startDateStr + " - " + endDateStr;
		putDateRange.sendKeys(dateRangeStr);
		
		displayText.click();

        // Locate the radio button
		return monthRadioBtn.getAttribute("class").contains("p-radiobutton-checked");
    
	}
	
}
