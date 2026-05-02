package es.ual.dra.autodiagnostico.model.entitity.core;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicleModel;

    @ManyToOne
    @JoinColumn(name = "idVehicle")
    private Vehicle vehicle;

    private String modelName;

    private TransmissionType transmission;

    @OneToMany(mappedBy = "vehicleModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PersonalVehicle> personalVehicles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idEngine")
    private Engine engine;
}
