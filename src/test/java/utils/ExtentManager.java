package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * Holds a single {@link ExtentReports} instance and a per-test {@link ExtentTest} in {@link ThreadLocal}
 * for use from test methods and page objects.
 */
public final class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    private ExtentManager() {}

    public static synchronized ExtentReports getExtent() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Project", "selenium-framework");
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void setTest(ExtentTest test) {
        TEST.set(test);
    }

    public static void removeTest() {
        TEST.remove();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
