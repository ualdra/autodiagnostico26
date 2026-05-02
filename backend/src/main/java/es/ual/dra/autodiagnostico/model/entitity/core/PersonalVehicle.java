package es.ual.dra.autodiagnostico.model.entitity.core;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personal_vehicle")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersonalVehicle;

    @ManyToOne
    @JoinColumn(name = "idVehicleModel")
    private VehicleModel vehicleModel;

    private LocalDate buildDate;
    private String VIN;
    private String plate;
}
