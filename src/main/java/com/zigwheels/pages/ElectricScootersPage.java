package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;

public class ElectricScootersPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ElectricScootersPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "*//div[@class='ht-20' and normalize-space()='Ola Electric']")
    private WebElement olaFilter;
    
    @FindBy(xpath = "//a[@title='Ola Electric Scooters']") 
    private WebElement olalink;
    
    public void filterOLA() throws InterruptedException {
        // Wait for the filter to be visible before interacting
        wait.until(ExpectedConditions.visibilityOf(olaFilter));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // 1. Scroll to the filter
        js.executeScript("arguments[0].scrollIntoView(true);", olaFilter);
        
        // 2. Small scroll up to avoid sticky header blocking
        js.executeScript("window.scrollBy(0,-100)");
        
        // 3. JS Click (Best for Jenkins/Headless)
        js.executeScript("arguments[0].click();", olaFilter);
    }

    public List<List<String>> scrapeModels() {
        List<List<String>> rows = new ArrayList<>();
        
        // Wait for items to load before scraping
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(@class,'lnk-hvr')]")));
        } catch (Exception ignored) {}

        List<WebElement> items = driver.findElements(By.xpath("//h3[@class='lnk-hvr fnt-16 b block of-hid h-height ml-0 mb-0-imp aro-r txt-ulne']"));
        List<WebElement> prices = driver.findElements(By.xpath("//div[@class='clr-bl']"));
        
        if (items.isEmpty()) {
            items = driver.findElements(By.xpath("//a[contains(@href,'/scooters/')]/ancestor::*[self::li or self::div][1]"));
        }
        
        int idx = 1;
        for (int i = 0; i < items.size(); i++) {
            try {
                String name = items.get(i).getText().trim();
                String price = (i < prices.size()) ? prices.get(i).getText().trim() : "NA";
                
                if (!name.isEmpty()) {
                    rows.add(List.of(String.valueOf(idx++), name, price));
                }
            } catch (Exception ignored) {}
        }
        return rows;
    }
}