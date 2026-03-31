package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LibraryPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public LibraryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    // Generic scroll method
    public void scrollToElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    public void navigateToLibrary(String url) {
        driver.get(url);
        waitForPageLoad();
    }

    public void clickReserveStudyRoom() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Reserve A Study Room")));
        js.executeScript("arguments[0].click();", element);
        waitForPageLoad();
    }

    public void clickBostonImage() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//img[contains(@alt, 'Boston')]")));
        js.executeScript("arguments[0].click();", element);
        waitForPageLoad();
    }

    public void navigateToRoomsPage() {
        driver.get("https://library.northeastern.edu/ideas/rooms-spaces/");
        waitForPageLoad();
    }

    public void clickBookARoom() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Book a Room")));
        js.executeScript("arguments[0].click();", element);
        waitForPageLoad();

        if (!driver.getCurrentUrl().contains("libcal")) {
            driver.get("https://northeastern.libcal.com/reserve/spaces/studyspace");
            waitForPageLoad();
        }
    }

    public void selectSeatStyle(String seatStyle) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("gid")));
        Select select = new Select(dropdown);
        select.selectByVisibleText(seatStyle);
        waitForPageLoad();
    }

    public void selectCapacity(String capacity) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("capacity")));
        Select select = new Select(dropdown);
        select.selectByVisibleText(capacity);
        waitForPageLoad();
    }

    public void clickFirstAvailableSlot() {
        WebElement slot = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.s-lc-eq-avail")));
        js.executeScript("arguments[0].click();", slot);
        waitForPageLoad();
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'});");
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    public void dismissCookieBanner() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement cookieBtn = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Accept')] | //button[contains(text(),'OK')] | //a[text()='OK']")));
            js.executeScript("arguments[0].click();", cookieBtn);
        } catch (Exception e) {}
    }

    public boolean hasAvailableSlots() {
        try {
            driver.findElement(By.cssSelector("a.s-lc-eq-avail"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void navigateToNextDay() {
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.fc-next-button")));
        js.executeScript("arguments[0].click();", nextButton);
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }
}