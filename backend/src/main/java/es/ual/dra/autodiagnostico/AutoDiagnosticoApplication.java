package es.ual.dra.autodiagnostico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import es.ual.dra.autodiagnostico.service.UltimateSpecsVehicleScraperService;

@SpringBootApplication
public class AutoDiagnosticoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoDiagnosticoApplication.class, args);
	}

}
