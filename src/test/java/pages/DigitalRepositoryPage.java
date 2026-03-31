package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DigitalRepositoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public DigitalRepositoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    public void clickDatasets() {
        By preferred = By.cssSelector("a.btn.btn-clear.btn-block[href='/datasets']");
        By fallback = By.xpath("//a[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'datasets')]");

        WebElement datasets;
        if (!driver.findElements(preferred).isEmpty()) {
            datasets = wait.until(ExpectedConditions.elementToBeClickable(preferred));
        } else {
            datasets = wait.until(ExpectedConditions.elementToBeClickable(fallback));
        }

        js.executeScript("arguments[0].click();", datasets);
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }
}
