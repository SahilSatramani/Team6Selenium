package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AcademicCalendarPage;
import pages.LoginPage;
import utils.ExcelReader;
import utils.PasswordUtil;
import utils.ScreenShotUtil;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Scenario5_AcademicCalendarTest extends BaseTest {

    @Test
    public void testUpdateAcademicCalendar() {
        String scenarioName = "Scenario5";
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(90));

        // ----------------------------------------------------------------
        // STEP 1: Read credentials from Excel (no hardcoded values)
        // ----------------------------------------------------------------
        System.out.println("Step 1: Reading credentials from Excel");
        String credPath = "src/test/resources/testdata/credentials.xlsx";
        Assert.assertTrue(new File(credPath).exists(), "Credentials file not found at: " + credPath);

        List<Map<String, String>> credentials = ExcelReader.readExcel(credPath);
        String username = credentials.get(0).get("Username");
        String password = PasswordUtil.decode(credentials.get(0).get("Password"));

        // ----------------------------------------------------------------
        // STEP 2: Navigate to Student Hub
        // ----------------------------------------------------------------
        System.out.println("Step 2: Navigate to Northeastern Student Hub");
        driver.get("https://student.me.northeastern.edu/");
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "01_Before_Login");

        // ----------------------------------------------------------------
        // STEP 3: Login
        // ----------------------------------------------------------------
        System.out.println("Step 3: Login with NEU credentials");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "02_After_Entering_Credentials");

        loginPage.clickSignIn();
        System.out.println("  → Sign in clicked, waiting for Duo...");

        // ----------------------------------------------------------------
        // STEP 4: Duo 2FA (manual Enter key press allowed once)
        // ----------------------------------------------------------------
        System.out.println("Step 4: Waiting for Duo authentication");
        longWait.until(ExpectedConditions.urlContains("duosecurity.com"));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "03_Duo_Page");

        loginPage.handleDuoDevicePrompt();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "04_After_Duo");

        loginPage.handleStaySignedIn();

        // Wait for Student Hub to fully load
        longWait.until(ExpectedConditions.urlContains("northeastern.edu"));
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "05_Student_Hub_Loaded");

        Assert.assertTrue(driver.getCurrentUrl().contains("northeastern.edu"),
                "Expected to be on Student Hub");
        System.out.println("  ✓ Logged in to Student Hub");

        // ----------------------------------------------------------------
        // STEP 5: Click Resources tab
        // ----------------------------------------------------------------
        System.out.println("Step 5: Click Resources tab");
        AcademicCalendarPage calPage = new AcademicCalendarPage(driver);
        calPage.clickResourcesTab();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "06_Resources_Tab");

        // ----------------------------------------------------------------
        // STEP 6: Click Academics, Classes & Registration
        // ----------------------------------------------------------------
        System.out.println("Step 6: Click Academics, Classes & Registration");
        calPage.clickAcademicsCategory();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "07_Academics_Category");

        // ----------------------------------------------------------------
        // STEP 7: Click Academic Calendar link — may open a new tab
        // ----------------------------------------------------------------
        System.out.println("Step 7: Click Academic Calendar link");
        String originalTab = driver.getWindowHandle();
        calPage.clickAcademicCalendarLink();

        // Wait up to 10s for a new tab to open, then switch to it
        try {
            longWait.until(d -> d.getWindowHandles().size() > 1);
            calPage.switchToNewTab(originalTab);
            System.out.println("  → Now on: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("  → No new tab, continuing in same window");
        }

        // Wait for Registrar page to load
        longWait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("Calendar"),
                ExpectedConditions.urlContains("registrar")
        ));
        System.out.println("  → Registrar page loaded: " + driver.getCurrentUrl());
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "08_Registrar_Calendar_Page");

        // ----------------------------------------------------------------
        // STEP 8: Navigate directly to Academic Calendar page
        // More reliable than clicking — avoids timing/locator issues
        // ----------------------------------------------------------------
        System.out.println("Step 8: Navigate directly to Academic Calendar page");
        driver.get("https://registrar.northeastern.edu/article/academic-calendar/");

        // Wait for the Trumba spud iframes to load — trumba.spud.7 has the checkboxes
        longWait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("trumba.spud.7.iframe")));
        // Give iframes extra time to fully render their content
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "09_Academic_Calendar_List_View");

        // ----------------------------------------------------------------
        // STEP 9: Scroll down to the Calendars filter panel on the right
        // ----------------------------------------------------------------
        System.out.println("Step 9: Scrolling down to Calendars filter panel");
        calPage.scrollToBottom();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "10_Scrolled_To_Checkboxes");

        // ----------------------------------------------------------------
        // STEP 10: Uncheck one checkbox from the Calendars section
        // ----------------------------------------------------------------
        System.out.println("Step 10: Uncheck one calendar checkbox");
        String uncheckedLabel = calPage.uncheckOneCalendarCheckbox();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "11_After_Uncheck");

        // Assert that we actually unchecked something
        Assert.assertFalse(uncheckedLabel.isEmpty(),
                "Expected to uncheck a calendar checkbox, but none were found/checked");
        System.out.println("  ✓ Unchecked calendar: " + uncheckedLabel);

        // ----------------------------------------------------------------
        // STEP 11: Scroll down and verify "Add to My Calendar" button
        // ----------------------------------------------------------------
        System.out.println("Step 11: Verify 'Add to My Calendar' button is displayed");
        calPage.scrollToBottom();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "12_Scrolled_To_Add_Calendar_Button");

        boolean btnDisplayed = calPage.isAddToMyCalendarButtonDisplayed();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "13_Add_To_Calendar_Button_Visible");

        // Assert button is visible
        Assert.assertTrue(btnDisplayed,
                "'Add to My Calendar' button should be visible after unchecking a calendar");
        System.out.println("  ✓ 'Add to My Calendar' button is visible");

        System.out.println("✓ Scenario 5 PASSED: Academic Calendar updated successfully");
    }
}