
package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

public class UpcomingBikesPage {
    private final WebDriver driver;

    public UpcomingBikesPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public List<List<String>> scrapeUpcomingBikes() {
        List<List<String>> rows = new ArrayList<>();
        List<WebElement> items = driver.findElements(By.xpath(
                "//li[@class='col-lg-4 txt-c rel modelItem ']"));
        System.out.println("items size"+items.size());
//        if (items.isEmpty()) {
//            items = driver.findElements(By.xpath("//a[contains(@href,'/bikes/')]/ancestor::*[self::li or self::div][1]"));
//        }
        int idx=1;
        for (WebElement it : items) {
            try {
                WebElement nameEl = it.findElement(By.xpath("(//strong[@class='lnk-hvr block of-hid h-height txt-ulne'])["+idx+"]"));
                String name = nameEl.getText().trim();
                String price = "";
                String launch = "";
                try {
                    WebElement priceEl = it.findElement(By.xpath(
                            "(//div[@class='b fnt-15'])["+idx+"]"));
                    price = priceEl.getText().trim();
                } catch (NoSuchElementException nse) { price = "NA"; }
                try {
                    WebElement launchEl = it.findElement(By.xpath(
                            "(//div[@class='clr-try fnt-14'])["+idx+"]"));
                    launch = launchEl.getText().trim();
                } catch (NoSuchElementException nse) { launch = "NA"; }
                if (!name.isEmpty()) rows.add(List.of(String.valueOf(idx++), name, price, launch));
            } catch (Exception ignored) {}
        }
        return rows;
    }
}
