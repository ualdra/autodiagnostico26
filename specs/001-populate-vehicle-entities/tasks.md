# Tasks: Populate Vehicle Data

**Input**: Design documents from `/specs/001-populate-vehicle-entities/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 [P] Create project structure for `service.core` package in `backend/src/main/java/es/ual/dra/autodiagnostico/service/core`
- [ ] T002 Configure MySQL test database in `backend/src/test/resources/application-test.properties`

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [ ] T003 [P] Create `EngineRepository` in `backend/src/main/java/es/ual/dra/autodiagnostico/repository/EngineRepository.java`
- [ ] T004 [P] Create `VehicleModelRepository` in `backend/src/main/java/es/ual/dra/autodiagnostico/repository/VehicleModelRepository.java`

**Checkpoint**: Foundation ready - repositories available for persistence.

## Phase 3: User Story 1 - Initial Data Population (Priority: P1) 🎯 MVP

**Goal**: Populate database with all vehicle models and specifications from scraper output.

**Independent Test**: Run `DataPopulationServiceIntegrationTest` and verify entities are created in the test database.

### Tests for User Story 1 (MANDATORY per TDD principle)

- [ ] T005 [P] [US1] Create sample JSON file in `backend/src/test/resources/sample-seat.json` for testing
- [ ] T006 [US1] Implement integration test `DataPopulationServiceIntegrationTest` in `backend/src/test/java/es/ual/dra/autodiagnostico/service/core/DataPopulationServiceIntegrationTest.java` verifying that `Vehicle`, `VehicleModel`, and `Engine` are created. **Verify failure (Red).**

### Implementation for User Story 1

- [ ] T007 [P] [US1] Implement `DataPopulationService` skeleton in `backend/src/main/java/es/ual/dra/autodiagnostico/service/core/DataPopulationService.java`
- [ ] T008 [US1] Implement recursive directory scanning logic in `DataPopulationService`
- [ ] T009 [US1] Implement JSON parsing logic for `models` and `versions` using Jackson
- [ ] T010 [US1] Implement mapping logic from JSON `specifications` to `Vehicle` entity fields
- [ ] T011 [US1] Implement mapping logic from `table_versions` to `VehicleModel` and `Engine` (type detection)
- [ ] T012 [US1] Run tests and ensure they pass (Green)

**Checkpoint**: User Story 1 fully functional and testable.

## Phase 4: User Story 2 - Idempotent Data Update (Priority: P2)

**Goal**: Ensure the script can be run multiple times without duplicating data.

**Independent Test**: Run `DataPopulationServiceIntegrationTest` twice and assert database counts remain constant.

### Tests for User Story 2

- [ ] T013 [US2] Update `DataPopulationServiceIntegrationTest` with a test case for re-running the population. **Verify failure (Red).**

### Implementation for User Story 2

- [ ] T014 [US2] Implement existence checks for `Engine` (name/type) and `Vehicle` (brand/name) in `DataPopulationService`
- [ ] T015 [US2] Run tests and ensure they pass (Green)

## Phase 5: Polish & Cross-Cutting Concerns

- [ ] T016 Add comprehensive logging for import progress and errors
- [ ] T017 [P] Verify SC-002: Process `VAG` brand folder in under 30 seconds
- [ ] T018 Run final validation against `quickstart.md`

---

## Dependencies & Execution Order

1. **Phase 1 & 2** are prerequisites for all User Stories.
2. **User Story 1 (P1)** is the MVP and must be completed first.
3. **User Story 2 (P2)** builds upon User Story 1 logic.
4. **Phase 5** is the final polish.

## Parallel Execution Examples

```bash
# Parallel Repositories
- [ ] T003 Create EngineRepository
- [ ] T004 Create VehicleModelRepository

# Parallel US1 Setup
- [ ] T005 Create sample JSON file
- [ ] T007 Implement DataPopulationService skeleton
```
