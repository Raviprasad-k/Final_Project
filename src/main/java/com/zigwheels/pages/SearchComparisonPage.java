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
    
    @FindBy(xpath = "//a[@id='Engine']")
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
        js.executeScript("arguments[0].scrollIntoView(true);", hover);
         
        comparisonLink.click();
   
        Actions actions = new Actions(driver);
        actions.scrollToElement(engineLink).perform();
    }

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
                i--; 
            }
        }
        return data;
    }
}