package com.zigwheels.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.zigwheels.base.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // 1. Logic from ExtentManager moved here
    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter spark = new ExtentSparkReporter("artifacts/ExtentReport.html");
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("ZigWheels Automation Report");
        spark.config().setReportName("Selenium Automation Testing");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Project", "ZigWheels");
        extent.setSystemInfo("Tester", "QEA Team 3");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed");
        try {
            String base64Screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                                      .getScreenshotAs(OutputType.BASE64);
            test.get().addScreenCaptureFromBase64String(base64Screenshot, "Success Screenshot");
        } catch (Exception e) {
            test.get().log(Status.INFO, "Could not capture success screenshot");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        //test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        String fullMessage = result.getThrowable().toString();
        String simpleMessage = fullMessage.split("\n")[0]; 

        // 2. Log only the simple message to Extent Report
        test.get().log(Status.FAIL, "<b>Test Failed:</b> " + simpleMessage);
        
        // Capture and attach screenshot
        try {
            String base64Screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                                      .getScreenshotAs(OutputType.BASE64);
            test.get().addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
        } catch (Exception e) {
            test.get().log(Status.INFO, "Could not capture screenshot: " );
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }
}


//-----------VERSION 1

//package com.zigwheels.utils;
//
//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import com.aventstack.extentreports.reporter.configuration.Theme;
//
//public class ExtentManager {
//    private static ExtentReports extent;
//
//    public static ExtentReports getInstance() {
//        if (extent == null) {
//            // 🔹 Storing report in the artifacts folder we already created
//            ExtentSparkReporter spark = new ExtentSparkReporter("artifacts/ExtentReport.html");
//            spark.config().setTheme(Theme.STANDARD);
//            spark.config().setDocumentTitle("ZigWheels Automation Report");
//            spark.config().setReportName("Selenium Automation Testing");
//
//            extent = new ExtentReports();
//            extent.attachReporter(spark);
//            extent.setSystemInfo("Project", "ZigWheels");
//            extent.setSystemInfo("Tester", "QEA Team 3");
//        }
//        return extent;
//    }
//}