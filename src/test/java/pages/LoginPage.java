package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // neeed to update this
    private By usernameField = By.id("i0116");
    private By passwordField = By.id("i0118");
    private By nextButton = By.id("idSIButton9");
    private By signInButton = By.id("idSIButton9");

    // Constructor for using the driver
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    // for next button
    public void clickNextButton() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        element.click();
    }

    // for username field
    public void enterUsername(String username) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        element.clear();
        element.sendKeys(username);
    }

    // for password field
    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        element.clear();
        element.sendKeys(password);

    }

    //for sign in button
    public void clickSignIn() {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(signInButton));
        element.click();
    }

    // for Duo authentication - this is my device
    public void handleDuoDevicePrompt(){
        try{
            System.out.println("Checking for 'this is my device?' prompt");
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement yesButton = shortWait.until(ExpectedConditions.elementToBeClickable(By.id("trust-browser-button")));
            yesButton.click();
            System.out.println("  → Clicked 'Yes, this is my device'");
        } catch (Exception e) {
            System.out.println("Device prompt not found");
        }
    }

    public void handleStaySignedIn() {
        try {
            System.out.println("Checking for 'Stay signed in?' prompt");
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            // dont show again - checkbox
            WebElement checkbox = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.id("KmsiCheckboxField")));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            // yes button
            WebElement yesButton = shortWait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
            yesButton.click();
            System.out.println("Clicked 'Yes' on Stay signed in");

        } catch (Exception e) {
            System.out.println("'Stay signed in?' prompt not found");
        }
    }


    // main login method which calls all the methods
    public void login(String username, String password) {
        enterUsername(username);
        clickNextButton();
        enterPassword(password);
    }


}
