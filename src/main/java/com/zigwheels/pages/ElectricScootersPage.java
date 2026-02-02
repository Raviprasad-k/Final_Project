
package com.zigwheels.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.ArrayList;
import java.util.List;

public class ElectricScootersPage {
    private final WebDriver driver;

    public ElectricScootersPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "*//div[@class='ht-20' and normalize-space()='Ola Electric']")
    private WebElement olaFilter;
    
    @FindBy(xpath = "//a[@title='Ola Electric Scooters']") private WebElement olalink;
    //div[@class='ht-20' and normalize-space()='Ola Electric']
    public void filterOLA() throws InterruptedException {
//        try { olaFilter.click(); } catch (Exception e) {
//
//        }
    	
    	 Actions actions = new Actions(driver);
         
         actions.scrollToElement(olalink).perform();
        JavascriptExecutor e=(JavascriptExecutor) driver;
        e.executeScript("arguments[0].scrollIntoView()",olaFilter);
        Thread.sleep(2000);
        e.executeScript("arguments[0].click();",olaFilter);
//        olaFilter.click();
    }

    public List<List<String>> scrapeModels() {
        List<List<String>> rows = new ArrayList<>();
        List<WebElement> items = driver.findElements(By.xpath("//h3[@class='lnk-hvr fnt-16 b block of-hid h-height ml-0 mb-0-imp aro-r txt-ulne']"));
        List<WebElement> prices=driver.findElements(By.xpath("//div[@class='clr-bl']"));
        if (items.isEmpty()) {
            items = driver.findElements(By.xpath("//a[contains(@href,'/scooters/')]/ancestor::*[self::li or self::div][1]"));
        }
        int idx=1;
        for(int i=0;i<items.size();i++){
            try {
                //WebElement nameEl = it.findElement(By.xpath(".//a[1]"));
                WebElement it=items.get(i);
                WebElement pt=prices.get(i);
                String name = it.getText().trim();
                String price = "";
                try {
                    //WebElement priceEl = driver.findElement(By.xpath(".//*[contains(text(),'Rs')"));
                    price = pt.getText().trim();
                } catch (NoSuchElementException nse) { price = "NA"; }
                if (!name.isEmpty()) rows.add(List.of(String.valueOf(idx++), name, price));
            } catch (Exception ignored) {}

        }
//        for(int i=0;i<items.size();i++){
//            String price="";
//            try {
//                    WebElement priceEl = driver.findElement(By.xpath("//div[@class='clr-bl']"));
//                    price = priceEl.getText().trim();
//                } catch (NoSuchElementException nse) { price = "NA"; }
//        }

        return rows;
    }
}
