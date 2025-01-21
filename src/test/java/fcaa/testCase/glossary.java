package fcaa.testCase;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
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
			String searchPlaceHolder = glossary.;
			Assert.assertTrue(searchPlaceHolder.equalsIgnoreCase("Search Glossary"));
		}

		
		
}
