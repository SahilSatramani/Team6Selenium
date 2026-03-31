package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds a simple HTML table after execution: scenario name, actual, expected, pass/fail.
 */
public final class HtmlReportWriter {

    public static final class Row {
        public final String scenarioName;
        public final String actual;
        public final String expected;
        public final String passFail;

        public Row(String scenarioName, String actual, String expected, String passFail) {
            this.scenarioName = scenarioName;
            this.actual = actual;
            this.expected = expected;
            this.passFail = passFail;
        }
    }

    private static final List<Row> ROWS = Collections.synchronizedList(new ArrayList<>());

    private HtmlReportWriter() {}

    public static void addRow(String scenarioName, String actual, String expected, String passFail) {
        ROWS.add(new Row(escapeHtml(scenarioName), escapeHtml(actual), escapeHtml(expected), escapeHtml(passFail)));
    }

    public static void clear() {
        ROWS.clear();
    }

    public static void writeReport(Path outputFile) throws IOException {
        Path parent = outputFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Scenario Report</title>")
                .append("<style>table{border-collapse:collapse;width:100%;font-family:sans-serif;}")
                .append("th,td{border:1px solid #ccc;padding:8px;text-align:left;}")
                .append("th{background:#f0f0f0;}</style></head><body>")
                .append("<h1>Test scenario report</h1><table><tr>")
                .append("<th>Test scenario name</th><th>Actual</th><th>Expected</th><th>Pass/Fail</th></tr>");
        synchronized (ROWS) {
            for (Row r : ROWS) {
                sb.append("<tr><td>").append(r.scenarioName).append("</td><td>")
                        .append(r.actual).append("</td><td>")
                        .append(r.expected).append("</td><td>")
                        .append(r.passFail).append("</td></tr>");
            }
        }
        sb.append("</table></body></html>");
        Files.write(outputFile, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
