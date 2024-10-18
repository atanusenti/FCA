package fcaa.testComponent;
	 
	import java.io.IOException;
	 
	import org.openqa.selenium.WebDriver;
	import org.testng.ITestContext;
	import org.testng.ITestListener;
	import org.testng.ITestResult;
	import org.testng.Reporter;
	 
	import com.aventstack.extentreports.ExtentReports;
	import com.aventstack.extentreports.ExtentTest;
	import com.aventstack.extentreports.Status;
	 
	public class ListenerImplementation extends BaseClass implements ITestListener{
		

		ExtentTest test;
		ExtentReports extent = ExtentReporterNG.getReportObject();
		ThreadLocal<ExtentTest> extentLocal = new ThreadLocal<ExtentTest>();
		@Override
		public void onTestStart(ITestResult result) {
			Reporter.log("========================================", true);
			Reporter.log("Starting test: " + result.getMethod().getMethodName(), true);
			test = extent.createTest(result.getMethod().getMethodName());
			extentLocal.set(test); // Unique thread id
		}
	 
		@Override
		public void onTestSuccess(ITestResult result) {
			Reporter.log("Finished test: " + result.getMethod().getMethodName(), true);
			extentLocal.get().log(Status.PASS, "Test Passed");
		}
	 
		@Override
		public void onTestFailure(ITestResult result) {
			extentLocal.get().fail(result.getThrowable());
			try {
				driver = (WebDriver) result.getTestClass().getRealClass().getField("driver").get(result.getInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String filePath = null;
			try {
				filePath = getScreenshot(result.getMethod().getMethodName(), driver);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			extentLocal.get().addScreenCaptureFromPath(filePath, result.getMethod().getMethodName());
		}
	 
		@Override
		public void onTestSkipped(ITestResult result) {
		}
	 
		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		}
	 
		@Override
		public void onStart(ITestContext context) {
		}
	 
		@Override
		public void onFinish(ITestContext context) {
			extent.flush();
		}
	

}
