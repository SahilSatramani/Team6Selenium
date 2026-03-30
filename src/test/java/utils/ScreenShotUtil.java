package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class ScreenShotUtil {

    public static void takeScreenshot(WebDriver driver, String scenarioName, String stepName) {
        if (driver == null){
            System.err.println("Driver is null, cannot take screenshot");
            return;
        }

        try{
            // wait for the page to be ready before taking screenshot
            Thread.sleep(4000);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = stepName + "_" + timeStamp + ".png";

            TakesScreenshot screenshot = ((TakesScreenshot) driver);
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File("screenshots/" + scenarioName + "/" + fileName);

            //creating dir if does not exists
            File parentDir = destFile.getParentFile();
            if (!parentDir.exists()){
                parentDir.mkdirs();
            }

            FileUtils.copyFile(srcFile, destFile);
        } catch (Exception e){
            System.err.println("Failed to capture screenshot" + e.getMessage());
        }
    }
}
