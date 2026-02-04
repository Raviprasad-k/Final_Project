package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopularCarsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public PopularCarsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    public List<List<String>> scrapeNamesAndPrices() {
        List<List<String>> rows = new ArrayList<>();

        // Jenkins Fix: Wait up to 10-20 seconds for at least one car card to be visible
        // Without this, findElements returns an empty list immediately if the page is slow.
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[contains(@class,'modelItem')]")));
        } catch (TimeoutException e) {
            // Fallback wait for the broader container if the primary one isn't showing
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'/new-cars/')]")));
            } catch (Exception ignored) {}
        }

        // 1) Try primary card containers
        List<WebElement> items = driver.findElements(By.xpath("//li[contains(@class,'modelItem')]"));

        // 2) Fallback: broader container if primary not found
        if (items.isEmpty()) {
            items = driver.findElements(By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]"));
        }

        int idx = 1;
        Set<String> seenNames = new HashSet<>();

        for (WebElement it : items) {
            try {
                // Name lookup
                WebElement nameEl = it.findElement(By.xpath(
                        ".//a[contains(@class,'ht-name') or contains(@class,'lnk-hvr') or contains(@href,'/new-cars/')]"));
                String name = nameEl.getText().trim();

                if (name.isEmpty() || seenNames.contains(name)) {
                    continue; 
                }

                String price = "NA";
                try {
                    // Price lookup
                    WebElement priceEl = it.findElement(By.xpath(
                            ".//span[normalize-space(@title)='Ex-Showroom Price' or contains(normalize-space(.),'Ex-Showroom Price') or contains(normalize-space(@class),'price')]"));
                    String priceText = priceEl.getText().trim();
                    if (!priceText.isEmpty()) {
                        price = priceText;
                    }
                } catch (NoSuchElementException ignore) {
                    // Leave price as "NA"
                }

                rows.add(List.of(String.valueOf(idx++), name, price));
                seenNames.add(name);

            } catch (NoSuchElementException ignore) {
                // Item structure mismatch, skip safely
            } catch (StaleElementReferenceException sere) {
                // Page refreshed or shifted, skip this specific card
            } catch (Exception e) {
                // Catch-all for unexpected errors while parsing a card
            }
        }

        return rows;
    }
}


//
//package com.zigwheels.pages;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.PageFactory;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class PopularCarsPage {
//    private final WebDriver driver;
//
//    public PopularCarsPage(WebDriver driver) {
//        this.driver = driver;
//        PageFactory.initElements(driver, this);
//    }
//
//
//    public List<List<String>> scrapeNamesAndPrices() {
//        List<List<String>> rows = new ArrayList<>();
//
//        // 1) Try primary card containers
//        List<WebElement> items = driver.findElements(
//                By.xpath("//li[contains(@class,'modelItem')]")
//        );
//
//        // 2) Fallback: broader container if primary not found
//        if (items.isEmpty()) {
//            items = driver.findElements(
//                    By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]")
//            );
//        }
//
//        int idx = 1;
//        Set<String> seenNames = new HashSet<>();
//
//        for (WebElement it : items) {
//            try {
//                // RELATIVE lookup: note the leading dot in .//
//                // Name: be tolerant with classes and pick the first link that looks like the model name
//                WebElement nameEl = it.findElement(By.xpath(
//                        ".//a[contains(@class,'ht-name') or contains(@class,'lnk-hvr') or contains(@href,'/new-cars/')]"
//                ));
//                String name = nameEl.getText().trim();
//
//                if (name.isEmpty() || seenNames.contains(name)) {
//                    continue; // skip blanks and duplicates
//                }
//
//                String price = "NA";
//                try {
//                    // Price: normalize title (avoid leading/trailing spaces)
//                    WebElement priceEl = it.findElement(By.xpath(
//                            ".//span[normalize-space(@title)='Ex-Showroom Price' or contains(normalize-space(.),'Ex-Showroom Price') or contains(normalize-space(@class),'price')]"
//                    ));
//                    String priceText = priceEl.getText().trim();
//                    if (!priceText.isEmpty()) {
//                        price = priceText;
//                    }
//                } catch (NoSuchElementException ignore) {
//                    // Leave price as "NA"
//                }
//
//                rows.add(List.of(String.valueOf(idx++), name, price));
//                seenNames.add(name);
//
//            } catch (NoSuchElementException ignore) {
//                // This item doesn't have a name in the expected structure; skip
//            } catch (StaleElementReferenceException sere) {
//                // The page updated; skip this card safely
//            } catch (Exception e) {
////                .debug("Item parse error: {}", e.getMessage());
//            }
//        }
//
//        return rows;
//    }
//
//
//}
