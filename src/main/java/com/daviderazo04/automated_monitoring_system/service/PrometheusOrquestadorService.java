package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.model.Sistema;
import com.daviderazo04.automated_monitoring_system.repository.SistemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // Para imprimir logs en consola
public class PrometheusOrquestadorService {

    private final SistemaRepository sistemaRepository;

    // Ruta donde se guardará el prometheus.yml (puedes configurarlo en application.properties)
    @Value("${prometheus.config.path:./prometheus.yml}")
    private String prometheusConfigPath;

    public void generarArchivoConfiguracion() {
        try {
            List<Sistema> sistemasActivos = sistemaRepository.findByMonitoreadoTrue();
            StringBuilder yamlBuilder = new StringBuilder();

            // Configuración Global base de Prometheus
            yamlBuilder.append("global:\n");
            yamlBuilder.append("  scrape_interval: 15s\n\n");
            yamlBuilder.append("scrape_configs:\n");

            // Job por defecto (El propio orquestador)
            yamlBuilder.append("  - job_name: 'omniwatch_manager'\n");
            yamlBuilder.append("    static_configs:\n");
            yamlBuilder.append("      - targets: ['localhost:8080']\n\n");

            // Jobs dinámicos desde la Base de Datos
            for (Sistema sistema : sistemasActivos) {
                yamlBuilder.append("  - job_name: '").append(sistema.getAlias()).append("'\n");
                yamlBuilder.append("    scrape_interval: ").append(sistema.getIntervalo()).append("\n");
                yamlBuilder.append("    metrics_path: '").append(sistema.getPath()).append("'\n");

                // Condición para soportar HTTPS en Google Cloud Run
                if (sistema.getPuerto() == 443) {
                    yamlBuilder.append("    scheme: https\n");
                }

                yamlBuilder.append("    static_configs:\n");
                yamlBuilder.append("      - targets: ['").append(sistema.getHost()).append(":").append(sistema.getPuerto()).append("']\n\n");
            }

            // Escribir el archivo
            Path path = Paths.get(prometheusConfigPath);
            Files.writeString(path, yamlBuilder.toString());
            log.info("Archivo prometheus.yml generado exitosamente con {} targets.", sistemasActivos.size());

            // TODO: Aquí luego añadiremos una llamada HTTP POST a http://localhost:9090/-/reload
            // para que Prometheus lea el nuevo archivo sin reiniciarse.

        } catch (Exception e) {
            log.error("Error al generar el archivo prometheus.yml", e);
        }
    }
}