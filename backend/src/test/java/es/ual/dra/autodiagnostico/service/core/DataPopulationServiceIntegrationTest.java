package es.ual.dra.autodiagnostico.service.core;

import es.ual.dra.autodiagnostico.model.entitity.core.Engine;
import es.ual.dra.autodiagnostico.model.entitity.core.Vehicle;
import es.ual.dra.autodiagnostico.model.entitity.core.VehicleModel;
import es.ual.dra.autodiagnostico.repository.EngineRepository;
import es.ual.dra.autodiagnostico.repository.VehicleModelRepository;
import es.ual.dra.autodiagnostico.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DataPopulationServiceIntegrationTest {

    @Autowired
    private DataPopulationService dataPopulationService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    @Autowired
    private EngineRepository engineRepository;

    @Test
    public void testPopulateFromJson() throws IOException {
        // Path to the sample JSON created in T005
        String sampleJsonPath = "src/test/resources/sample-seat.json";
        // When
        dataPopulationService.populateFromFile(sampleJsonPath, "Seat");

        // Then
        List<Vehicle> vehicles = vehicleRepository.findAll();
        System.out.println("\n--- POPULATED VEHICLES ---");
        vehicles.forEach(v -> {
            System.out.println("Vehicle: " + v);
        });
        assertFalse(vehicles.isEmpty(), "Should have populated vehicles");
        
        Vehicle mii = vehicles.stream()
                .filter(v -> v.getName().equals("Seat Mii Ficha Tecnica"))
                .findFirst()
                .orElseThrow();
        
        assertEquals("Seat", mii.getBrand());
        assertEquals("242.1 cm / 95.31 pulgadas", mii.getWheelbase());

        // Cycle/Check for Ibiza
        System.out.println("\n--- IBIZA CYCLE CHECK ---");
        List<Vehicle> ibizas = vehicles.stream()
                .filter(v -> v.getName().toLowerCase().contains("ibiza"))
                .toList();
        ibizas.forEach(v -> System.out.println("Found Ibiza variant: " + v));

        assertTrue(ibizas.size() == 6, "Should have 6 Ibiza variants");

        List<VehicleModel> models = vehicleModelRepository.findAll();
        System.out.println("\n--- POPULATED VEHICLE MODELS ---");
        models.forEach(m -> System.out.println("Model: " + m));
        assertTrue(models.size() >= 2, "Should have at least models for Mii and Ibiza");

        List<Engine> engines = engineRepository.findAll();
        System.out.println("\n--- POPULATED ENGINES ---");
        engines.forEach(e -> System.out.println("Engine: " + e));
        assertFalse(engines.isEmpty(), "Should have populated engines");
    }
}
