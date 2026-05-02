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
        assertFalse(vehicles.isEmpty(), "Should have populated vehicles");
        
        Vehicle vehicle = vehicles.stream()
                .filter(v -> v.getName().equals("Seat Mii Ficha Tecnica"))
                .findFirst()
                .orElseThrow();
        
        assertEquals("Seat", vehicle.getBrand());
        assertEquals("242.1 cm / 95.31 pulgadas", vehicle.getWheelbase());

        List<VehicleModel> models = vehicleModelRepository.findAll();
        assertTrue(models.size() >= 2, "Should have at least 2 models for Mii");

        List<Engine> engines = engineRepository.findAll();
        assertFalse(engines.isEmpty(), "Should have populated engines");
    }
}
