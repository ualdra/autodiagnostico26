# Quickstart: Populating Vehicle Data

## Prerequisites
- Java 21
- MySQL database running and configured in `application.properties`
- Scraper output JSON files in `backend/src/main/resources/scraper-output/Groups`

## Execution
The population is handled by `DataPopulationService`. You can trigger it by:
1. Running the Spring Boot application (if configured to run on startup).
2. Manually invoking the service via an admin endpoint (to be implemented if needed).

## Verification
Connect to the MySQL database and run:
```sql
SELECT count(*) FROM vehicle;
SELECT count(*) FROM vehicle_model;
SELECT count(*) FROM engine;
```

## Running Tests
Run the integration tests to verify the population logic:
```bash
./mvnw test -Dtest=DataPopulationServiceIntegrationTest
```
