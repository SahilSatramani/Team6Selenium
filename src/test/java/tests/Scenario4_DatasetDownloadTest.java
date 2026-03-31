package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DatasetListingPage;
import pages.DigitalRepositoryPage;
import pages.OneSearchPage;
import utils.ExcelReader;
import utils.ExtentManager;
import utils.ScreenShotUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Scenario4_DatasetDownloadTest extends BaseTest {

    @Test(description = "Scenario 4: Download a dataset (negative scenario that must fail)")
    public void scenario4_downloadDataset_negative() throws Exception {
        Map<String, String> data = ExcelReader.readFirstDataRowByHeader("/testdata/scenario4_data.xlsx");

        String scenarioFolder = value(data, "scenarioScreenshotFolder", "Scenario4_DownloadDataset");
        Path scenarioDir = Paths.get("screenshots", scenarioFolder);
        long timeoutSeconds = parseLong(value(data, "implicitWaitSeconds", "10"), 10L);

        String baseUrl = require(data, "baseUrl");
        String drsText = require(data, "drsLinkText");
        String datasetsText = require(data, "datasetsLinkText");
        String expectedFailMessage = value(
                data,
                "expectedFailureMessage",
                "Scenario 4 is a required negative scenario and must fail by design."
        );

        OneSearchPage oneSearchPage = new OneSearchPage(driver, timeoutSeconds);
        DigitalRepositoryPage digitalRepositoryPage = new DigitalRepositoryPage(driver, timeoutSeconds);
        DatasetListingPage datasetListingPage = new DatasetListingPage(driver, timeoutSeconds);

        String actual = "";
        try {
            ScreenShotUtil.capture(driver, scenarioDir, "stepA_openOneSearch", "before");
            oneSearchPage.open(baseUrl);
            ScreenShotUtil.capture(driver, scenarioDir, "stepA_openOneSearch", "after");

            ScreenShotUtil.capture(driver, scenarioDir, "stepB_clickDigitalRepositoryService", "before");
            oneSearchPage.clickDigitalRepositoryService(drsText);
            ScreenShotUtil.capture(driver, scenarioDir, "stepB_clickDigitalRepositoryService", "after");

            ScreenShotUtil.capture(driver, scenarioDir, "stepC_clickDatasets", "before");
            digitalRepositoryPage.clickDatasets(datasetsText);
            ScreenShotUtil.capture(driver, scenarioDir, "stepC_clickDatasets", "after");

            ScreenShotUtil.capture(driver, scenarioDir, "stepD_openAnyDataset", "before");
            String chosenDataset = datasetListingPage.openAnyDataset();
            ScreenShotUtil.capture(driver, scenarioDir, "stepD_openAnyDataset", "after");

            ScreenShotUtil.capture(driver, scenarioDir, "stepE_clickZipFile", "before");
            datasetListingPage.clickZipFile();
            ScreenShotUtil.capture(driver, scenarioDir, "stepE_clickZipFile", "after");

            if (datasetListingPage.isLoginPromptVisible()) {
                actual = "Login prompt displayed after ZIP click for dataset: " + chosenDataset;
                logAssignmentSummary(
                        "Scenario 4: Download a Dataset (Negative)",
                        actual,
                        expectedFailMessage,
                        "Fail"
                );
                Assert.fail(expectedFailMessage);
            }

            actual = "ZIP clicked without login prompt for dataset: " + chosenDataset + " | URL: " + driver.getCurrentUrl();
            logAssignmentSummary(
                    "Scenario 4: Download a Dataset (Negative)",
                    actual,
                    expectedFailMessage,
                    "Fail"
            );
            Assert.fail(expectedFailMessage);
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            actual = "Execution error: " + e.getMessage();
            logAssignmentSummary(
                    "Scenario 4: Download a Dataset (Negative)",
                    actual,
                    expectedFailMessage,
                    "Fail"
            );
            throw e;
        }
    }

    private static void logAssignmentSummary(String scenarioName, String actual, String expected, String passFail) {
        ExtentTest t = ExtentManager.getTest();
        if (t == null) {
            return;
        }
        t.log(Status.INFO, "<b>Test scenario name:</b> " + scenarioName);
        t.log(Status.INFO, "<b>Actual:</b> " + actual);
        t.log(Status.INFO, "<b>Expected:</b> " + expected);
        t.log(Status.INFO, "<b>Pass/Fail:</b> " + passFail);
    }

    private static String require(Map<String, String> data, String key) {
        String v = data.get(key);
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required spreadsheet value for: " + key);
        }
        return v.trim();
    }

    private static String value(Map<String, String> data, String key, String defaultValue) {
        String v = data.get(key);
        if (v == null || v.trim().isEmpty()) {
            return defaultValue;
        }
        return v.trim();
    }

    private static long parseLong(String text, long fallback) {
        try {
            return Long.parseLong(text);
        } catch (Exception ex) {
            return fallback;
        }
    }
}
