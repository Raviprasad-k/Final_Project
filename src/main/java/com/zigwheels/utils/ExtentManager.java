package com.zigwheels.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            // 🔹 Storing report in the artifacts folder we already created
            ExtentSparkReporter spark = new ExtentSparkReporter("artifacts/ExtentReport.html");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("ZigWheels Automation Report");
            spark.config().setReportName("Selenium Automation Testing");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Project", "ZigWheels");
            extent.setSystemInfo("Tester", "QEA Team 3");
        }
        return extent;
    }
}