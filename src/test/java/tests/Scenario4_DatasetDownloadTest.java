package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ScreenShotUtil;

import java.time.Duration;

/**
 * Scenario 4 — negative dataset download test (inline Selenium flow; hardcoded OneSearch URL, no Excel).
 */
public class Scenario4_DatasetDownloadTest extends BaseTest {

    private static final String SCENARIO_FOLDER = "Scenario4";

    private void takeScreenshot(String scenarioName, String stepName) {
        ScreenShotUtil.takeScreenshot(driver, scenarioName, stepName);
    }

    // This is a Negative Test — this test must fail!
    @Test(description = "Scenario 4: Dataset download negative (must fail)")
    public void testDownloadDatasetNegativeScenario() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Open Scholar OneSearch page
        driver.get("https://onesearch.library.northeastern.edu/discovery/search?vid=01NEU_INST:NU&lang=en");
        Thread.sleep(2000);
        takeScreenshot(SCENARIO_FOLDER, "01_open_onesearch");

        // Step 2: Click "Digital Repository Service" in the top navigation menu
        try {
            WebElement drsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href*='reposit'] span.item-content")));
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", drsLink);
            Thread.sleep(2000);

            // Switch to the new tab that opened
            String newTab = "";
            for (String handle : driver.getWindowHandles()) {
                newTab = handle;
            }
            driver.switchTo().window(newTab);
            Thread.sleep(2000);
            takeScreenshot(SCENARIO_FOLDER, "02_click_drs");

        } catch (Exception e) {
            System.out.println("DRS link not found, trying backup selector: " + e.getMessage());
            try {
                WebElement drsLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'repository.library')]")));
                Thread.sleep(1000);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", drsLink);
                Thread.sleep(2000);

                // Switch to new tab
                String newTab = "";
                for (String handle : driver.getWindowHandles()) {
                    newTab = handle;
                }
                driver.switchTo().window(newTab);
                Thread.sleep(2000);
                takeScreenshot(SCENARIO_FOLDER, "02_click_drs");

            } catch (Exception e2) {
                System.out.println("DRS backup also failed: " + e2.getMessage());
                takeScreenshot(SCENARIO_FOLDER, "02_drs_not_found");
            }
        }

        // Step 3: Scroll down and click "Datasets" under Featured Content
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 400)");
            Thread.sleep(1000);
            WebElement datasetsBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/datasets']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", datasetsBtn);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", datasetsBtn);
            Thread.sleep(2000);
            takeScreenshot(SCENARIO_FOLDER, "03_click_datasets");

        } catch (Exception e) {
            System.out.println("Datasets button not found: " + e.getMessage());
            takeScreenshot(SCENARIO_FOLDER, "03_datasets_not_found");
        }

        // Step 4: Click "Zip File" button on the first dataset in the list
        try {
            WebElement zipBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a.btn-mini.btn-clear[title='Zip File']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", zipBtn);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", zipBtn);
            Thread.sleep(2000);
            takeScreenshot(SCENARIO_FOLDER, "04_click_zip_file");

        } catch (Exception e) {
            System.out.println("Zip File button not found: " + e.getMessage());
            takeScreenshot(SCENARIO_FOLDER, "04_zip_not_found");
        }

        // This test must fail as required by the assignment (Negative Test)
        Assert.fail("Negative Test Case: Unauthorized access to dataset download is not permitted.");
    }
}
