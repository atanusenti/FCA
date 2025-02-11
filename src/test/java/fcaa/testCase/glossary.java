package fcaa.testCase;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import fcaa.AbstractComponnent.MenuBar;
import fcaa.pageObject.Glossary;
import fcaa.testComponent.BaseClass;

public class glossary extends BaseClass {

	// Latest News Object
	MenuBar menuBar;
	Glossary glossary;

	@BeforeMethod
	public void setUpGlossary() {
		menuBar = new MenuBar(driver);
		glossary = menuBar.goToGlossary();
	}

	// <--------------Test cases for the glossary Home page---------------->

	// Verify Search functionality
	@Test(priority = 1)
	public void testsearchbox() throws InterruptedException {
//			String searchPlaceHolder = glossary.;
//			Assert.assertTrue(searchPlaceHolder.equalsIgnoreCase("Search Glossary"));
	}

	@DataProvider(name = "booleanSearchQueries")
	    public Object[][] getSearchQueries() {
	        return new Object[][]{
//	        	    ----------------------AND/&&------------------------------
//	                {"market AND finance"},
                	{"market && finance"}, 
//        			{"market &&finance"}, // test case fails  
//        	    	----------------------OR/||-------------------------------
//	                {"market OR finance"},
//	        		{"market finance"},
//	        		{"market || finance"},   
//    	    		----------------------NOT/-/!-------------------------------
//	                {"finance -market"},
//        			{"market NOT finance"}, 
//        			{"finance !order"},
//	    			----------------------Combination of AND/OR/NOT-------------------------------
//	        		{"!finance -market AND orders"},
//	        		{"management || order && contract"},
//	        		{"management AND order OR contract"},
//	        		{"market NOT finance !order"},
//	        		{"market NOT finance orders"},
//        			{"management || order NOT contract"},
//    				{"management AND order NOT contract"},
//        			{"management AND order OR contract AND finance"},
//        			{"management && order && contract"},
//        			{"management || order || contract"},
//	        		----------------------Exact Operator-------------------------------
//	        		{"\"finance\""},
//	        		{"\"finance transaction\""},
//	        		----------------------Proximity Operator-------------------------------
//                	{"maket~"},
//        			{"ordr~"},
//        			{"fninancee~"},
//	        		----------------------Proximity/?/* Operator-------------------------------
//                	{"finance*"},
//	        		{"*nance"},
//	        		{"*nance*"},
//	        		{"*nance~"},
//                	{"finan?e"},
//	        		{"?inance~"},
//	        		----------------------Combination of AND/OR/NOT/()/Exact Operator-------------------------------
//	        		{"market NOT finance \"orders\""},
//	                {"market AND (finance OR contract)"},
//        			{"!finance -market AND transaction"},
//        			{"!(finance market) AND transaction"},
//        			{"finance !order AND market"},
//        			{"finance !order -market"},
//            		{"\"finance transaction\" AND order"},
//        			{"finance -(order || manage)"},
//        			{"market OR (finance !manage)"}, // test case fails
//        			{"market AND (finance !manage)"},	
//        			{"market OR \"finance transaction\" -order"},
//    				{"(market finance) NOT abuse"},
//        			{"\"finance transaction\" -order"},
//        			{"\"finance transaction\" market"},
//        			{"(market AND finance) NOT contract"},
//        			{"(market OR finance) NOT contract"},
//        			{"(market OR finance) AND contract"},
//        			{"\"finance transaction\" NOT \"order\""},
//        			{"market \"finance transaction\" NOT \"order\""},
//        			{"finance && \"finance transaction\" NOT \"order\""},
//        			{"(market OR finance) AND \"contract\""},
//	        		----------------------Combination of +/~/?/()/Exact/*/NOT Operator-------------------------------
//	        		{"finan* -mark?t"},
//        			{"finance* AND marke*"},
//        			{"financ* OR marke*"},
//        			{"(market AND finance) NOT contract*"},
//        			{"(market OR finance) NOT contract*"},
//        			{"(market AND finance) AND contract*"},
//        			{"financ? && marke*"},
//        			{"+finance +market"},
//	        		{"-mark?t finan*"},
//	        		{"financ~ -mark?t"},
//	        		{"financ~ +mark?t"},
//	        		{"-fninancee~ +mark?t"},
//	        		{"fninan~ mark?t"},
//	        		{"fninan~ -mark?t +orders"},
//	        		{"fninan~ -mark?t -order"},
//	        		{"fninan~ -mark?t ordr~"},
//	        		{"fninan~ mark?t +ordre~"},
//	        		{"+ordre~ -orders"},
//	        		{"+ordre~ AND \"orders\""}, // should highlight orders text as well
//    				{"market +finance -order"},
//        			{"market +finance"},
//        			{"-finance +market"},
//        			{"+finance markt~"},
//        			{"+finance -markt~"}, // test case fails
//        			{"market -financ*"},
//        			{"fninance~ -order"},
//        			{"markett~ ?inance~"},
//        			{"markette~ -*nance~"},
//	        		----------------------Some Other Combinations-------------------------------
//	        		{"fninan~ -mark?t OR ordre~"},
//	        		{"fninan~ mark?t OR ordre~"},
//	        		{"fninan~ mark?t NOT ordre~"},// test case fails
//	        		{"-fninan~ mark?t NOT ordre~"},// test case fails
//	        		{"-mark?t fninan~ AND ordree~"},
//	        		{"fninan~ -mark?t AND ordree~"},
//	        		{"fninan~ -mark?t +ordree~"},
//	        		{"fninan~ AND order"},
//	        		{"fninace~ OR mark?t -order"},
//        			{"fninan~ && ordr~"},
//        			{"fninan~ OR ordr~"},
//	                {"finan?e AND ?arket"},
//	                {"mark* || financ*"},
//	        		{"market && finan?e"},
//	        		{"marke~ && finan?e !*ment"},
//        			{"finance* AND \"market\""},
//        			{"(marke* || finance*) AND (contract* OR order*)"},
//        			{"(marke* || finance*) AND (\"transaction\" OR \"services\") NOT (contract* OR order*)"},
//        			{"(mar* OR fin*) AND (\"review status\" OR \"documents\") NOT (leg* OR acc*)"},
//	        		{"fninance~ || order NOT manage"},
//	        		{"market +finance || (order && management)"},
//    		    	------------------------------Proximity Fuzzy----------------------------------------
//	        		{"market \"finance abuse\"~5"},
//	        		{"(market \"finance transaction\"~5) -order"},
//	        		{"order && \"finance transaction\"~3"},
//	        		{"\"market order\"~5"},
//        			{"\"market system\"~2"},
//    		    	------------------------------Boost Operator (Doubt for Written Code)----------------------------------------
//	        		{"market^2 finance"},
//	        		{"market finance^2"},
//	        		{"(market && abuse)^5 AND order"},
//	        		{"market finance^2 NOT transaction"},
//	        		{"(market && abuse)^5 (finance && abuse)^2 abuse"},
	        };
	    }

	@Test(dataProvider = "booleanSearchQueries")
	public void testBooleanSearch(String query) throws InterruptedException {
		glossary.performSearch(query);
		glossary.changeTheSortingValues(100);
		boolean isValid = glossary.validateResults(query);
		Assert.assertTrue(isValid, "Search results do not match for query: " + query);
	}
}
