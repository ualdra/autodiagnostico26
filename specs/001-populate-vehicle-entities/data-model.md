# Data Model: Vehicle Catalog

## Entities

### Vehicle
Represents a vehicle generation/group with general dimensions.
- `idVehicle` (Long, PK)
- `brand` (String)
- `name` (String)
- `wheelbase` (String)
- `averageConsumptionPer100km` (String)
- `height` (String)
- `length` (String)
- `width` (String)
- `weight` (String)
- `periodOfProduction` (String)
- `engineDisplacement` (String)

**Relationships**:
- One-to-Many with `VehicleModel`

### VehicleModel
Represents a specific technical version of a vehicle.
- `idVehicleModel` (Long, PK)
- `modelName` (String)
- `transmission` (TransmissionType, Enum)
- `vehicle` (FK to Vehicle)
- `engine` (FK to Engine)

### Engine
Represents an engine configuration.
- `idEngine` (Long, PK)
- `name` (String)
- `engineType` (EngineType, Enum)

## Enums

### EngineType
- `PETROL`, `DIESEL`, `BEV`, `HEV`, `PHEV`, `REEV`

### TransmissionType
- `MANUAL`, `AUTOMATIC`, etc. (To be populated as encountered, currently ignored by user request)

## Mapping Logic Summary
- **JSON `versions`** -> `Vehicle`
- **JSON `table_versions`** -> `VehicleModel`
- **JSON `table_versions` keys** -> `Engine` (type detection)
