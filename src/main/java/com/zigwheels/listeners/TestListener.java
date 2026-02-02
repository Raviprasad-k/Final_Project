package com.zigwheels.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.zigwheels.base.DriverFactory;
import com.zigwheels.utils.ExtentManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        // 🔹 Create a new test entry in the report
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        
        // 🔹 Capture and attach screenshot
        String base64Screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                                  .getScreenshotAs(OutputType.BASE64);
        test.get().addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
    }

    @Override
    public void onFinish(ITestContext context) {
        // 🔹 This writes everything to the HTML file
        extent.flush(); 
    }
}




//package com.zigwheels.listeners;
//
//import com.zigwheels.base.DriverFactory;
//import com.zigwheels.utils.ScreenshotUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.openqa.selenium.WebDriver;
//import org.testng.ITestContext;
//import org.testng.ITestListener;
//import org.testng.ITestResult;
//
//import java.io.File;
//
//public class TestListener implements ITestListener {
//    private static final Logger logger = LogManager.getLogger(TestListener.class);
//
//    @Override
//    public void onTestFailure(ITestResult result) {
//        try {
//            WebDriver driver = DriverFactory.getDriver();
//            if (driver != null) {
//                String name = "screenshots" + File.separator + System.currentTimeMillis() + "_FAIL_" + result.getName() + ".png";
//                ScreenshotUtils.takeScreenshot(driver, name);
//                logger.error("Saved failure screenshot: {}", name);
//            }
//        } catch (Exception e) {
//            logger.error("Listener screenshot failed: {}", e.getMessage());
//        }
//    }
//
//    @Override public void onStart(ITestContext context) { logger.info("=== TEST RUN START ==="); }
//    @Override public void onFinish(ITestContext context) { logger.info("=== TEST RUN END ==="); }
//}
