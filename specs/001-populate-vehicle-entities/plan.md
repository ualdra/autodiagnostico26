# Implementation Plan: Populate Vehicle Data

**Branch**: `001-populate-vehicle-entities` | **Date**: 2026-05-02 | **Spec**: [/specs/001-populate-vehicle-entities/spec.md](file:///home/eifm/git/ifm562/autodiagnostico26-ifm562/specs/001-populate-vehicle-entities/spec.md)

## Summary

Implement a data population service in the backend that reads vehicle specifications from JSON files in `src/main/resources/scraper-output/Groups`. The approach will involve:
1. Creating a `DataPopulationService` that uses Jackson to parse the complex JSON structure.
2. Implementing logic to map JSON entries to `Vehicle`, `VehicleModel`, and `Engine` entities.
3. Ensuring idempotency by checking for existing records before insertion.
4. Following TDD by writing integration tests first to verify the population logic against sample JSON data.

## Technical Context

- **Language/Version**: Java 21
- **Primary Dependencies**: Spring Boot 3.4.5, Spring Data JPA, Jackson
- **Storage**: MySQL (managed via JPA)
- **Testing**: JUnit 5, Spring Boot Test (Integration Tests)
- **Project Type**: Spring Boot Backend
- **Performance Goals**: Brand folder processing in < 30s
- **Constraints**: MUST follow TDD. MUST NOT change existing entity logic.

## Constitution Check

- **Cybersecurity Focused (I)**: Using standard libraries (Jackson, Spring). JSoup 1.22.1 is already in `pom.xml`.
- **TDD First (II)**: Mandatory. Tests will be written before implementation.
- **Human Ownership (III)**: User specified Java 21, Spring Boot 4 (using 3.4.5 as per pom), and MySQL.
- **Simplicity (IV)**: Direct mapping from JSON to entities using standard Spring patterns.

## Project Structure

### Documentation (this feature)

```text
specs/001-populate-vehicle-entities/
├── spec.md              # Feature specification
├── plan.md              # This file
├── research.md          # Data mapping and idempotency research
├── data-model.md        # Entity relationships and mapping
└── tasks.md             # Implementation tasks
```

### Source Code (repository root)

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/es/ual/dra/autodiagnostico/
│   │   │   ├── repository/
│   │   │   │   ├── EngineRepository.java (New)
│   │   │   │   ├── VehicleModelRepository.java (New)
│   │   │   │   └── VehicleRepository.java (Existing)
│   │   │   └── service/core/
│   │   │       └── DataPopulationService.java (New)
│   └── test/
│       └── java/es/ual/dra/autodiagnostico/
│           └── service/core/
│               └── DataPopulationServiceIntegrationTest.java (New)
```

**Structure Decision**: New repositories for `Engine` and `VehicleModel` are necessary as they are currently absent. Logic will reside in `service/core`.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| New Repositories | Missing persistence for core entities | Direct JDBC would bypass JPA optimizations and validation |
