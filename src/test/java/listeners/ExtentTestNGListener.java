package listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ExtentManager;

public class ExtentTestNGListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getExtent();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String title = result.getMethod().getDescription();
        if (title == null || title.isEmpty()) {
            title = result.getMethod().getMethodName();
        }
        ExtentTest test = ExtentManager.getExtent().createTest(title);
        ExtentManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest t = ExtentManager.getTest();
        if (t != null) {
            t.log(Status.PASS, "Test passed");
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = ExtentManager.getTest();
        if (t != null) {
            Throwable th = result.getThrowable();
            if (th != null) {
                t.fail(th);
            } else {
                t.log(Status.FAIL, "Test failed");
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest t = ExtentManager.getTest();
        if (t != null) {
            t.log(Status.SKIP, "Test skipped");
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }
}
