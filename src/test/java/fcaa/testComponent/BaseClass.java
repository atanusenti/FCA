package fcaa.testComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import fcaa.pageObject.HomePage;

public class BaseClass {

	public Properties properties;
	public WebDriver driver;
	public HomePage HomePage;

	public WebDriver initializeDriver() throws IOException {
		properties = new Properties();
		FileInputStream fileInputStream = new FileInputStream("./src/main/java/fcaa/resources/config.properties");
		properties.load(fileInputStream);
		String browserName = properties.getProperty("browser");
		driver = switch (browserName) {
		case "chrome" -> new ChromeDriver();
		case "edge" -> new EdgeDriver();
		case "firefox" -> new FirefoxDriver();
		default -> null;
		};

		// Maximize window
		driver.manage().window().maximize();
		// Implicit wait
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		return driver;
	}

	@BeforeMethod
	public void LaunchApplication() throws IOException {
		driver = initializeDriver();
		HomePage = new HomePage(driver);
		HomePage.goTo(properties.getProperty("url"));
	}

	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	// Screenshot
	public String getScreenshot(String testCaseName, WebDriver driver) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		File dest = new File(System.getProperty("user.dir") + "//Reports//" + testCaseName + ".png");
		FileUtils.copyFile(source, dest);
		return System.getProperty("user.dir") + "//Reports//" + testCaseName + ".png";
	}

}
