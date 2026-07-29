package com.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationLoginTest {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test
    void shouldRegisterUserAndLoginViaApi() throws Exception {
        DataRow data = fetchRandomDataRow();

        page.navigate("https://rahulshettyacademy.com/client");
        page.getByText("Register here").click();

        String firstName = data.firstName;
        String lastName = data.lastName;
        String uniqueEmail = buildUniqueEmail(firstName, lastName);
        String mobile = data.mobile;
        String occupation = data.occupation;
        String gender = data.gender;
        String password = "TestPass123!";
        List<String> allowedOccupations = Arrays.asList("Doctor", "Student", "Engineer", "Scientist");
        String selectedOccupation = allowedOccupations.contains(occupation) ? occupation : "Engineer";

        page.locator("#firstName").fill(firstName);
        page.locator("#lastName").fill(lastName);
        page.locator("#userEmail").fill(uniqueEmail);
        page.locator("#userMobile").fill(mobile);
        page.locator("select").selectOption(selectedOccupation);
        page.locator("input[type='radio']").nth(gender.equalsIgnoreCase("Female") ? 1 : 0).check();
        page.locator("#userPassword").fill(password);
        page.locator("#confirmPassword").fill(password);
        page.locator("input[type='checkbox']").check();
        page.locator("input[type='submit']").click();

        page.waitForTimeout(4000);
        String pageText = page.locator("body").textContent();
        assertTrue(pageText.contains("Login") || pageText.contains("Registered Successfully") || pageText.contains("Please Login"),
                "Registration did not complete as expected. Page body: " + pageText);

        String loginPayload = "{\"userEmail\":\"" + uniqueEmail + "\",\"userPassword\":\"" + password + "\"}";
        String loginResponse = postJsonWithRetry("https://rahulshettyacademy.com/api/ecom/auth/login", loginPayload);
        assertTrue(loginResponse.contains("Login Successfully") || loginResponse.contains("token"),
                "Login API did not return success payload. Body: " + loginResponse);

        writeExcel(uniqueEmail, password);
    }

    private DataRow fetchRandomDataRow() throws Exception {
        String jdbcUrl = "jdbc:mysql://localhost:3306/swethaTest";
        String user = "swetha";
        String password = "3091S@ib";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT r.id_number, r.first_name, r.last_name, r.phone_number, r.occupation, r.gender, u.email FROM registrationdetails r JOIN usernames u ON r.id_number = u.id_number ORDER BY RAND() LIMIT 1")) {
            if (resultSet.next()) {
                return new DataRow(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("occupation"),
                        resultSet.getString("gender")
                );
            }
        }
        throw new IllegalStateException("No data returned from MySQL tables");
    }

    private String postJsonWithRetry(String urlString, String payload) throws Exception {
        String lastResponse = "";
        for (int attempt = 1; attempt <= 5; attempt++) {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            int statusCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            lastResponse = response.toString();

            if (statusCode == 200) {
                return lastResponse;
            }

            if (attempt < 5) {
                Thread.sleep(2000L);
            }
        }

        assertTrue(false, "Expected login API status 200, but got: " + lastResponse);
        return lastResponse;
    }

    private String buildUniqueEmail(String firstName, String lastName) {
        String sanitizedFirst = firstName == null ? "user" : firstName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
        String sanitizedLast = lastName == null ? "user" : lastName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
        long suffix = System.currentTimeMillis() % 1000000;
        return sanitizedFirst + sanitizedLast + suffix + "@example.com";
    }

    private void writeExcel(String email, String password) throws IOException {
        Path excelPath = Paths.get("newdata.xlsx");
        Workbook workbook;
        Sheet sheet;

        if (Files.exists(excelPath)) {
            try (InputStream inputStream = Files.newInputStream(excelPath)) {
                workbook = new XSSFWorkbook(inputStream);
            }
            sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                sheet = workbook.createSheet("Sheet1");
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Email");
            header.createCell(1).setCellValue("Password");
        }

        int nextRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(nextRowIndex);
        row.createCell(0).setCellValue(email);
        row.createCell(1).setCellValue(password);

        try (OutputStream outputStream = Files.newOutputStream(excelPath)) {
            workbook.write(outputStream);
        }
        workbook.close();
    }

    private static class DataRow {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String mobile;
        private final String occupation;
        private final String gender;

        private DataRow(String firstName, String lastName, String email, String mobile, String occupation, String gender) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.mobile = mobile;
            this.occupation = occupation;
            this.gender = gender;
        }
    }
}
