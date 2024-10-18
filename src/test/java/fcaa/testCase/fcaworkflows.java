package fcaa.testCase;



import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.junit.experimental.theories.suppliers.TestedOn;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.LatestNews;
import fcaa.testComponent.BaseClass;

@Listeners(fcaa.testComponent.ListenerImplementation.class)
public class fcaworkflows extends BaseClass{
	
	
@Test
public void home_page() throws InterruptedException, IOException, ParseException 
{
	
//Latest News Object
	MenuBar menuBar = new MenuBar(driver);
	LatestNews latestNews = menuBar.goToLatestNews();
	//Utility utility = new Utility(driver);
	
	//Latest News
	
	String searchPlaceHolder = latestNews.getSearchLatestNews();	 
	Assert.assertTrue(searchPlaceHolder.equalsIgnoreCase("Search Latest News"));
	
	//Sort the dates before filter
	//List<String> dateBeforeSort = latestNews.getDateList();
	latestNews.clickOnSort();
	latestNews.selectOldest();
	List<String> dateAfterSort = latestNews.getDateList();
	List<String> latestDateaftersort = latestNews.ascendingDates(dateAfterSort);
	Assert.assertEquals(latestDateaftersort, dateAfterSort);
	

	
	//Sort(Title ascending)
	//2.filter the sort
	latestNews.clickOnSort();
	latestNews.selectNewest();
		
	Thread.sleep(5000);
	
	//Compare Title Descending
	latestNews.clickOnSort();
	latestNews.selectdescending();
	List<String> titleAfterSort= latestNews.getTitleList();
	List<String> latestTitleafterSort = latestNews.descendingTitle(titleAfterSort);
	Assert.assertEquals(latestTitleafterSort, titleAfterSort);
	
	//Select Month Radio
	latestNews.selectMonthRadio();
	//Click on the Submit
	latestNews.selectSubmit();
	Thread.sleep(3000);
	
	//Click on the Submit
	//latestNews.selectSubmit();
	
	//Compare date range with data in the table
//	latestNews.getDateList();
	latestNews.selectMonthRadio();
	
	String startDateStr = "20/12/2019";
    String endDateStr = "24/12/2020";
	boolean allDatesInRange = latestNews.verifyDataWithinDateRange(startDateStr, endDateStr);
	Assert.assertTrue(allDatesInRange, "Not all displayed dates are within the specified range.");
	
	boolean isRadioButtonSelected = latestNews.verifyRadioButtonUnselected(startDateStr, endDateStr);
    Assert.assertFalse(isRadioButtonSelected, "Radio button should be unselected after entering date range.");
    System.out.println(isRadioButtonSelected);

	
		
}
}
