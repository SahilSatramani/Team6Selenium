package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DatasetListingPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public DatasetListingPage(WebDriver driver, long timeoutSeconds) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    public String openAnyDataset() {
        By datasetCandidates = By.xpath(
                "//a[contains(@href,'dataset') or contains(@href,'record') or contains(normalize-space(),'Dataset')]"
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(datasetCandidates));
        List<WebElement> links = driver.findElements(datasetCandidates);
        if (links.isEmpty()) {
            throw new IllegalStateException("No dataset links found on the page.");
        }
        String chosen = links.get(0).getText();
        wait.until(ExpectedConditions.elementToBeClickable(links.get(0))).click();
        return chosen == null ? "" : chosen.trim();
    }

    public void clickZipFile() {
        By zipFile = By.cssSelector("a.btn.btn-mini.btn-clear[title='Zip File'][href*='datastream_id=content']");
        wait.until(ExpectedConditions.elementToBeClickable(zipFile)).click();
    }

    public boolean isLoginPromptVisible() {
        By loginPrompt = By.cssSelector("a.btn[href='/users/auth/shibboleth']");
        return !driver.findElements(loginPrompt).isEmpty();
    }
}
