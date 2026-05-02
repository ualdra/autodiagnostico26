package es.ual.dra.autodiagnostico.model.entitity.core;

public enum EngineType {
    PETROL, // Coche de gasolina
    DIESEL, // Coche diésel
    BEV, // Vehículo eléctrico de batería (coche eléctrico)
    HEV, // Vehículo híbrido eléctrico: combina gasolina/diésel con una batería eléctrica
         // auto-recargable y motor
    PHEV, // Vehículo híbrido enchufable: combina gasolina/diésel con una batería y motor
          // eléctricos que permiten recarga externa
    REEV // Vehículo eléctrico de batería con un motor auxiliar que solo carga la batería
         // y no interviene en la propulsión
}