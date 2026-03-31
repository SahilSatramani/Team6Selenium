package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class OneSearchPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public OneSearchPage(WebDriver driver, long timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
    }

    public void clickDigitalRepositoryService(String linkText) {
        String originalWindow = driver.getWindowHandle();
        By byDataQa = By.cssSelector("div[data-qa='mainmenu.digitalrepository'] a[href='https://repository.library.northeastern.edu/']");
        By byMenuItem = By.cssSelector("div[data-main-menu-item='digitalrepository'] a[aria-label*='digital repository service']");
        By fallbackContains = By.xpath("//a[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),\"" + linkText.toLowerCase() + "\")]");

        if (!driver.findElements(byDataQa).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(byDataQa)).click();
        } else if (!driver.findElements(byMenuItem).isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(byMenuItem)).click();
        } else {
            wait.until(ExpectedConditions.elementToBeClickable(fallbackContains)).click();
        }

        // DRS opens in a new tab. Switch so the next step runs in that context.
        wait.until(d -> d.getWindowHandles().size() > 1);
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }
}
