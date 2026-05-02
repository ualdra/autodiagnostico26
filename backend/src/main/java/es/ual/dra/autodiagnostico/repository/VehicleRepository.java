package es.ual.dra.autodiagnostico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.ual.dra.autodiagnostico.model.entitity.core.Vehicle;

/**
 * Repositorio para la gestión de la persistencia de la entidad Vehicle.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    java.util.Optional<Vehicle> findByNameAndBrand(String name, String brand);
}
