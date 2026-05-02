package es.ual.dra.autodiagnostico.service.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ual.dra.autodiagnostico.model.entitity.core.Engine;
import es.ual.dra.autodiagnostico.model.entitity.core.EngineType;
import es.ual.dra.autodiagnostico.model.entitity.core.Vehicle;
import es.ual.dra.autodiagnostico.model.entitity.core.VehicleModel;
import es.ual.dra.autodiagnostico.repository.EngineRepository;
import es.ual.dra.autodiagnostico.repository.VehicleModelRepository;
import es.ual.dra.autodiagnostico.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DataPopulationService {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final EngineRepository engineRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Scans a directory for JSON files and populates the database.
     * 
     * @param rootPath Path to the root folder (e.g. scraper-output/Groups)
     */
    public void scanAndPopulate(String rootPath) {
        try {
            Files.walk(Paths.get(rootPath))
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .filter(p -> !p.getFileName().toString().startsWith("carparts")) // Skip carparts files
                    .forEach(this::processFile);
        } catch (IOException e) {
            log.error("Error scanning directory {}: {}", rootPath, e.getMessage());
        }
    }

    private void processFile(Path path) {
        // Extract brand from filename (e.g. ultimatespecs-Seat.json -> Seat)
        String fileName = path.getFileName().toString();
        String brand = "Unknown";
        if (fileName.contains("-")) {
            brand = fileName.substring(fileName.lastIndexOf("-") + 1, fileName.lastIndexOf("."));
        } else {
            // Or use the parent folder name as brand if filename is generic
            brand = path.getParent().getFileName().toString();
        }
        
        log.info("Processing file: {} for brand: {}", fileName, brand);
        populateFromFile(path.toString(), brand);
    }

    public void populateFromFile(String filePath, String brand) {
        try {
            JsonNode root = objectMapper.readTree(new File(filePath));
            JsonNode models = root.get("models");
            if (models != null && models.isArray()) {
                for (JsonNode modelNode : models) {
                    processModel(modelNode, brand);
                }
            }
        } catch (IOException e) {
            log.error("Error reading file {}: {}", filePath, e.getMessage());
        }
    }

    private void processModel(JsonNode modelNode, String brand) {
        JsonNode versions = modelNode.get("versions");
        if (versions != null && versions.isArray()) {
            for (JsonNode versionNode : versions) {
                Vehicle vehicle = mapToVehicle(versionNode, brand);
                Vehicle finalVehicle = vehicleRepository.findByNameAndBrand(vehicle.getName(), vehicle.getBrand())
                        .orElseGet(() -> vehicleRepository.save(vehicle));
                
                processTableVersions(versionNode.get("table_versions"), finalVehicle);
            }
        }
    }

    private Vehicle mapToVehicle(JsonNode versionNode, String brand) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setName(versionNode.get("actualFinalModelName").asText());
        
        JsonNode specs = versionNode.get("specifications");
        if (specs != null) {
            vehicle.setWheelbase(getSpec(specs, "Batalla:"));
            vehicle.setAverageConsumptionPer100km(getSpec(specs, "Consumos Medio:"));
            vehicle.setHeight(getSpec(specs, "Alto:"));
            vehicle.setLength(getSpec(specs, "Largo:"));
            vehicle.setWidth(getSpec(specs, "Ancho:"));
            vehicle.setWeight(getSpec(specs, "Peso:"));
            vehicle.setPeriodOfProduction(getSpec(specs, "Período de producción:"));
            vehicle.setEngineDisplacement(getSpec(specs, "Cilindrada:"));
        }
        return vehicle;
    }

    private String getSpec(JsonNode specs, String key) {
        JsonNode node = specs.get(key);
        return node != null ? node.asText() : null;
    }

    private void processTableVersions(JsonNode tableVersions, Vehicle vehicle) {
        if (tableVersions != null && tableVersions.isArray()) {
            for (JsonNode entry : tableVersions) {
                String modelName = extractModelName(entry);
                EngineType type = detectEngineType(entry);
                
                if (modelName != null) {
                    Engine engine = engineRepository.findByNameAndEngineType(modelName, type)
                            .orElseGet(() -> engineRepository.save(Engine.builder()
                                    .name(modelName)
                                    .engineType(type)
                                    .build()));
                    
                    VehicleModel vehicleModel = vehicleModelRepository.findByModelNameAndVehicle(modelName, vehicle)
                            .orElseGet(() -> {
                                VehicleModel vm = VehicleModel.builder()
                                        .modelName(modelName)
                                        .vehicle(vehicle)
                                        .engine(engine)
                                        .build();
                                return vehicleModelRepository.save(vm);
                            });
                }
            }
        }
    }

    private String extractModelName(JsonNode entry) {
        // The model name is usually the value of the engine type key
        Iterator<Map.Entry<String, JsonNode>> fields = entry.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            if (isEngineCategoryKey(key)) {
                return field.getValue().asText();
            }
        }
        // Fallback to "Otros" if no category key found
        if (entry.has("Otros")) {
            return entry.get("Otros").asText();
        }
        return null;
    }

    private boolean isEngineCategoryKey(String key) {
        return key.contains("Gasolina") || key.contains("Diesel") || key.contains("Diésel") 
                || key.contains("Eléctrico") || key.contains("Híbrido") || key.contains("HEV")
                || key.contains("PHEV") || key.contains("REEV");
    }

    private EngineType detectEngineType(JsonNode entry) {
        Iterator<String> fieldNames = entry.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            if (key.contains("Gasolina")) return EngineType.PETROL;
            if (key.contains("Diesel") || key.contains("Diésel")) return EngineType.DIESEL;
            if (key.contains("Eléctrico")) return EngineType.BEV;
            if (key.contains("HEV")) return EngineType.HEV;
            if (key.contains("PHEV")) return EngineType.PHEV;
            if (key.contains("REEV")) return EngineType.REEV;
        }
        return EngineType.PETROL; // Default
    }
}
