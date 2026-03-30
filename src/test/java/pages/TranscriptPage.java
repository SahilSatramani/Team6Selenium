package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TranscriptPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- Student Hub locators ---
    private By resourcesTab             = By.xpath("//a[normalize-space()='Resources']");
    private By academicsCategory        = By.xpath("//span[normalize-space()='Academics, Classes & Registration']");
    private By unofficialTranscriptLink = By.xpath("//a[normalize-space()='Unofficial Transcript']");

    // --- NEU CAS/SSO login page locators (different from Microsoft login) ---
    // This page appears at /idp/profile/cas/login when transcript opens in new tab
    private By casUsernameField = By.id("username");
    private By casPasswordField = By.id("password");
    private By casLoginButton   = By.cssSelector("button[type='submit'][name='_eventId_proceed']");

    // --- Academic Transcript form locators (Angular ui-select dropdowns) ---
    // These are NOT standard <select> — they are custom Angular components
    private By transcriptLevelDropdown = By.id("transcriptLevelSelection");
    private By transcriptTypeDropdown  = By.id("transcriptTypeSelection");

    public TranscriptPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Click the Resources tab on Student Hub
    public void clickResourcesTab() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(resourcesTab));
        el.click();
        System.out.println("  → Clicked Resources tab");
    }

    // Click Academics, Classes & Registration category
    public void clickAcademicsCategory() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(academicsCategory));
        el.click();
        System.out.println("  → Clicked Academics, Classes & Registration");
    }

    // Click Unofficial Transcript link
    public void clickUnofficialTranscript() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(unofficialTranscriptLink));
        el.click();
        System.out.println("  → Clicked Unofficial Transcript link");
    }

    // Switch to a new tab/window that opened
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
     * Handle the NEU CAS login page that appears when transcript opens.
     * This is a DIFFERENT login page from Microsoft/Duo — it uses:
     *   id="username" and id="password"
     * The username must be only the part before @northeastern.edu
     * e.g. "satramani.s@northeastern.edu" → "satramani.s"
     */
    public void handleCasLogin(String fullEmail, String password) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement usernameEl = shortWait.until(
                ExpectedConditions.visibilityOfElementLocated(casUsernameField));

            // Extract username: everything before @northeastern.edu
            String username = fullEmail.contains("@")
                ? fullEmail.substring(0, fullEmail.indexOf("@"))
                : fullEmail;

            System.out.println("  → CAS login page detected");
            System.out.println("  → Entering CAS username: " + username);

            usernameEl.clear();
            usernameEl.sendKeys(username);

            WebElement passwordEl = wait.until(
                ExpectedConditions.visibilityOfElementLocated(casPasswordField));
            passwordEl.clear();
            passwordEl.sendKeys(password);

            WebElement loginBtn = wait.until(
                ExpectedConditions.elementToBeClickable(casLoginButton));
            loginBtn.click();
            System.out.println("  → Clicked CAS Log In button");

        } catch (Exception e) {
            System.out.println("  → CAS login page not detected, skipping: " + e.getMessage());
        }
    }

    /**
     * Select a value from an Angular ui-select dropdown.
     * These are NOT standard <select> elements — clicking the container
     * opens the dropdown, then we click the option by visible text.
     */
    private void selectFromAngularDropdown(By containerLocator, String visibleText) {
        WebElement container = wait.until(
            ExpectedConditions.elementToBeClickable(containerLocator));
        container.click();
        System.out.println("  → Opened dropdown, looking for: " + visibleText);

        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'ui-select-choices')]" +
                     "//div[normalize-space(text())='" + visibleText + "']" +
                     " | //li[contains(@class,'ui-select-choices-row')]" +
                     "//div[normalize-space()='" + visibleText + "']")));
        option.click();
        System.out.println("  → Selected: " + visibleText);
    }

    // Select Transcript Level (e.g. "Graduate")
    public void selectTranscriptLevel(String level) {
        selectFromAngularDropdown(transcriptLevelDropdown, level);
        System.out.println("  → Transcript Level set to: " + level);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // Select Transcript Type (e.g. "Audit Transcript")
    public void selectTranscriptType(String type) {
        selectFromAngularDropdown(transcriptTypeDropdown, type);
        System.out.println("  → Transcript Type set to: " + type);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    /**
     * Save transcript as PDF using Chrome DevTools Protocol (CDP).
     * executeCdpCommand is available on ChromeDriver directly.
     * Saves PDF to transcripts/Academic_Transcript.pdf in project root.
     */
    public void printPageAsPdf() {
        try {
            Thread.sleep(2000);

            // Create output directory
            String saveDir = "transcripts";
            new java.io.File(saveDir).mkdirs();
            String filePath = saveDir + "/Academic_Transcript.pdf";

            // Cast to ChromeDriver to access executeCdpCommand
            org.openqa.selenium.chrome.ChromeDriver chromeDriver =
                (org.openqa.selenium.chrome.ChromeDriver) driver;

            // Build CDP params for Page.printToPDF
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("landscape", false);
            params.put("printBackground", true);
            params.put("paperWidth", 8.5);
            params.put("paperHeight", 11.0);
            params.put("marginTop", 0.4);
            params.put("marginBottom", 0.4);
            params.put("marginLeft", 0.4);
            params.put("marginRight", 0.4);

            System.out.println("  → Sending CDP Page.printToPDF command...");

            // Execute CDP command — returns map with "data" key (base64 PDF)
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> response =
                (java.util.Map<String, Object>) chromeDriver
                    .executeCdpCommand("Page.printToPDF", params);

            if (response == null || !response.containsKey("data")) {
                System.err.println("  ✗ CDP response was null or missing 'data' key");
                return;
            }

            // Decode base64 PDF and write to file
            String base64Data = (String) response.get("data");
            byte[] pdfBytes = java.util.Base64.getDecoder().decode(base64Data);
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), pdfBytes);

            // Verify file was actually created
            java.io.File savedFile = new java.io.File(filePath);
            if (savedFile.exists() && savedFile.length() > 0) {
                System.out.println("  ✓ PDF saved: " + savedFile.getAbsolutePath()
                    + " (" + savedFile.length() + " bytes)");
            } else {
                System.err.println("  ✗ PDF file was not created or is empty");
            }

        } catch (ClassCastException e) {
            System.err.println("  ✗ Driver is not ChromeDriver: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("  ✗ CDP PDF save failed: " + e.getClass().getSimpleName()
                + " - " + e.getMessage());
        }
    }
}