# AIAgentMCP

This project demonstrates an end-to-end browser and API automation flow for Rahul Shetty Academy using:

- Playwright for browser automation
- Java + Maven for the test runner
- MySQL for pulling registration data
- REST API calls for login and order flows
- Apache POI for writing results to an Excel workbook

## Running the test

```bash
mvn test -Dtest=RegistrationLoginTest
```

The test registers a user through the UI, verifies the login API contract, and writes the generated email/password to [newdata.xlsx](newdata.xlsx).

The repository also includes the MCP server configuration used to connect Playwright, REST, Excel, MySQL, and filesystem tools.
