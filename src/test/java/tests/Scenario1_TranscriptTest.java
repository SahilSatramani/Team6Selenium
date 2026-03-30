package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.TranscriptPage;
import utils.ExcelReader;
import utils.PasswordUtil;
import utils.ScreenShotUtil;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Scenario1_TranscriptTest extends BaseTest {

    @Test
    public void testDownloadTranscript() {
        String scenarioName = "Scenario1";
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(90));

        // ----------------------------------------------------------------
        // STEP 1: Read credentials from Excel (no hardcoded values)
        // ----------------------------------------------------------------
        System.out.println("Step 1: Reading credentials from Excel");
        String credPath = "src/test/resources/testdata/credentials.xlsx";
        Assert.assertTrue(new File(credPath).exists(),
                "Credentials file not found at: " + credPath);

        List<Map<String, String>> credentials = ExcelReader.readExcel(credPath);
        // Full email stored in Excel e.g. satramani.s@northeastern.edu
        String fullEmail = credentials.get(0).get("Username");
        String password  = PasswordUtil.decode(credentials.get(0).get("Password"));

        // Read transcript settings from Excel
        String transcriptPath = "src/test/resources/testdata/scenario1_data.xlsx";
        Assert.assertTrue(new File(transcriptPath).exists(),
                "Scenario1 data file not found at: " + transcriptPath);

        List<Map<String, String>> s1Data = ExcelReader.readExcel(transcriptPath);
        String transcriptLevel = s1Data.get(0).get("TranscriptLevel"); // e.g. "Graduate"
        String transcriptType  = s1Data.get(0).get("TranscriptType");  // e.g. "Audit Transcript"
        System.out.println("  → Level: " + transcriptLevel + ", Type: " + transcriptType);

        // ----------------------------------------------------------------
        // STEP 2: Navigate to My NEU portal
        // ----------------------------------------------------------------
        System.out.println("Step 2: Navigate to My NEU portal");
        driver.get("https://me.northeastern.edu");
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "01_NEU_Portal");

        // ----------------------------------------------------------------
        // STEP 3: Microsoft login with NEU credentials
        // ----------------------------------------------------------------
        System.out.println("Step 3: Login with NEU credentials (Microsoft SSO)");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(fullEmail, password);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "02_After_Credentials");

        loginPage.clickSignIn();
        System.out.println("  → Sign in clicked, waiting for Duo...");

        // ----------------------------------------------------------------
        // STEP 4: Duo 2FA
        // ----------------------------------------------------------------
        System.out.println("Step 4: Waiting for Duo authentication");
        longWait.until(ExpectedConditions.urlContains("duosecurity.com"));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "03_Duo_Page");

        loginPage.handleDuoDevicePrompt();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "04_After_Duo");

        loginPage.handleStaySignedIn();

        // Wait for Student Hub to load
        longWait.until(ExpectedConditions.urlContains("northeastern.edu"));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "05_Student_Hub");

        Assert.assertTrue(driver.getCurrentUrl().contains("northeastern.edu"),
                "Expected NEU portal but was: " + driver.getCurrentUrl());
        System.out.println("  ✓ On Student Hub");

        // ----------------------------------------------------------------
        // STEP 5: Click Resources tab
        // ----------------------------------------------------------------
        System.out.println("Step 5: Click Resources tab");
        TranscriptPage transcriptPage = new TranscriptPage(driver);
        transcriptPage.clickResourcesTab();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "06_Resources_Tab");

        // ----------------------------------------------------------------
        // STEP 6: Click Academics, Classes & Registration
        // ----------------------------------------------------------------
        System.out.println("Step 6: Click Academics, Classes & Registration");
        transcriptPage.clickAcademicsCategory();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "07_Academics_Category");

        // ----------------------------------------------------------------
        // STEP 7: Click Unofficial Transcript — opens new tab with CAS login
        // ----------------------------------------------------------------
        System.out.println("Step 7: Click Unofficial Transcript link");
        String originalTab = driver.getWindowHandle();
        transcriptPage.clickUnofficialTranscript();

        // Switch to new tab if it opened one
        try {
            longWait.until(d -> d.getWindowHandles().size() > 1);
            transcriptPage.switchToNewTab(originalTab);
            System.out.println("  → New tab URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  → No new tab, staying in same window");
        }
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "08_After_Transcript_Click");

        // ----------------------------------------------------------------
        // STEP 8: Handle NEU CAS login (different from Microsoft login)
        // Uses id="username" and id="password" — username is before @
        // e.g. satramani.s@northeastern.edu → satramani.s
        // ----------------------------------------------------------------
        System.out.println("Step 8: Handle CAS login page");
        transcriptPage.handleCasLogin(fullEmail, password);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "09_After_CAS_Login");

        // Wait for transcript page to load after CAS login
        longWait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("StudentSelfService"),
                ExpectedConditions.urlContains("transcript"),
                ExpectedConditions.urlContains("nubanner")
        ));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "10_Transcript_Page_Loaded");

        // Assert we are on the transcript page
        Assert.assertTrue(
                driver.getCurrentUrl().contains("StudentSelfService")
                || driver.getCurrentUrl().contains("transcript")
                || driver.getCurrentUrl().contains("nubanner"),
                "Expected transcript page but was: " + driver.getCurrentUrl());
        System.out.println("  ✓ On transcript page");

        // ----------------------------------------------------------------
        // STEP 9: Select Transcript Level and Type from Excel
        // ----------------------------------------------------------------
        System.out.println("Step 9: Select Transcript Level and Type");
        transcriptPage.selectTranscriptLevel(transcriptLevel);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "11_Level_Selected");

        transcriptPage.selectTranscriptType(transcriptType);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "12_Type_Selected");

        // Give the transcript content time to refresh after dropdown selection
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Assert transcript content is visible on page
        String pageSource = driver.getPageSource();
        Assert.assertTrue(
                pageSource.contains("Student Information")
                || pageSource.contains("Curriculum Information")
                || pageSource.contains("Sahil"),
                "Transcript content not found on page after selecting level/type");
        System.out.println("  ✓ Transcript content verified");
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "13_Transcript_Verified");

        // ----------------------------------------------------------------
        // STEP 10: Right-click → Print → Save as PDF
        // Uses Cmd+P keyboard shortcut → Enter (Save) → Enter (confirm)
        // ----------------------------------------------------------------
        System.out.println("Step 10: Save transcript as PDF (Cmd+P → Save)");
        transcriptPage.printPageAsPdf();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "14_After_PDF_Save");

        System.out.println("✓ Scenario 1 PASSED: Transcript downloaded successfully");
    }
}