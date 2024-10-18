package fcaa.AbstractComponnent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utility {
	
	WebDriver driver;
	public Utility(WebDriver driver) {
		
		this.driver=driver;
		PageFactory.initElements(driver, this);
		
		
	}
	
	//Date Strim

	public List<String> getDate(List<WebElement> element) {
		
		List<String> dateList = element.stream().map(n->n.getText().split(":")[1].trim()).collect(Collectors.toList());
		return dateList;
	}
	
	//Date Sort in ascending Order
	public List<String> ascendingDates (List<String> dateList){
		Collections.sort(dateList,new Comparator<String>() {
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
	
	//Date Sort DecendingOrder
	public List<String> decendingDates (List<String> dateList){
		Collections.sort(dateList,new Comparator<String>() {
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
	
	//Title Strim

	// Method to get titles from WebElements
    public List<String> getTitle(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }
    
 // Method to sort titles in ascending order
    public List<String> ascendingTitle(List<String> titleList) {
        Collections.sort(titleList, Comparator.naturalOrder());
        return titleList;
    }
    

    // Method to sort titles in descending order
    public List<String> descendingTitle(List<String> titleList) {
        Collections.sort(titleList, Comparator.reverseOrder());
        return titleList;
    }
    
    
		
		
	
	//Wait for Element Visible
	
	public void waitForElementToAppear(By findBy) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(findBy));

	}
	
	public void waitForWebElementToAppear(WebElement findBy) {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOf(findBy));

	}
	
	
}
