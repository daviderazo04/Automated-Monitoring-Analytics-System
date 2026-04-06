package com.daviderazo04.automated_monitoring_system;

import com.daviderazo04.automated_monitoring_system.model.TipoAgente;
import com.daviderazo04.automated_monitoring_system.repository.TipoAgenteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(TipoAgenteRepository repository) {
		return args -> {
			if (repository.findByNombre("SPRING_BOOT").isEmpty()) {
				TipoAgente springBootAgente = new TipoAgente();
				// Dejamos que la BD asigne el ID (IDENTITY empezará en 1)
				springBootAgente.setNombre("SPRING_BOOT");
				springBootAgente.setDescripcion("Agente apps Springboot");
				repository.save(springBootAgente);
				System.out.println("✅ Se ha pre-registrado el TipoAgente: SPRING_BOOT (ID autogenerado)");
			}
		};
	}

}
