package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LibraryPage;
import utils.ExcelReader;
import utils.ScreenShotUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Scenario3_LibraryTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario3";

    @Test
    public void testReserveSnellLibraryRoom() {
        String excelPath = "src/test/resources/testdata/scenario_3_data.xlsx";
        Assert.assertTrue(new File(excelPath).exists());

        List<Map<String, String>> dataList = ExcelReader.readExcel(excelPath);
        Map<String, String> data = dataList.get(0);

        String url = data.get("url");
        String location = data.get("location");
        String seatStyle = data.get("seatStyle");
        String capacity = data.get("capacity");

        LibraryPage libraryPage = new LibraryPage(driver);

        // Step 1: Open Library
        System.out.println("\n=== Step 1: Open Library ===");
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "01_Before_Open");

        libraryPage.navigateToLibrary(url);
        libraryPage.dismissCookieBanner();

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "02_After_Open");

        // Step 2: Reserve Study Room
        System.out.println("\n=== Step 2: Reserve Study Room ===");
        libraryPage.scrollToElement(By.linkText("Reserve A Study Room"));
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "03_Before_Reserve");

        libraryPage.clickReserveStudyRoom();

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "04_After_Reserve");

        // Step 3: Select Boston
        System.out.println("\n=== Step 3: Select Boston ===");
        libraryPage.scrollToElement(By.xpath("//img[contains(@alt, 'Boston')]"));
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "05_Before_Boston");

        libraryPage.clickBostonImage();

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "06_After_Boston");

        // Step 4: Book Room
        System.out.println("\n=== Step 4: Book Room ===");
        libraryPage.navigateToRoomsPage();
        libraryPage.scrollToElement(By.linkText("Book a Room"));
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "07_Before_Book");

        libraryPage.clickBookARoom();
        libraryPage.dismissCookieBanner();

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "08_After_Book");

        // Step 5: Seat Style
        System.out.println("\n=== Step 5: Seat Style ===");
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "09_Before_SeatStyle");

        libraryPage.selectSeatStyle(seatStyle);

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "10_After_SeatStyle");

        // Step 6: Capacity
        System.out.println("\n=== Step 6: Capacity ===");
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "11_Before_Capacity");

        libraryPage.selectCapacity(capacity);

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "12_After_Capacity");

        // Step 7: Find and Click Available Slot
        System.out.println("\n=== Step 7: Click Available Slot ===");

        for (int i = 0; i < 7; i++) {
            if (libraryPage.hasAvailableSlots()) break;
            libraryPage.navigateToNextDay();
        }

        libraryPage.scrollToElement(By.cssSelector("a.s-lc-eq-avail"));
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "13_Before_Click_Slot");

        libraryPage.clickFirstAvailableSlot();

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "14_After_Click_Slot");

        // Step 8: Scroll to Bottom
        System.out.println("\n=== Step 8: Scroll to Bottom ===");
        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "15_Before_Final_Scroll");

        libraryPage.scrollToBottom();

        ScreenShotUtil.takeScreenshot(driver, SCENARIO_NAME, "16_After_Final_Scroll");

        System.out.println("\n*** Scenario 3 PASSED ***");
    }
}