package com.zigwheels.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.zigwheels.utils.ConfigReader;

public class ProfilePage {
	private final WebDriver driver;
	private final WebDriverWait wait;

	public ProfilePage(WebDriver driver) {
		this.driver = driver;
		// Initialize wait once (10 seconds timeout)
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//div[@id='des_lIcon']")
	private WebElement profileIcon;

	@FindBy(xpath = "//div[@class='lgn-sc c-p txt-l pl-30 pr-30 googleSignIn']")
	private WebElement googleBtn;

	public void openProfile() {
		wait.until(ExpectedConditions.elementToBeClickable(profileIcon)).click();
	}

	public String tryGoogleLoginInvalid() {
		String original = driver.getWindowHandle();
		
		// 1. Wait for Google button and click
		try {
			wait.until(ExpectedConditions.elementToBeClickable(googleBtn)).click();
		} catch (Exception e) {
			WebElement alt = driver.findElement(By.xpath("//a[contains(.,'Google')]"));
			alt.click();
		}

		// 2. Wait for the new window to appear (replaces Thread.sleep)
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));

		// Switch to child window
		for (String h : driver.getWindowHandles()) {
			if (!h.equals(original)) {
				driver.switchTo().window(h);
				break;
			}
		}

		try {
			// 3. Wait for email input and enter data
			WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='identifierId']")));
			email.sendKeys(ConfigReader.get("email"));
			email.sendKeys(Keys.ENTER);

			// 4. Wait for the error message to appear (replaces Thread.sleep)
			WebElement err = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='Ekjuhf Jj6Lae']")));
			return err.getText().trim();

		} catch (Exception e) {
			return "Google login error not captured: " + e.getMessage();
		} finally {
			// Close child windows and return to main
			for (String h : driver.getWindowHandles()) {
				if (!h.equals(original)) {
					driver.switchTo().window(h).close();
				}
			}
			driver.switchTo().window(original);
		}
	}
}
