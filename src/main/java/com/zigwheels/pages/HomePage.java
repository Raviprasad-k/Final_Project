
package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.*;

public class HomePage {
	private final WebDriver driver;

	public HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "//img[@title='ZigWheels Home']")
	private WebElement logo;

	@FindBy(xpath = "//span[contains(.,'NEW CARS')]")
	private WebElement newCarsMenu;

	@FindBy(xpath = "//a[@title='Popular Cars']")
	private WebElement popularCars;

	@FindBy(xpath = "//span[contains(.,'NEW BIKES')]")
	private WebElement newBikesMenu;

	@FindBy(xpath = "//a[contains(.,'Upcoming Bikes')]")
	private WebElement upcomingBikes;

	@FindBy(xpath = "//span[contains(.,'SCOOTERS')]")
	private WebElement scootersMenu;

	@FindBy(xpath = "//a[contains(.,'Electric Scooters')]")
	private WebElement electricScooters;

	@FindBy(xpath = "//span[contains(.,'MORE')]")
	private WebElement moreMenu;

	@FindBy(xpath = "//a[contains(.,'Used Cars')]")
	private WebElement usedCars;

	@FindBy(xpath = "//div[@id='des_lIcon']")
	private WebElement profileIcon;

	public void hover(WebElement el) {
		new Actions(driver).moveToElement(el).pause(Duration.ofMillis(300)).perform();
	}

	public void openPopularCars() {
		try {
			hover(newCarsMenu);
			popularCars.click();
		} catch (Exception e) {
			driver.navigate().to("https://www.zigwheels.com/popular-cars");
		}
	}

	public void openElectricScooters() {
		try {
			hover(scootersMenu);
			electricScooters.click();
		} catch (Exception e) {
			driver.navigate().to("https://www.zigwheels.com/electric-scooters");
		}
	}

	public void openUsedCars() {
		try {
			hover(moreMenu);
			usedCars.click();
		} catch (Exception e) {
			driver.navigate().to("https://www.zigwheels.com/used-car");
		}
	}

	public void openProfile() {
		profileIcon.click();
	}

	

}
