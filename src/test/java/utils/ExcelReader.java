package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {

    public static List<Map<String, String>> readExcel(String fileName) {
        List<Map<String, String>> data = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream file = new FileInputStream(fileName);
             Workbook workBook = new XSSFWorkbook(file)) {
            // getting the sheet from the workbook
            Sheet sheet = workBook.getSheetAt(0);
            // safety check
            if (sheet.getPhysicalNumberOfRows() == 0) {
                System.out.println("Excel sheet is empty");
                return data;
            }
            // getting header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                System.out.println("Header row is missing.");
                return data;
            }

            // now reading all rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> rowData = new LinkedHashMap<>();

                for (int j = 0; j < headerRow.getLastCellNum(); j++){
                    Cell headerCell = headerRow.getCell(j);
                    Cell dataCell = row.getCell(j);
                    String header = headerCell != null ? formatter.formatCellValue(headerCell).trim() : "Column" + j;
                    String value = dataCell != null ? formatter.formatCellValue(dataCell).trim() : "";

                    rowData.put(header, value);
                }
                data.add(rowData);
            }
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
}
