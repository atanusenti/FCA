package fcaa.testComponent;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.BeforeMethod;

import fcaa.pageObject.HomePage;

public class BaseClass {

	public Properties properties;
	public WebDriver driver;
	public HomePage HomePage;
	
	public void initializeDriver() throws IOException {
		 properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream("./src/main/java/fcaa/resources/config.properties");
		properties.load(fileInputStream);
		String browserName = properties.getProperty("browser");
		driver =switch(browserName){
		case "chrome"->new ChromeDriver();
		case "edge"->new EdgeDriver();
		case "firefox"->new FirefoxDriver();
		default->null;
		};
		
	//Maximize window
		driver.manage().window().maximize();
	//Implicit wait
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		
	}
	
	@BeforeMethod
	public void LaunchApplication() throws IOException {
		initializeDriver();
		HomePage =new HomePage(driver);
		HomePage.goTo("https://fcahandbook.sentientgeeks.us/");
	}
	
//	@AfterMethod
//	public void TearDown() {
//		driver.close();
//	}
	
}
