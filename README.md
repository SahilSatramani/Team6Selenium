# Team6Selenium

Maven + TestNG + Selenium. After `mvn test`, open the **Extent Spark** HTML report at `target/extent-report.html` in a browser.

Extent is wired through `@Listeners(ExtentTestNGListener.class)` on [`BaseTest`](src/test/java/base/BaseTest.java), so the report is produced even when running a single class with `mvn test -Dtest=SomeTest`.
