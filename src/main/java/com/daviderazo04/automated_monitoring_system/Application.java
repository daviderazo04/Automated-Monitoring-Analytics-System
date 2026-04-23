package com.daviderazo04.automated_monitoring_system;

import com.daviderazo04.automated_monitoring_system.model.TipoAgente;
import com.daviderazo04.automated_monitoring_system.repository.TipoAgenteRepository;
import com.daviderazo04.automated_monitoring_system.service.PrometheusOrquestadorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(TipoAgenteRepository repository,
										PrometheusOrquestadorService orquestadorService) {
		return args -> {
			if (repository.findByNombre("SPRING_BOOT").isEmpty()) {
				TipoAgente springBootAgente = new TipoAgente();
				springBootAgente.setNombre("SPRING_BOOT");
				springBootAgente.setDescripcion("Agente apps Springboot");
				repository.save(springBootAgente);
				System.out.println("✅ Se ha pre-registrado el TipoAgente: SPRING_BOOT (ID 1)");
			}
			if (repository.findByNombre("dbpostgres").isEmpty()) {
				TipoAgente dbPostgresAgente = new TipoAgente();
				dbPostgresAgente.setNombre("dbpostgres");
				dbPostgresAgente.setDescripcion("Agente base de datos Postgres");
				repository.save(dbPostgresAgente);
				System.out.println("✅ Se ha pre-registrado el TipoAgente: dbpostgres (ID 2)");
			}

			// Regenerate prometheus.yml on startup so the scrape targets always reflect the DB state
			orquestadorService.generarArchivoConfiguracion();
		};
	}

}
