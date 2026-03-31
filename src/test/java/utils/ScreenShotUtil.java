package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots before and after each automation step; files are stored under
 * {@code screenshots/&lt;scenarioFolder&gt;/} at the project working directory.
 */
public final class ScreenShotUtil {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenShotUtil() {}

    /**
     * @param phase use "before" or "after" (included in filename for the assignment requirement).
     */
    public static void capture(WebDriver driver, Path scenarioDir, String stepId, String phase) {
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }
        try {
            Files.createDirectories(scenarioDir);
            String safeStep = stepId.replaceAll("[^a-zA-Z0-9_-]", "_");
            String name = phase + "_" + safeStep + "_" + LocalDateTime.now().format(TS) + ".png";
            Path file = scenarioDir.resolve(name);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(file, png);
        } catch (IOException e) {
            throw new RuntimeException("Screenshot failed for step " + stepId + " (" + phase + ")", e);
        }
    }
}
