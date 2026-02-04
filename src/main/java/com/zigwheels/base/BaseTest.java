package com.zigwheels.base;

import com.zigwheels.utils.ConfigReader;
import com.zigwheels.utils.ExcelUtils;
import com.zigwheels.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected ExcelUtils excel;
    protected final String artifactsDir = "artifacts";
    protected String screenshotsDir = "screenshots";
    protected String browserName;

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String RUN_STAMP = LocalDateTime.now().format(TS_FORMAT);

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true) 
    public void setUp(@Optional("chrome") String browser) {
        this.browserName = browser;

        // Initialize driver once per suite
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless", "false"));
        DriverFactory.initDriver(browser, headless);
        driver = DriverFactory.getDriver();

        // Standard Timeouts
        int pageLoadTimeout = Integer.parseInt(ConfigReader.get("pageLoadTimeout", "50"));
        int explicitWait = Integer.parseInt(ConfigReader.get("explicitWait", "20"));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));

        // Create Directories
        new File(artifactsDir).mkdirs();
        screenshotsDir = "screenshots" + File.separator + RUN_STAMP + "_" + browserName;
        new File(screenshotsDir).mkdirs();

        // Initialize Excel once per suite
        String excelPath = artifactsDir + File.separator + "zigwheels_automation_" + browser + "_" + RUN_STAMP + ".xlsx";
        if (excel == null) {
            excel = new ExcelUtils(excelPath);
            logger.info("Excel initialized at: {}", excelPath);
        }

        driver.get(ConfigReader.get("baseUrl"));
        captureStep("00_Home_Launched");
    }

    @AfterMethod(alwaysRun = true) // 🔹 Navigates home instead of closing
    public void resetPage() {
        try {
            String baseUrl = ConfigReader.get("baseUrl");
            driver.navigate().to(baseUrl);
            logger.info("Test completed. Navigating back to Home.");
        } catch (Exception e) {
            logger.error("Could not reset page: " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true) // 🔹 Closes browser only after ALL tests are done
    public void tearDown() {
        if (driver != null) {
            DriverFactory.quitDriver();
        }
        logger.info("Browser session ended.");
    }

    protected void captureStep(String stepName) {
        try {
            boolean doShots = Boolean.parseBoolean(ConfigReader.get("screenshotEveryStep", "true"));
            if (!doShots) return;
            String ts = LocalDateTime.now().format(TS_FORMAT);
            String file = screenshotsDir + File.separator + ts + "_" + browserName + "_" + stepName + ".png";
            ScreenshotUtils.takeScreenshot(driver, file);
        } catch (Exception e) {
            logger.warn("Screenshot failed: " + e.getMessage());
        }
    }
}