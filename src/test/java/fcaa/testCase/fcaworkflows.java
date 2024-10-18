package fcaa.testCase;



import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.testng.annotations.Test;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.LatestNews;
import fcaa.testComponent.BaseClass;

public class fcaworkflows extends BaseClass{
	
	
@Test
public void home_page() throws InterruptedException, IOException 
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
	//Put date range
	latestNews.putDateRange();
	latestNews.displayText();
	//Click on the Submit
	latestNews.selectSubmit();
	
	
	
	


	
		
}
}
