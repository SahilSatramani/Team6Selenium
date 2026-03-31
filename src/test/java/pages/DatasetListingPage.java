package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DatasetListingPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public DatasetListingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    public String openAnyDataset() {
        By candidates = By.xpath("//a[contains(@href,'/concern/datasets/') or contains(@href,'/datasets/')]");
        wait.until(ExpectedConditions.presenceOfElementLocated(candidates));
        List<WebElement> links = driver.findElements(candidates);
        if (links.isEmpty()) {
            throw new IllegalStateException("No dataset links found.");
        }

        WebElement first = links.get(0);
        String text = first.getText() == null ? "" : first.getText().trim();
        js.executeScript("arguments[0].click();", first);
        waitForPageLoad();
        return text;
    }

    public void clickZipFile() {
        By zip = By.cssSelector("a.btn.btn-mini.btn-clear[title='Zip File'][href*='datastream_id=content']");
        WebElement zipLink = wait.until(ExpectedConditions.elementToBeClickable(zip));
        js.executeScript("arguments[0].click();", zipLink);
        waitForPageLoad();
    }

    public boolean isLoginPromptVisible() {
        By login = By.cssSelector("a.btn[href='/users/auth/shibboleth']");
        return !driver.findElements(login).isEmpty();
    }

    private void waitForPageLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }
}
