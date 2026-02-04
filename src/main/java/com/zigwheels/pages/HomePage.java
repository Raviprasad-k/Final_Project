
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

    public void goHome() {
        try { logo.click(); } catch (Exception e) { driver.navigate().to("https://www.zigwheels.com"); }
    }

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

    public void openUpcomingBikes() {
        try {
            hover(newBikesMenu);
            upcomingBikes.click();
        } catch (Exception e) {
            driver.navigate().to("https://www.zigwheels.com/upcoming-bikes");
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

    public void openProfile() { profileIcon.click(); }

    public static class LinkData {
        public final String text; public final String href; public final String status; public final String code;
        public LinkData(String t, String h, String s, String c) { text=t; href=h; status=s; code=c; }
    }

//    public List<LinkData> collectAllLinksAndValidate() {
//        List<WebElement> links = driver.findElements(By.tagName("a"));
//        List<LinkData> data = new ArrayList<>();
//        for (WebElement a : links) {
//            String txt = a.getText().trim();
//            String href = a.getAttribute("href");
//            if (href == null || href.isEmpty() || href.startsWith("javascript") || href.startsWith("mailto:")) {
//                data.add(new LinkData(txt, String.valueOf(href), "INVALID", ""));
//                continue;
//            }
//            int code = -1;
//            try {
//                HttpURLConnection con = (HttpURLConnection) new URL(href).openConnection();
//                con.setRequestMethod("HEAD");
//                con.setConnectTimeout(5000);
//                con.setReadTimeout(5000);
//                code = con.getResponseCode();
//            } catch (Exception ignored) {}
//            String status = (code >= 200 && code < 400) ? "OK" : "BROKEN";
//            data.add(new LinkData(txt, href, status, code == -1 ? "" : String.valueOf(code)));
//        }
//        // de-duplicate by href
//        Map<String, LinkData> map = new LinkedHashMap<>();
//        for (LinkData ld : data) {
//            map.putIfAbsent(ld.href, ld);
//        }
//        return new ArrayList<>(map.values());
//    }


    public List<LinkData> collectMeaningfulLinksAndValidate() {

        List<WebElement> links = driver.findElements(By.tagName("a"));
        List<LinkData> data = new ArrayList<>();

        Set<String> seenTexts = new HashSet<>();
        int maxLinks = 20; // Limit to avoid slow execution

        for (WebElement a : links) {
            if (data.size() >= maxLinks) break;

            String txt = a.getText().trim();
            String href = a.getAttribute("href");

            // Skip useless entries
            if (txt.isEmpty()) continue;
            if (seenTexts.contains(txt)) continue;
            if (href == null || href.startsWith("javascript") || href.startsWith("mailto:")) continue;

            seenTexts.add(txt);

            int code = -1;
            String status;

            try {
                HttpURLConnection con = (HttpURLConnection) new URL(href).openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                code = con.getResponseCode();
            } catch (Exception ignored) {}

            status = (code >= 200 && code < 400) ? "OK" : "BROKEN";

            data.add(new LinkData(txt, href, status, code == -1 ? "" : String.valueOf(code)));
        }

        return data;
    }

}
