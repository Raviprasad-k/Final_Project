package com.zigwheels.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchComparisonPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SearchComparisonPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "homeSearch")
    private WebElement searchBox;

    @FindBy(xpath = "//button[@class='btn search-r-btn']")
    private WebElement searchBtn;

    @FindBy(linkText = "All Mercedes-Benz Cars")
    private WebElement allCarsLink;
    
    @FindBy(xpath = "//div[@id='comparisons-container']")
    private WebElement hover;

    @FindBy(linkText = "GLS vs XC90")
    private WebElement comparisonLink;
    
    @FindBy(xpath = "//a[@id='lEngine']")
    private WebElement engineLink;

    public void searchCar(String brand) {
        searchBox.sendKeys(brand);
        searchBtn.click();
    }

    public void navigateToComparison() throws InterruptedException {
        try {
            if (allCarsLink.isDisplayed()) {
                allCarsLink.click();
            }
        } catch (Exception ignored) {}

        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Scroll to the comparison container so the link is clickable
        js.executeScript("arguments[0].scrollIntoView(true);", hover);
        Thread.sleep(2000); 
        
        comparisonLink.click();
   
        Actions actions = new Actions(driver);
        
        actions.scrollToElement(engineLink).perform();
    }

    /**
     * Refactored Scraper to ensure Column Alignment.
     * Separates the Attribute Label from the Car Specs.
     */
    
    public List<List<String>> scrapeComparisonTable() {
        List<List<String>> data = new ArrayList<>();
        
        // 1. Find the total number of rows first
        By rowLocator = By.xpath("//div[@id='summary']//tr");
        int rowCount = driver.findElements(rowLocator).size();

        for (int i = 0; i < rowCount; i++) {
            try {
                // 2. Re-find the specific row inside the loop so it's always "fresh"
                WebElement currentRow = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rowLocator)).get(i);
                
                // 3. Find cells relative to the fresh row
                List<WebElement> cells = currentRow.findElements(By.xpath("./td[not(contains(@class,'feature')) and not(position()=1)]"));
                
                List<String> rowValues = new ArrayList<>();
                for (WebElement cell : cells) {
                    rowValues.add(cell.getText().trim());
                }
                
                if (!rowValues.isEmpty()) {
                    data.add(rowValues);
                }
            } catch (StaleElementReferenceException e) {
                // 4. If it goes stale, stay on the same index and try again
                i--; 
            }
        }
        return data;
    }
//    public List<List<String>> scrapeComparisonTable() {
//        List<List<String>> data = new ArrayList<>();
//        
//        // Target rows specifically within the summary/comparison table
//        List<WebElement> allRows = driver.findElements(By.xpath("//div[@id='summary']//tr"));
//
//        for (WebElement webRow : allRows) {
//            List<String> rowData = new ArrayList<>();
//            
//            // 1. Get the Attribute Name (Header column)
//            // This is usually the first <th> or <td> in the row
//            List<WebElement> labelCells = webRow.findElements(By.xpath("./th | ./td[1]"));
//            
//            // 2. Get the Car Data (The values for the cars being compared)
//            // We exclude cells that act as headers to prevent duplication
//            List<WebElement> dataCells = webRow.findElements(By.xpath("./td[not(contains(@class,'feature')) and not(position()=1)]"));
//
//            if (!labelCells.isEmpty()) {
//                String attribute = labelCells.get(0).getText().trim();
//                
//                // Only process rows that have actual data values (skips section dividers)
//                if (!dataCells.isEmpty()) {
//                    rowData.add(attribute); // Add to Column A
//                    
//                    for (WebElement cell : dataCells) {
//                        String value = cell.getText().trim();
//                        // Handle empty values to keep columns aligned
//                        rowData.add(value.isEmpty() || value.equals("...") ? "N/A" : value);
//                    }
//                    
//                    data.add(rowData);
//                }
//            }
//        }
//        return data;
//    }
}









//package com.zigwheels.pages;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.interactions.Actions;
//import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.PageFactory;
//import java.util.ArrayList;
//import java.util.List;
//
//public class SearchComparisonPage  {
//    private final WebDriver driver;
//
//    public SearchComparisonPage(WebDriver driver) {
//        this.driver = driver;
//        PageFactory.initElements(driver, this);
//    }
//
//    @FindBy(id = "homeSearch")
//    private WebElement searchBox;
//
//    @FindBy(xpath = "//button[@class='btn search-r-btn']")
//    private WebElement searchBtn;
//
//    @FindBy(linkText = "All Mercedes-Benz Cars")
//    private WebElement allCarsLink;
//    
//    @FindBy(xpath="//div[@id='comparisons-container']")
//    private WebElement hover;
//
//    @FindBy(linkText = "GLS vs XC90")
//    private WebElement comparisonLink;
//
//    public void searchCar(String brand) {
//        searchBox.sendKeys(brand);
//        searchBtn.click();
//    }
//
//    public void navigateToComparison() throws InterruptedException {
//
//        try {
//            if (allCarsLink.isDisplayed()) {
//                allCarsLink.click();
//            }
//        } catch (Exception ignored) {}
//         // wait for page to load
//        new Actions(driver).moveToElement(comparisonLink).click().perform();
//        JavascriptExecutor js=(JavascriptExecutor) driver;
//        js.executeScript("arguments[0].scrollIntoView()",hover);
//        Thread.sleep(2000);
//        comparisonLink.click();
//    }
//
//    public List<List<String>> scrapeComparisonTable() {
//        List<List<String>> data = new ArrayList<>();
//        List<WebElement> allRows = driver.findElements(By.xpath("//div[@id='summary']//tr"));
//
//        for (WebElement webRow : allRows) {
//            List<WebElement> cells = webRow.findElements(By.xpath("./th|./td"));
//            List<String> rowData = new ArrayList<>();
//            for (WebElement cell : cells) {
//                rowData.add(cell.getText().trim());
//            }
//            if (!rowData.isEmpty()) {
//                data.add(rowData);
//            }
//        }
//        return data;
//    }
//}
