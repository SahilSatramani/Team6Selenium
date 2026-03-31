package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DatasetListingPage;
import pages.DigitalRepositoryPage;
import pages.OneSearchPage;
import utils.ExcelReader;
import utils.ScreenShotUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Scenario4_DatasetDownloadTest extends BaseTest {

    @Test
    public void testDownloadDatasetNegativeScenario() {
        String scenarioName = "Scenario4";
        String excelPath = "src/test/resources/testdata/scenario_4_data.xlsx";
        Assert.assertTrue(new File(excelPath).exists(), "Scenario 4 data file not found at: " + excelPath);

        List<Map<String, String>> dataList = ExcelReader.readExcel(excelPath);
        Assert.assertTrue(!dataList.isEmpty(), "No Scenario 4 test data found in Excel");
        Map<String, String> data = dataList.get(0);

        String baseUrl = data.get("baseUrl");
        String expectedFailureMessage = data.get("expectedFailureMessage");
        if (expectedFailureMessage == null || expectedFailureMessage.trim().isEmpty()) {
            expectedFailureMessage = "Scenario 4 is the required negative scenario and must fail.";
        }

        OneSearchPage oneSearchPage = new OneSearchPage(driver);
        DigitalRepositoryPage digitalRepositoryPage = new DigitalRepositoryPage(driver);
        DatasetListingPage datasetListingPage = new DatasetListingPage(driver);

        // Step 1: Open OneSearch
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "01_Before_Open_OneSearch");
        oneSearchPage.open(baseUrl);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "02_After_Open_OneSearch");

        // Step 2: Click Digital Repository Service (opens in new tab)
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "03_Before_Click_DRS");
        oneSearchPage.clickDigitalRepositoryServiceAndSwitchTab();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "04_After_Click_DRS");

        // Step 3: Click Datasets under Featured Content
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "05_Before_Click_Datasets");
        digitalRepositoryPage.clickDatasets();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "06_After_Click_Datasets");

        // Step 4: Open any dataset
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "07_Before_Open_Any_Dataset");
        String datasetName = datasetListingPage.openAnyDataset();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "08_After_Open_Any_Dataset");

        // Step 5: Attempt ZIP click (negative scenario still fails intentionally)
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "09_Before_Click_Zip");
        datasetListingPage.clickZipFile();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "10_After_Click_Zip");

        System.out.println("Scenario4 dataset opened: " + datasetName);
        System.out.println("Scenario4 negative case: ZIP click attempted, then intentional fail.");

        // Assignment requires Scenario 4 to fail.
        Assert.fail(expectedFailureMessage);
    }
}
