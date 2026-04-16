package es.ual.dra.autodiagnostico.model.entitity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

/**
 * Entidad que representa un producto asociado a un vehículo.
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    // Nombre del producto
    private String name;

    // Descripción del producto
    private String description;

    // Precio del producto
    private Double price;

    private String image;

    // Vehículo al que pertenece el producto
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "vm_has_product", joinColumns = @JoinColumn(name = "idProduct"), inverseJoinColumns = @JoinColumn(name = "idVehicleModel"))
    private List<Vehicle> vehicles;
}
