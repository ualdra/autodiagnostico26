package es.ual.dra.optcg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.ual.dra.optcg.model.entitity.Vehicle;

/**
 * Repositorio para la gestión de la persistencia de la entidad Vehicle.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
