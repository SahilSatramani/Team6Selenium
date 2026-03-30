package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class CalendarPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // need to update
    private By calendarLink = By.id("global_nav_calendar_link");
    private By plusButton = By.id("create_new_event_link");
    private By titleField = By.cssSelector("input[data-testid='edit-calendar-event-form-title']");
    private By dateField = By.cssSelector("input[data-testid='edit-calendar-event-form-date']");
    private By startTimeField = By.cssSelector("input[data-testid='event-form-start-time']");
    private By endTimeField = By.cssSelector("input[data-testid='event-form-end-time']");
    private By locationField = By.cssSelector("input[data-testid='edit-calendar-event-form-location']");
    private By submitButton = By.id("edit-calendar-event-submit-button");

    private By eventTitles = By.cssSelector("a.fc-event[class*='group_user']");

    // for getting the driver
    public CalendarPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // first navidate to the calendar tab
    public void navigateToCalendar() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(calendarLink));
        element.click();
    }

    // click plus button
    public void clickPlusButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(plusButton));
        element.click();
    }

    // now enter the title
    public void enterTitle(String title) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
        element.clear();
        element.sendKeys(title);
    }

    // enter the event dates
    public void enterDate(String date) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(dateField));
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(date);
    }

    // enter start time
    public void enterStartTime(String startTime) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(startTimeField));
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(startTime);
    }

    // enter end time
    public void enterEndTime(String endTime) {
        System.out.println("  → Entering end time: " + endTime);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(endTimeField));
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(endTime);
    }

    // enter end time
    public void enterLocation(String location) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locationField));
        element.clear();
        element.sendKeys(location);
    }

    // now add the task
    public void clickSubmit() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        element.click();
    }

    // now call all the methods
    public void createEvent(String title, String date, String startTime, String endTime, String location) {
        enterTitle(title);
        enterDate(date);
        enterStartTime(startTime);
        enterEndTime(endTime);
        enterLocation(location);
    }

    // getting the count of events
    public int getEventCount() {
        try{
            wait.until(ExpectedConditions.presenceOfElementLocated(eventTitles));
            List<WebElement> events = driver.findElements(eventTitles);
            return events.size();
        } catch (Exception e) {
            return 0;
        }

    }



}
