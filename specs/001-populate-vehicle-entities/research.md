# Research: Vehicle Data Population

## Data Mapping

### JSON Structure to Entity Mapping
The JSON files follow a hierarchical structure: `models` -> `versions` -> `table_versions` & `specifications`.

- **Vehicle**: Created from each entry in the `versions` array.
    - `name` <- `actualFinalModelName`
    - `brand` <- Extracted from folder/file name (e.g., "Seat")
    - `wheelbase` <- `specifications["Batalla:"]`
    - `averageConsumptionPer100km` <- `specifications["Consumos Medio:"]`
    - `height` <- `specifications["Alto:"]`
    - `length` <- `specifications["Largo:"]`
    - `width` <- `specifications["Ancho:"]`
    - `weight` <- `specifications["Peso:"]`
    - `periodOfProduction` <- `specifications["Período de producción:"]`
    - `engineDisplacement` <- `specifications["Cilindrada:"]`

- **VehicleModel**: Created from each entry in the `table_versions` array.
    - `modelName` <- The key representing the engine category (e.g., "Motores de Gasolina") or the value associated with it. *Correction*: The value of the category key is the specific model name (e.g., "Mii electric Ficha Tecnica").
    - `transmission` <- To be inferred if possible, otherwise null (spec says "Ignore url for now").

- **Engine**:
    - `name` <- Derived from the category key or description in `table_version`.
    - `engineType` <- Detected via keyword matching:
        - "Gasolina" -> `PETROL`
        - "Diésel" / "Diesel" -> `DIESEL`
        - "Eléctrico" -> `BEV`
        - "HEV" -> `HEV`
        - "PHEV" -> `PHEV`
        - "REEV" -> `REEV`

## Idempotency Strategy

To ensure multiple runs don't duplicate data:
1. **Engine**: Lookup by `name` and `engineType`. If exists, reuse.
2. **Vehicle**: Lookup by `name` and `brand`. If exists, update fields.
3. **VehicleModel**: Lookup by `modelName` and associated `Vehicle`. If exists, skip or update.

## Performance Considerations
- Use batch inserts (configured via `spring.jpa.properties.hibernate.jdbc.batch_size`).
- Disable redundant logging during large imports.
- Scan files recursively using `Files.walk()`.

## Decisions
- **Decision**: Use `Jackson` with `JsonNode` for flexible parsing of dynamic keys in `table_versions`.
- **Rationale**: The keys in `table_versions` are not fixed (they represent the engine type in Spanish). A static POJO would be difficult to maintain.
- **Alternatives considered**: Static POJOs (rejected due to dynamic keys), Manual string parsing (rejected as brittle).
