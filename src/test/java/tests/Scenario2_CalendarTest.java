package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CalendarPage;
import pages.LoginPage;
import utils.ExcelReader;
import utils.PasswordUtil;
import utils.ScreenShotUtil;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Scenario2_CalendarTest extends BaseTest {

    @Test
    public void testAddTwoCalendarEvents() {
        String scenarioName = "Scenario2";
        WebDriverWait longwait = new WebDriverWait(driver, Duration.ofSeconds(90));
        driver.get("https://www.google.com");

        System.out.println("Step 1: Go to canvas");
        driver.get("https://northeastern.instructure.com/");

        boolean alreadyLoggedIn = driver.getCurrentUrl().contains("northeastern.instructure.com")
                && !driver.getCurrentUrl().contains("login");
        if (alreadyLoggedIn) {
            System.out.println("Already logged in via SSO");
        } else {
            System.out.println("Not logged in. Will perform login operation");
        }

        ScreenShotUtil.takeScreenshot(driver, scenarioName, "01_Canvas_or_Login_Homepage");
        // get the username and decode the password
        if (!alreadyLoggedIn) {
            // login to canvas
            System.out.println("Step 2: login to canvas");

            // read credentials from excel
            String credPath = "src/test/resources/testdata/credentials.xlsx";
            Assert.assertTrue(new File(credPath).exists(), "Credentials file not found");

            List<Map<String, String>> credentials = ExcelReader.readExcel(credPath);
            String username = credentials.get(0).get("Username");
            String password = PasswordUtil.decode(credentials.get(0).get("Password"));
            System.out.println("credentials loaded from excel");

            LoginPage loginPage = new LoginPage(driver);
            loginPage.login(username, password);
            ScreenShotUtil.takeScreenshot(driver, scenarioName, "02_After_entering_username_and_password");
            loginPage.clickSignIn();

            longwait.until(ExpectedConditions.urlContains("duosecurity.com"));

            ScreenShotUtil.takeScreenshot(driver, scenarioName, "03_After_Signing_in_and_now_on_Duo_page");
            loginPage.handleDuoDevicePrompt();

            ScreenShotUtil.takeScreenshot(driver, scenarioName, "04_After_Duo_page");
            loginPage.handleStaySignedIn();

            // wait for canvas to load
            longwait.until(ExpectedConditions.urlContains("northeastern.instructure.com"));
            longwait.until(ExpectedConditions.presenceOfElementLocated(By.id("global_nav_profile_link")));
            ScreenShotUtil.takeScreenshot(driver, scenarioName, "05_Canvas_Dashboard");
        } else {
            longwait.until(ExpectedConditions.presenceOfElementLocated(By.id("global_nav_profile_link")));
            ScreenShotUtil.takeScreenshot(driver, scenarioName, "02_Already_Logged_In");
        }

        // Assert: Verify successful login
        Assert.assertTrue(driver.getCurrentUrl().contains("northeastern.instructure.com"),"Should be on Canvas after login");
        System.out.println("Login successful - on Canvas dashboard");

        // step 3: navigate to calendar
        System.out.println("Step 3: Go to Calendar tab");
        CalendarPage calendarPage = new CalendarPage(driver);
        calendarPage.navigateToCalendar();
        ScreenShotUtil.takeScreenshot(driver, scenarioName, "06_Calendar_Page");

        // step 4: Read event data from Excel
        System.out.println("Step 4: Read Event Data");
        String excelPath = "src/test/resources/testdata/scenario_2_data.xlsx";
        Assert.assertTrue(new File(excelPath).exists(), "Event data file not found");

        List<Map<String, String>> testData = ExcelReader.readExcel(excelPath);
        System.out.println("Found " + testData.size() + " events in Excel");

        // Step 5: Create events iteratively
        System.out.println("Step 5: Create Events");
        for (int i = 0; i < testData.size(); i++) {
            Map<String, String> eventData = testData.get(i);
            System.out.println("Creating Event " + (i + 1) + ": " + eventData.get("Title"));

            // Screenshot before creating event
            ScreenShotUtil.takeScreenshot(driver, scenarioName, "07_Before_Event_Creation" + (i + 1));
            calendarPage.clickPlusButton();
            ScreenShotUtil.takeScreenshot(driver, scenarioName, "08_Before_Entering_Details" + (i + 1));

            // Create event using data from Excel
            calendarPage.createEvent(
                    eventData.get("Title"),
                    eventData.get("Date"),
                    eventData.get("StartTime"),
                    eventData.get("EndTime"),
                    eventData.get("Location")
            );

            ScreenShotUtil.takeScreenshot(driver, scenarioName, "09_After_Entering_Details" + (i + 1));
            calendarPage.clickSubmit();
            // Screenshot after creating event
            ScreenShotUtil.takeScreenshot(driver, scenarioName, "10_After_Event_Creation" + (i + 1));
            System.out.println("Event " + (i + 1) + " created successfully");

        }

        // Step 6: Verify events are created
        System.out.println("Step 6: Verify Events");
        int eventCount = calendarPage.getEventCount();
        System.out.println("Total events on calendar: " + eventCount);

        ScreenShotUtil.takeScreenshot(driver, scenarioName, "08_Final_Verification");
        // Assert that events were created
        Assert.assertEquals(eventCount, testData.size(),
                "Expected " + testData.size() + " events, found " + eventCount);

        System.out.println("Scenario 2 has passed");
        }

}
