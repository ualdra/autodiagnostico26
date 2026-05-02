package es.ual.dra.autodiagnostico.repository;

import es.ual.dra.autodiagnostico.model.entitity.core.Engine;
import es.ual.dra.autodiagnostico.model.entitity.core.EngineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngineRepository extends JpaRepository<Engine, Long> {
    Optional<Engine> findByNameAndEngineType(String name, EngineType engineType);
}
