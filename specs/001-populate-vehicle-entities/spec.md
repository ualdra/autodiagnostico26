# Feature Specification: Populate Vehicle Data

**Feature Branch**: `001-populate-vehicle-entities`  
**Created**: 2026-05-02  
**Status**: Draft  
**Input**: User description: "Populate core entities with scripts using JSON data from scraper output without changing existing logic."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Initial Data Population (Priority: P1)

As a system administrator, I want to populate the database with all vehicle models and their specifications from the scraper output so that the application has a comprehensive catalog of cars.

**Why this priority**: This is the core requirement. Without data, the application cannot function for end-users.

**Independent Test**: Can be fully tested by running the population script and verifying that `Vehicle`, `VehicleModel`, and `Engine` entities are correctly created in the database with data matching the JSON source.

**Acceptance Scenarios**:

1. **Given** the scraper output JSON files are present, **When** the population script is executed, **Then** a `Vehicle` entity is created for each entry in the `versions` array of the JSON.
2. **Given** a `Vehicle` is created, **When** its `specifications` are processed, **Then** fields like `wheelbase`, `averageConsumptionPer100km`, `height`, `length`, `width`, `weight`, `periodOfProduction`, and `engineDisplacement` are correctly populated.
3. **Given** a `version` in the JSON has multiple `table_versions`, **When** the script runs, **Then** a `VehicleModel` is created for each entry in `table_versions` and linked to the corresponding `Vehicle`.
4. **Given** a `table_version` entry, **When** the engine type keyword (Gasolina, Diésel, etc.) is found, **Then** an `Engine` entity is created (or reused) with the correct `EngineType` enum value and linked to the `VehicleModel`.

---

### User Story 2 - Idempotent Data Update (Priority: P2)

As a developer, I want the population script to be idempotent so that I can run it multiple times without creating duplicate data or causing errors.

**Why this priority**: Crucial for development and maintenance. Avoids database pollution.

**Independent Test**: Can be tested by running the script twice and verifying that the number of entities in the database remains the same after the second run (or updates existing ones).

**Acceptance Scenarios**:

1. **Given** the database already contains data from a previous run, **When** the script is executed again with the same JSON files, **Then** no new duplicate records are created.

---

### Edge Cases

- **Empty or Malformed JSON**: The script should log a warning and continue with the next file if a JSON file is invalid or missing required fields.
- **Missing Keywords**: If a `table_version` does not contain any known engine type keyword, the script should default to a "General" engine or log a warning.
- **Duplicate Brand/Model Names**: The script should handle cases where the same brand or model appears in different files (e.g., merging or treating as unique if appropriate).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST scan the directory `backend/src/main/resources/scraper-output/Groups` recursively for JSON files.
- **FR-002**: System MUST parse the `models` array in each JSON file.
- **FR-003**: System MUST create a `Vehicle` entity for each version in the `versions` array.
- **FR-004**: System MUST map the `actualFinalModelName` to the `Vehicle` name and fill other attributes from the `specifications` object.
- **FR-005**: System MUST create a `VehicleModel` for each entry in `table_versions`.
- **FR-006**: System MUST detect the `EngineType` by searching for keywords ("Gasolina", "Diésel", "Eléctrico", "HEV", "PHEV", "REEV") in the `table_versions` keys.
- **FR-007**: System MUST create and link an `Engine` entity to each `VehicleModel`.
- **FR-008**: System MUST maintain the brand name (e.g., "Seat") derived from the folder/file structure or JSON content.

### Key Entities *(include if feature involves data)*

- **Vehicle**: Represents a specific vehicle generation or variant group.
- **VehicleModel**: Represents a specific technical version/trim of a vehicle.
- **Engine**: Represents the engine specification, categorized by type.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of valid vehicle entries in the `VAG/ultimatespecs-Seat.json` file are imported into the database.
- **SC-002**: The population script completes processing of one brand folder (e.g., VAG) in under 30 seconds.
- **SC-003**: No duplicate `Engine` entities are created for the same engine name/type combination.

## Assumptions

- **Existing Data Model**: The current logic of `Vehicle`, `VehicleModel`, and `Engine` entities is sufficient and should not be modified.
- **Language**: Keywords for engine types are in Spanish ("Gasolina", "Diésel", etc.) as per the scraper output.
- **Environment**: The script will run as part of the Spring Boot application startup or as a separate task that has access to the Spring context.
