
package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopularCarsPage {
    private final WebDriver driver;

    public PopularCarsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

//    @FindBy(xpath = "//h1[contains(.,'Popular Cars') or contains(.,'Top') or contains(.,'Best')] | //h2[contains(.,'Popular Cars')]")
//    private WebElement header;

//    public List<List<String>> scrapeNamesAndPrices() {
//        List<List<String>> rows = new ArrayList<>();
//        List<WebElement> items = driver.findElements(By.xpath("//li[@class='col-lg-6 col-xs-12 pl-0 pr-15 txt-c rel modelItem ']"));
//        if (items.isEmpty()) {
//            items = driver.findElements(By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]"));
//        }
//        int idx=1;
//        for (WebElement it : items) {
//            try {
//                WebElement nameEl = it.findElement(By.xpath("//a[@class='fnt-14 lnk-hvr of-hid ht-name txt-ulne'][1]"));
//                String name = nameEl.getText().trim();
//                String price = "";
//                try {
//                    WebElement priceEl = it.findElement(By.xpath(
//                            "//span[@title=' Ex-Showroom Price'][1]"));
//                    price = priceEl.getText().trim();
//                } catch (NoSuchElementException nse) { price = "NA"; }
//                if (!name.isEmpty()) rows.add(List.of(String.valueOf(idx++), name, price));
//            } catch (Exception ignored) {}
//        }
//        return rows;
//    }


    public List<List<String>> scrapeNamesAndPrices() {
        List<List<String>> rows = new ArrayList<>();

        // 1) Try primary card containers
        List<WebElement> items = driver.findElements(
                By.xpath("//li[contains(@class,'modelItem')]")
        );

        // 2) Fallback: broader container if primary not found
        if (items.isEmpty()) {
            items = driver.findElements(
                    By.xpath("//a[contains(@href,'/new-cars/')]/ancestor::*[self::li or self::div][1]")
            );
        }

        int idx = 1;
        Set<String> seenNames = new HashSet<>();

        for (WebElement it : items) {
            try {
                // RELATIVE lookup: note the leading dot in .//
                // Name: be tolerant with classes and pick the first link that looks like the model name
                WebElement nameEl = it.findElement(By.xpath(
                        ".//a[contains(@class,'ht-name') or contains(@class,'lnk-hvr') or contains(@href,'/new-cars/')]"
                ));
                String name = nameEl.getText().trim();

                if (name.isEmpty() || seenNames.contains(name)) {
                    continue; // skip blanks and duplicates
                }

                String price = "NA";
                try {
                    // Price: normalize title (avoid leading/trailing spaces)
                    WebElement priceEl = it.findElement(By.xpath(
                            ".//span[normalize-space(@title)='Ex-Showroom Price' or contains(normalize-space(.),'Ex-Showroom Price') or contains(normalize-space(@class),'price')]"
                    ));
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
                // This item doesn't have a name in the expected structure; skip
            } catch (StaleElementReferenceException sere) {
                // The page updated; skip this card safely
            } catch (Exception e) {
//                .debug("Item parse error: {}", e.getMessage());
            }
        }

        return rows;
    }


}
