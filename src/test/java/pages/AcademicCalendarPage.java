package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AcademicCalendarPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- Student Hub locators ---
    private By resourcesTab      = By.xpath("//a[normalize-space()='Resources']");
    private By academicsCategory = By.xpath("//span[normalize-space()='Academics, Classes & Registration']");
    private By academicCalLink   = By.xpath("//a[normalize-space()='Academic Calendar']");

    // --- Registrar page locator ---
    // Clicks the "Academic Calendar" entry (current year), not Future/Past
    private By registrarCalLink  = By.xpath(
        "//h3[normalize-space()='Academic Calendar']/following-sibling::p/a | " +
        "//a[normalize-space()='Academic Calendar'][not(ancestor::*[contains(@class,'future') or contains(@class,'past')])]"
    );

    // --- Inside the Trumba calendar iframes ---
    // Checkboxes are in trumba.spud.7.iframe (title="Calendar List Control")
    private By checkboxIframe     = By.id("trumba.spud.7.iframe");
    // "Add to My Calendar" is in trumba.spud.2.iframe (title="List Calendar View")
    private By addToCalIframe     = By.id("trumba.spud.2.iframe");

    // Checkboxes inside the Calendar List Control iframe
    private By calendarCheckboxes = By.cssSelector("input[type='checkbox']");

    // "Add to My Calendar" button inside the List Calendar View iframe
    private By addToMyCalendarXpath = By.xpath(
        "//*[contains(text(),'Add to My Calendar') or @title='Add to My Calendar' or @value='Add to My Calendar']"
    );

    public AcademicCalendarPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void clickResourcesTab() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(resourcesTab));
        el.click();
        System.out.println("  → Clicked Resources tab");
    }

    public void clickAcademicsCategory() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(academicsCategory));
        el.click();
        System.out.println("  → Clicked Academics, Classes & Registration");
    }

    public void clickAcademicCalendarLink() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(academicCalLink));
        el.click();
        System.out.println("  → Clicked Academic Calendar link");
    }

    // Switch to any new tab that opened
    public void switchToNewTab(String originalHandle) {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                System.out.println("  → Switched to new tab: " + driver.getTitle());
                return;
            }
        }
    }

    // Click "Academic Calendar" (current + future years) on the Registrar page
    // Uses exact class + href from page HTML to avoid clicking "Past Academic Calendars"
    public void clickRegistrarAcademicCalendar() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(registrarCalLink));
        el.click();
        System.out.println("  → Clicked 'Academic Calendar' link");
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight)");
        System.out.println("  → Scrolled to bottom");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    public void scrollToMiddle() {
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight / 2)");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // Switch into the Calendar List Control iframe (contains checkboxes)
    public boolean switchIntoCheckboxIframe() {
        try {
            driver.switchTo().defaultContent();
            WebElement iframe = wait.until(
                ExpectedConditions.presenceOfElementLocated(checkboxIframe));
            driver.switchTo().frame(iframe);
            System.out.println("  → Switched into trumba.spud.7.iframe (checkboxes)");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Could not switch to checkbox iframe: " + e.getMessage());
            return false;
        }
    }

    // Switch into the List Calendar View iframe (contains Add to My Calendar)
    public boolean switchIntoAddToCalIframe() {
        try {
            driver.switchTo().defaultContent();
            WebElement iframe = wait.until(
                ExpectedConditions.presenceOfElementLocated(addToCalIframe));
            driver.switchTo().frame(iframe);
            System.out.println("  → Switched into trumba.spud.2.iframe (Add to My Calendar)");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ Could not switch to add-to-cal iframe: " + e.getMessage());
            return false;
        }
    }

    // Switch back out of iframe to main page
    public void switchToMainContent() {
        driver.switchTo().defaultContent();
        System.out.println("  → Switched back to main content");
    }

    // Uncheck the first checked checkbox inside trumba.spud.7.iframe
    public String uncheckOneCalendarCheckbox() {
        scrollToBottom();

        // Must switch into the correct Trumba iframe for checkboxes
        if (!switchIntoCheckboxIframe()) {
            System.out.println("  ✗ Could not access checkbox iframe");
            return "";
        }

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        List<WebElement> checkboxes = driver.findElements(calendarCheckboxes);
        System.out.println("  → Found " + checkboxes.size() + " checkboxes in iframe");

        for (WebElement cb : checkboxes) {
            try {
                if (cb.isSelected() && cb.isDisplayed()) {
                    // Get label text from parent element
                    String labelText = "";
                    try {
                        WebElement parent = cb.findElement(By.xpath(".."));
                        labelText = parent.getText().trim();
                        if (labelText.isEmpty()) {
                            labelText = cb.getAttribute("value") != null
                                ? cb.getAttribute("value") : "calendar-item";
                        }
                    } catch (Exception ignored) {
                        labelText = "calendar-item";
                    }
                    cb.click();
                    System.out.println("  → Unchecked: " + labelText);
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    // Switch back to main document after done
                    driver.switchTo().defaultContent();
                    return labelText;
                }
            } catch (Exception e) {
                System.out.println("  → Skipping checkbox: " + e.getMessage());
            }
        }

        driver.switchTo().defaultContent();
        System.out.println("  ✗ No checked checkbox found");
        return "";
    }

    // Check if "Add to My Calendar" button is visible inside trumba.spud.2.iframe
    public boolean isAddToMyCalendarButtonDisplayed() {
        try {
            scrollToBottom();

            // Switch into the List Calendar View iframe
            if (!switchIntoAddToCalIframe()) {
                return false;
            }

            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            List<WebElement> btns = driver.findElements(addToMyCalendarXpath);
            if (!btns.isEmpty() && btns.get(0).isDisplayed()) {
                System.out.println("  → 'Add to My Calendar' button found in calendar iframe");
                driver.switchTo().defaultContent();
                return true;
            }

            driver.switchTo().defaultContent();
            System.out.println("  ✗ 'Add to My Calendar' button not found in iframe");
            return false;
        } catch (Exception e) {
            driver.switchTo().defaultContent();
            System.out.println("  ✗ Error finding button: " + e.getMessage());
            return false;
        }
    }
}