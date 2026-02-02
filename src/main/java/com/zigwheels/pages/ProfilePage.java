
package com.zigwheels.pages;

import com.zigwheels.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.ArrayList;
import java.util.List;

public class ProfilePage {
    private final WebDriver driver;

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

//    ConfigReader obj = new ConfigReader();

    @FindBy(xpath = "//div[@id='des_lIcon']")
    private WebElement profileIcon;

    @FindBy(xpath = "//div[@class='lgn-sc c-p txt-l pl-30 pr-30 googleSignIn']")
    private WebElement googleBtn;

    public void openProfile() { profileIcon.click(); }

    public String tryGoogleLoginInvalid() {
        String original = driver.getWindowHandle();
        int before = driver.getWindowHandles().size();
        try {
            googleBtn.click();
        } catch (Exception e) {
            try {
                WebElement alt = driver.findElement(By.xpath("//a[contains(.,'Google')]"));
                alt.click();
            } catch (Exception ignored) {}
        }
        // switch to new window
        try { Thread.sleep(1500);} catch (InterruptedException ignored) {}
        for (String h : driver.getWindowHandles()) {
            if (!h.equals(original)) { driver.switchTo().window(h); break; }
        }
        try {
            WebElement email = driver.findElement(By.xpath("//input[@id='identifierId']"));
            email.sendKeys(ConfigReader.get("email"));
            email.sendKeys(Keys.ENTER);
            Thread.sleep(1200);
            try {
                WebElement err = driver.findElement(By.xpath("//div[@class='Ekjuhf Jj6Lae']"));
                return err.getText().trim();
            } catch (NoSuchElementException e) {
                return "No explicit Google error message captured.";
            }
        } catch (Exception e) {
            return "Google login window did not load as expected.";
        } finally {
            // close child windows
            List<String> toClose = new ArrayList<>(driver.getWindowHandles());
            for (String h : toClose) {
                if (!h.equals(original)) {
                    try { driver.switchTo().window(h).close(); } catch (Exception ignored) {}
                }
            }
            driver.switchTo().window(original);
        }
    }
}
