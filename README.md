
# ZigWheels Automation (Selenium + TestNG + POM + PageFactory)

This project automates 5 real-world scenarios on **zigwheels.com** with:

- **Selenium 4**, **TestNG**, **Page Object Model (POM)** with **PageFactory**
- **Apache POI** for Excel outputs
- **log4j2** rolling file logs
- **Parallel multi-browser** execution (Chrome + Edge) using **WebDriverManager**
- **Screenshots at every stage**

## Scenarios Covered
1. **Homepage**: Collect all links, validate each URL (HTTP HEAD), and write to Excel.
2. **New Cars → Popular Cars**: Scrape car names + prices to Excel, return to home.
3. **New Bikes → Upcoming Bikes**: Scrape bike name, price, expected launch to Excel, return to home.
4. **Scooters → Electric Scooters → OLA**: Filter by OLA, scrape model & price, return to home.
5. **More → Used Cars (Chennai)**: Select city, sort by *Price: High to Low*, write car name/price/fuel; click *View Seller Details* of first car, submit wrong number, capture error, return to home.
6. **Profile → Google**: Try invalid Google login and capture error message.

> All steps save screenshots under `screenshots/` and write results under `artifacts/zigwheels_automation_<browser>.xlsx`.

## Tech Stack
- Java 17, Maven
- Selenium 4.25.0, TestNG 7.10.2
- WebDriverManager 5.9.2 (no manual driver setup needed)
- Apache POI 5.2.5
- log4j2 2.22.1

## Quick Start (IntelliJ IDEA)
1. **Import** this folder as a **Maven project**.
2. Ensure JDK **17** is configured.
3. Open `testng.xml` and click **Run** (or use Maven command below).

### Run from terminal
```bash
mvn -q clean test -Dsurefire.suiteXmlFiles=testng.xml
```

### Toggle headless mode
Set in `src/main/resources/config.properties`:
```
headless=true
```

## Outputs
- **Excel**: `artifacts/zigwheels_automation_<browser>.xlsx`
  - Sheets: `HomeLinks`, `PopularCars`, `UpcomingBikes`, `ElectricScooters_OLA`, `UsedCars_Chennai`, `UsedCars_Chennai_Error`, `GoogleLogin_Error`
- **Screenshots**: `screenshots/*` (each major step and failures)
- **Logs**: `logs/automation.log` with rolling policy

## Notes & Known Behaviors
- Some menus are hover-driven; code uses Actions with fallbacks to direct URLs if hover UI changes.
- URL validation uses **HTTP HEAD** and marks links `OK` for 2xx/3xx, `BROKEN` otherwise (some servers disable HEAD, so those may appear broken).
- Google Auth sometimes blocks automation; an informative message is still captured and written to Excel.
- If a cookie or location banner appears on your system, it may slightly alter the DOM. The locators are intentionally robust (using text contains), but you can add a quick dismiss in `HomePage` if needed.

## Structure
```
zigwheels-automation-pom/
 ├─ pom.xml
 ├─ testng.xml
 ├─ src/
 │  ├─ main/java/com/zigwheels/
 │  │  ├─ base/ (BaseTest, DriverFactory)
 │  │  ├─ pages/ (HomePage, PopularCarsPage, UpcomingBikesPage, ElectricScootersPage, UsedCarsPage, ProfilePage)
 │  │  ├─ utils/ (ConfigReader, ExcelUtils, ScreenshotUtils, WaitUtils)
 │  │  └─ listeners/ (TestListener)
 │  └─ test/java/com/zigwheels/tests/ (ZigWheelsScenariosTest)
 ├─ src/main/resources/ (config.properties, log4j2.xml)
 ├─ artifacts/ (Excel written at runtime)
 ├─ logs/ (runtime)
 └─ screenshots/ (runtime)
```

## Extending
- Add **Firefox**: duplicate a `<test>` block in `testng.xml` with `<parameter name="browser" value="firefox"/>`.
- Reports: integrate **Allure** or **Extent Reports** easily via listeners.

---
Happy testing! If you want me to switch to explicit **data provider**-driven tests, or split scenarios into individual classes, tell me your preferred structure and I’ll refactor it.
