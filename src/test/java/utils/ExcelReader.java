package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads test data from the Scenario 4 spreadsheet (no hard-coded test values in Java).
 */
public final class ExcelReader {

    private ExcelReader() {}

    /**
     * Reads the first data row (row index 1) using row 0 as headers from a classpath resource.
     *
     * @param resourcePath path beginning with / (e.g. {@code /testdata/scenario4_data.xlsx})
     */
    public static Map<String, String> readFirstDataRowByHeader(String resourcePath) throws IOException {
        try (InputStream in = ExcelReader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (Workbook workbook = WorkbookFactory.create(in)) {
                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0);
                Row dataRow = sheet.getRow(1);
                if (headerRow == null || dataRow == null) {
                    throw new IOException("Spreadsheet must have header row and at least one data row.");
                }
                Map<String, String> map = new HashMap<>();
                for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                    String key = cellString(headerRow.getCell(c));
                    if (key == null || key.isEmpty()) {
                        continue;
                    }
                    map.put(key.trim(), cellString(dataRow.getCell(c)));
                }
                return map;
            }
        }
    }

    private static String cellString(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType type = cell.getCellType();
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultType();
        }
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double n = cell.getNumericCellValue();
                if (n == Math.rint(n)) {
                    return String.valueOf((long) n);
                }
                return String.valueOf(n);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
