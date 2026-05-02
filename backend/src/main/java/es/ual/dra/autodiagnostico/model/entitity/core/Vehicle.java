package es.ual.dra.autodiagnostico.model.entitity.core;

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

    // Estos atributos existen porque se sacaron en el scraping. Su tipado
    // como String no es el más adecuado, pero se dejará así por simplicidad. Al
    // menos por ahora.
    private String wheelbase;
    private String averageConsumptionPer100km;
    private String height;
    private String length;
    private String width;
    private String weight;
    private String periodOfProduction;
    private String engineDisplacement;

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
