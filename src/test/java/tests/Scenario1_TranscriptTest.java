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
        // STEP 1: Read credentials and transcript settings from Excel
        // ----------------------------------------------------------------
        System.out.println("Step 1: Reading data from Excel");
        String credPath = "src/test/resources/testdata/credentials.xlsx";
        Assert.assertTrue(new File(credPath).exists(), "credentials.xlsx not found");

        List<Map<String, String>> creds = ExcelReader.readExcel(credPath);
        String fullEmail = creds.get(0).get("Username");
        String password  = PasswordUtil.decode(creds.get(0).get("Password"));

        String s1Path = "src/test/resources/testdata/scenario1_data.xlsx";
        Assert.assertTrue(new File(s1Path).exists(), "scenario1_data.xlsx not found");

        List<Map<String, String>> s1Data = ExcelReader.readExcel(s1Path);
        String transcriptLevel = s1Data.get(0).get("TranscriptLevel");
        String transcriptType  = s1Data.get(0).get("TranscriptType");
        System.out.println("  → Level: " + transcriptLevel + ", Type: " + transcriptType);

        // ----------------------------------------------------------------
        // STEP 2: Navigate to My NEU portal
        // ----------------------------------------------------------------
        System.out.println("Step 2: Navigate to My NEU portal");
        driver.get("https://me.northeastern.edu");
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "01_NEU_Portal");

        // ----------------------------------------------------------------
        // STEP 3: Microsoft SSO login
        // ----------------------------------------------------------------
        System.out.println("Step 3: Login (Microsoft SSO)");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(fullEmail, password);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "02_Credentials_Entered");

        loginPage.clickSignIn();
        System.out.println("  → Waiting for Duo...");

        // ----------------------------------------------------------------
        // STEP 4: Duo 2FA
        // ----------------------------------------------------------------
        System.out.println("Step 4: Duo authentication");
        longWait.until(ExpectedConditions.urlContains("duosecurity.com"));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "03_Duo_Page");

        loginPage.handleDuoDevicePrompt();
        loginPage.handleStaySignedIn();

        longWait.until(ExpectedConditions.urlContains("northeastern.edu"));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "04_Student_Hub");

        Assert.assertTrue(driver.getCurrentUrl().contains("northeastern.edu"),
            "Not on Student Hub: " + driver.getCurrentUrl());
        System.out.println("  ✓ On Student Hub");

        // ----------------------------------------------------------------
        // STEP 5: Resources → Academics, Classes & Registration
        // ----------------------------------------------------------------
        System.out.println("Step 5: Click Resources tab");
        TranscriptPage tp = new TranscriptPage(driver);
        tp.clickResourcesTab();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "05_Resources");

        System.out.println("Step 6: Click Academics, Classes & Registration");
        tp.clickAcademicsCategory();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "06_Academics");

        // ----------------------------------------------------------------
        // STEP 6: Click Unofficial Transcript → new tab → CAS login
        // ----------------------------------------------------------------
        System.out.println("Step 7: Click Unofficial Transcript");
        String originalTab = driver.getWindowHandle();
        tp.clickUnofficialTranscript();

        try {
            longWait.until(d -> d.getWindowHandles().size() > 1);
            tp.switchToNewTab(originalTab);
            System.out.println("  → New tab: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  → Same tab: " + driver.getCurrentUrl());
        }
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "07_Transcript_Tab");

        // ----------------------------------------------------------------
        // STEP 7: CAS login (separate NEU SSO — not Microsoft)
        // ----------------------------------------------------------------
        System.out.println("Step 8: CAS login");
        tp.handleCasLogin(fullEmail, password);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "08_After_CAS");

        longWait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("StudentSelfService"),
            ExpectedConditions.urlContains("nubanner")
        ));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "09_Transcript_Page");

        Assert.assertTrue(
            driver.getCurrentUrl().contains("StudentSelfService")
            || driver.getCurrentUrl().contains("nubanner"),
            "Not on transcript page: " + driver.getCurrentUrl());
        System.out.println("  ✓ On transcript page");

        // ----------------------------------------------------------------
        // STEP 8: Select Graduate + Audit Transcript from Excel data
        // ----------------------------------------------------------------
        System.out.println("Step 9: Select Transcript Level and Type");
        tp.selectTranscriptLevel(transcriptLevel);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "10_Level_Selected");

        tp.selectTranscriptType(transcriptType);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "11_Type_Selected");

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Assert transcript content visible
        String src = driver.getPageSource();
        Assert.assertTrue(
            src.contains("Student Information") || src.contains("Curriculum"),
            "Transcript content not found");
        System.out.println("  ✓ Transcript content verified");
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "12_Transcript_Content");

        // ----------------------------------------------------------------
        // STEP 9: Save as PDF using CDP (saves to transcripts/ folder)
        // ----------------------------------------------------------------
        System.out.println("Step 10: Save transcript as PDF");
        tp.printPageAsPdf();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "13_PDF_Saved");

        // Assert PDF was created
        File pdfFile = new File("transcripts/Academic_Transcript.pdf");
        Assert.assertTrue(pdfFile.exists() && pdfFile.length() > 0,
            "PDF file was not created");

        System.out.println("✓ Scenario 1 PASSED: Transcript saved as PDF");
    }
}