package com.zigwheels.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class DriverFactory {
	private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

	public static synchronized void initDriver(String browser, boolean headless) {
		WebDriver driver;
		String browserType = browser.toLowerCase();

		switch (browserType) {
		case "chrome":
			ChromeOptions co = new ChromeOptions();
			// Add window-size here!
			co.addArguments("--disable-notifications", "--start-maximized");
			if (headless)
				co.addArguments("--headless=new");
			driver = new ChromeDriver(co);
			break;

		case "edge":
			EdgeOptions eo = new EdgeOptions();
			// Add window-size here!
			eo.addArguments("--disable-notifications", "--start-maximized");
			if (headless)
				eo.addArguments("--headless=new");
			driver = new EdgeDriver(eo);
			break;

		default:
			throw new IllegalArgumentException("Unsupported browser: " + browser);
		}

		tlDriver.set(driver);
	}

	public static synchronized WebDriver getDriver() {
		return tlDriver.get();
	}

	public static synchronized void quitDriver() {
		WebDriver driver = tlDriver.get();
		if (driver != null) {
			driver.quit();
			tlDriver.remove();
		}
	}
}
