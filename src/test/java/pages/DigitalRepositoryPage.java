package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DigitalRepositoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DigitalRepositoryPage(WebDriver driver, long timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public void clickDatasets(String datasetsText) {
        By hrefDatasets = By.cssSelector("a.btn.btn-clear.btn-block[href='/datasets']");
        By featuredDatasets = By.xpath(
                "//*[contains(normalize-space(),\"Featured Content\")]" +
                        "/following::*[self::a or self::button][contains(normalize-space(),\"" + datasetsText + "\")][1]"
        );
        By fallback = By.xpath("//a[contains(normalize-space(),\"" + datasetsText + "\")]");
        if (!driver.findElements(hrefDatasets).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(hrefDatasets)).click();
        } else if (!driver.findElements(featuredDatasets).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(featuredDatasets)).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(fallback)).click();
        }
    }
}
