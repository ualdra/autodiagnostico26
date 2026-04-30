package es.ual.dra.autodiagnostico.model.entitity.chat;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "taller_assignment",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_taller_client_session", columnNames = {"taller_id", "client_id", "session_uuid"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TallerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del usuario con rol TALLER que atiende
     */
    @Column(name = "taller_id", nullable = false)
    private Long tallerId;

    /**
     * ID del cliente (usuario normal) siendo atendido
     */
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    /**
     * UUID único para esta sesión de chat
     * Permite que un TALLER tenga múltiples conversaciones simultáneas
     */
    @Column(name = "session_uuid", nullable = false, unique = true, length = 36)
    private String sessionUuid;

    /**
     * Estado de la asignación
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Descripción del problema o tema
     */
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (sessionUuid == null) {
            sessionUuid = UUID.randomUUID().toString();
        }
        createdAt = now;
        updatedAt = now;
    }
}
