package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class OneSearchPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public OneSearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    public void open(String url) {
        driver.get(url);
        waitForPageLoad();
    }

    public void clickDigitalRepositoryServiceAndSwitchTab() {
        String originalWindow = driver.getWindowHandle();

        By byDataQa = By.cssSelector("div[data-qa='mainmenu.digitalrepository'] a[href='https://repository.library.northeastern.edu/']");
        By byMenuItem = By.cssSelector("div[data-main-menu-item='digitalrepository'] a[aria-label*='digital repository service']");
        By fallback = By.xpath("//a[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'digital repository service')]");

        WebElement link;
        if (!driver.findElements(byDataQa).isEmpty()) {
            link = wait.until(ExpectedConditions.elementToBeClickable(byDataQa));
        } else if (!driver.findElements(byMenuItem).isEmpty()) {
            link = wait.until(ExpectedConditions.elementToBeClickable(byMenuItem));
        } else {
            link = wait.until(ExpectedConditions.elementToBeClickable(fallback));
        }

        js.executeScript("arguments[0].click();", link);

        wait.until(d -> d.getWindowHandles().size() > 1);
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        waitForPageLoad();
    }

    private void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }
}
