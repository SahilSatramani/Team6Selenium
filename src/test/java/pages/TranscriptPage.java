package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TranscriptPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- Student Hub locators ---
    private By resourcesTab             = By.xpath("//a[normalize-space()='Resources']");
    private By academicsCategory        = By.xpath("//span[normalize-space()='Academics, Classes & Registration']");
    private By unofficialTranscriptLink = By.xpath("//a[normalize-space()='Unofficial Transcript']");

    // --- NEU CAS/SSO login page locators ---
    private By casUsernameField = By.id("username");
    private By casPasswordField = By.id("password");
    private By casLoginButton   = By.cssSelector("button[type='submit'][name='_eventId_proceed']");

    // --- Academic Transcript form locators (Angular ui-select dropdowns) ---
    private By transcriptLevelDropdown = By.id("transcriptLevelSelection");
    private By transcriptTypeDropdown  = By.id("transcriptTypeSelection");

    // Print button on transcript page
    private By printButton = By.id("print");

    public TranscriptPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void clickResourcesTab() {
        wait.until(ExpectedConditions.elementToBeClickable(resourcesTab)).click();
        System.out.println("  → Clicked Resources tab");
    }

    public void clickAcademicsCategory() {
        wait.until(ExpectedConditions.elementToBeClickable(academicsCategory)).click();
        System.out.println("  → Clicked Academics, Classes & Registration");
    }

    public void clickUnofficialTranscript() {
        wait.until(ExpectedConditions.elementToBeClickable(unofficialTranscriptLink)).click();
        System.out.println("  → Clicked Unofficial Transcript link");
    }

    public void switchToNewTab(String originalHandle) {
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                System.out.println("  → Switched to new tab");
                return;
            }
        }
    }

    /**
     * Handle NEU CAS login page — uses id="username" and id="password"
     * Username is the part before @northeastern.edu
     */
    public void handleCasLogin(String fullEmail, String password) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement usernameEl = shortWait.until(
                ExpectedConditions.visibilityOfElementLocated(casUsernameField));

            String username = fullEmail.contains("@")
                ? fullEmail.substring(0, fullEmail.indexOf("@"))
                : fullEmail;

            System.out.println("  → CAS login detected, entering: " + username);
            usernameEl.clear();
            usernameEl.sendKeys(username);

            wait.until(ExpectedConditions.visibilityOfElementLocated(casPasswordField))
                .sendKeys(password);
            wait.until(ExpectedConditions.elementToBeClickable(casLoginButton)).click();
            System.out.println("  → Clicked CAS Log In");

        } catch (Exception e) {
            System.out.println("  → CAS login not detected: " + e.getMessage());
        }
    }

    /**
     * Select from Angular ui-select dropdown by clicking container then option text
     */
    private void selectFromAngularDropdown(By containerLocator, String visibleText) {
        wait.until(ExpectedConditions.elementToBeClickable(containerLocator)).click();
        System.out.println("  → Opened dropdown, looking for: " + visibleText);
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'ui-select-choices')]" +
                     "//div[normalize-space(text())='" + visibleText + "']" +
                     " | //li[contains(@class,'ui-select-choices-row')]" +
                     "//div[normalize-space()='" + visibleText + "']"))).click();
        System.out.println("  → Selected: " + visibleText);
    }

    public void selectTranscriptLevel(String level) {
        selectFromAngularDropdown(transcriptLevelDropdown, level);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void selectTranscriptType(String type) {
        selectFromAngularDropdown(transcriptTypeDropdown, type);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    /**
     * Save transcript as PDF using Chrome DevTools Protocol (CDP).
     * This saves the file directly to transcripts/Academic_Transcript.pdf
     * without any print dialog interaction needed.
     */
    public void printPageAsPdf() {
        try {
            Thread.sleep(2000);

            // Scroll to bottom so full page is captured
            ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(1000);

            // Create output directory
            new java.io.File("transcripts").mkdirs();
            String filePath = "transcripts/Academic_Transcript.pdf";

            // Send CDP Page.printToPDF command
            Map<String, Object> params = new HashMap<>();
            params.put("landscape", false);
            params.put("printBackground", true);
            params.put("paperWidth", 8.5);
            params.put("paperHeight", 11.0);
            params.put("marginTop", 0.4);
            params.put("marginBottom", 0.4);
            params.put("marginLeft", 0.4);
            params.put("marginRight", 0.4);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>)
                ((ChromeDriver) driver).executeCdpCommand("Page.printToPDF", params);

            // Decode base64 and write file
            byte[] pdfBytes = Base64.getDecoder().decode((String) response.get("data"));
            Files.write(Paths.get(filePath), pdfBytes);

            java.io.File saved = new java.io.File(filePath);
            System.out.println("  ✓ PDF saved: " + saved.getAbsolutePath()
                + " (" + saved.length() + " bytes)");

        } catch (Exception e) {
            System.err.println("  ✗ PDF save failed: " + e.getMessage());
        }
    }
}