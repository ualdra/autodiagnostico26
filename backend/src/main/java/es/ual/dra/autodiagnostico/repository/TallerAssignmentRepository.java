package es.ual.dra.autodiagnostico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.ual.dra.autodiagnostico.model.entitity.chat.TallerAssignment;

@Repository
public interface TallerAssignmentRepository extends JpaRepository<TallerAssignment, Long> {

    /**
     * Obtener todas las asignaciones activas de un taller
     */
    List<TallerAssignment> findByTallerIdAndActiveTrue(Long tallerId);

    /**
     * Obtener una asignación específica por UUID
     */
    Optional<TallerAssignment> findBySessionUuid(String sessionUuid);

    /**
     * Obtener asignación entre taller y cliente
     */
    Optional<TallerAssignment> findByTallerIdAndClientIdAndActiveTrue(Long tallerId, Long clientId);

    /**
     * Obtener todas las asignaciones para un cliente
     */
    List<TallerAssignment> findByClientIdAndActiveTrue(Long clientId);

    /**
     * Obtener asignaciones por taller y estado
     */
    List<TallerAssignment> findByTallerId(Long tallerId);
}
