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

public class Scenario4_DatasetDownloadTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario4";

    @Test(description = "Scenario 4: Dataset download negative test")
    public void testDownloadDataset() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Open Scholar OneSearch
        driver.get("https://onesearch.library.northeastern.edu/discovery/search?vid=01NEU_INST:NU&lang=en");
        Thread.sleep(2000);
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "01_open_onesearch");

        // Step 2: Click Digital Repository Service
        try {
            WebElement drsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href*='reposit'] span.item-content")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", drsLink);
            Thread.sleep(2000);

            String newTab = "";
            for (String handle : driver.getWindowHandles()) {
                newTab = handle;
            }
            driver.switchTo().window(newTab);
            Thread.sleep(2000);
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "02_click_drs");

        } catch (Exception e) {
            System.out.println("DRS link not found: " + e.getMessage());
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "02_drs_not_found");
        }

        // Step 3: Scroll to Datasets button and click
        System.out.println("\n=== Step 3: Click Datasets ===");

        try {
            // Scroll to find Datasets button
            js.executeScript("window.scrollTo(0, 400)");
            Thread.sleep(1000);

            WebElement datasetsBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/datasets']")));
            js.executeScript("arguments[0].scrollIntoView(true);", datasetsBtn);
            Thread.sleep(1000);

            // Screenshot AFTER scroll, BEFORE click
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "03_before_datasets_click");

            js.executeScript("arguments[0].click();", datasetsBtn);
            Thread.sleep(2000);

            // Screenshot AFTER click
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "04_after_datasets_click");

        } catch (Exception e) {
            System.out.println("Datasets button not found: " + e.getMessage());
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "03_datasets_not_found");
        }

        // Step 4: Try to download dataset
        boolean downloadSucceeded = false;
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "05_before_download_attempt");
        try {
            WebElement zipBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("invalid_zip_button_12345")));
            js.executeScript("arguments[0].scrollIntoView(true);", zipBtn);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", zipBtn);
            Thread.sleep(2000);

            downloadSucceeded = true;
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "06_download_success");

        } catch (Exception e) {
            System.out.println("Download failed: " + e.getMessage());
            ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "06_download_failed");
        }

        Assert.assertTrue(downloadSucceeded, "Dataset download did not complete");
    }
}