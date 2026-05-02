package es.ual.dra.autodiagnostico.model.entitity.core;

public enum TransmissionType {
    MT, // Transmisión Manual: El vehículo cuenta con embrague que el usuario utiliza y
        // un selector de marchas
    AT, // Transmisión Automática: El vehículo cuenta con un sistema de embrague, pero
        // el usuario no tiene que hacer nada para circular
    CVT, // Transmisión Variable Continua: Es un tipo de transmisión automática que puede
         // cambiar la relación de cambio a cualquier valor dentro de sus límites y según
         // las necesidades de la marcha
    iMT, // Transmisión Manual Inteligente: Es un sistema sin pedal de embrague que
         // combina la sensación de una caja manual con la comodidad de un automático.
         // Usa un embrague electrónico al mover la palanca de cambios
    DCT, // Transmisión de Doble Embrague: Es una caja de cambios automática de doble
         // embrague. Este cambio automático es de tipo rápido y mejora la experiencia de
         // conducción, reduciendo el consumo y los tiempos de aceleración.
    eCVT, // Transmisión Continuamente Variable Electrónica: Es un sistema avanzado usado
          // en vehículos híbridos (principalmente Toyota/Lexus) que gestiona la potencia
          // mediante engranajes planetarios y motores eléctricos en lugar de correas y
          // poleas
    DSG // Caja de Cambios de Cambio Directo: Es una transmisión automática de doble
        // embrague desarrollada por el Grupo VAG desde 2003. Usa dos embragues para
        // preseleccionar marchas, permitiendo cambios ultra rápidos
}