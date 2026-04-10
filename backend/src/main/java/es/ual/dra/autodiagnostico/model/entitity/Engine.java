package es.ual.dra.optcg.model.entitity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "engine")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Engine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEngine;
    private String name;
    private EngineType engineType;

    @OneToMany(mappedBy = "engine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehicleModel> vehicleModels = new ArrayList<>();
}
