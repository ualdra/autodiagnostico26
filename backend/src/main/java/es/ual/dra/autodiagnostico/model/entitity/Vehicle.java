package es.ual.dra.optcg.model.entitity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un vehículo y sus productos asociados.
 */
@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicle;

    // Marca del vehículo
    private String brand;

    // Modelo del vehículo
    private String name;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehicleModel> vehicleModels = new ArrayList<>();

    /**
     * Helper para añadir modelos de vehículo asegurando la relación bidireccional.
     * 
     * @param vehicleModel Modelo de vehículo a añadir
     */
    public void addVehicleModel(VehicleModel vehicleModel) {
        vehicleModels.add(vehicleModel);
        vehicleModel.setVehicle(this);
    }

}
