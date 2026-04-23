package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.model.Sistema;
import com.daviderazo04.automated_monitoring_system.repository.SistemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrometheusOrquestadorService {

    private final SistemaRepository sistemaRepository;

    @Value("${prometheus.config.path}")
    private String prometheusConfigPath;

    @Value("${prometheus.api.url}")
    private String prometheusApiUrl;

    // Host where the orchestrator itself is reachable from the Prometheus container.
    // Defaults to host.docker.internal so Prometheus (in docker) can scrape a manager running on the host.
    // Override with manager.host=... for cloud deployments where both share a network.
    @Value("${manager.host:host.docker.internal}")
    private String managerHost;

    @Value("${manager.port:8080}")
    private int managerPort;

    @Value("${prometheus.global.scrape-interval:15s}")
    private String globalScrapeInterval;

    public void generarArchivoConfiguracion() {
        try {
            List<Sistema> sistemasActivos = sistemaRepository.findByMonitoreadoTrue();
            StringBuilder yamlBuilder = new StringBuilder();

            yamlBuilder.append("global:\n");
            yamlBuilder.append("  scrape_interval: ").append(globalScrapeInterval).append("\n\n");
            yamlBuilder.append("scrape_configs:\n");

            // Self-scrape: the orchestrator exposes its own /actuator/prometheus
            yamlBuilder.append("  - job_name: 'automated_monitoring_manager'\n");
            yamlBuilder.append("    metrics_path: '/actuator/prometheus'\n");
            yamlBuilder.append("    static_configs:\n");
            yamlBuilder.append("      - targets: ['").append(managerHost).append(":").append(managerPort).append("']\n\n");

            for (Sistema sistema : sistemasActivos) {
                String jobName = sistema.getAlias();
                String scrapeInterval = sistema.getIntervalo() != null ? sistema.getIntervalo() : globalScrapeInterval;
                String metricsPath = sistema.getPath() != null ? sistema.getPath() : "/actuator/prometheus";
                String host = sistema.getHost();
                int puerto = sistema.getPuerto();
                String scheme = "http";

                if ("dbpostgres".equals(sistema.getTipoAgente().getNombre())) {
                    jobName = "postgres_" + sistema.getAlias();
                    metricsPath = "/api/metrics/postgres/" + sistema.getId();
                    host = managerHost;
                    puerto = managerPort;
                } else if (sistema.getPuerto() == 443) {
                    scheme = "https";
                }

                yamlBuilder.append("  - job_name: ").append(yamlString(jobName)).append("\n");
                yamlBuilder.append("    scrape_interval: ").append(scrapeInterval).append("\n");
                yamlBuilder.append("    metrics_path: ").append(yamlString(metricsPath)).append("\n");

                if ("https".equals(scheme)) {
                    yamlBuilder.append("    scheme: https\n");
                }

                yamlBuilder.append("    static_configs:\n");
                yamlBuilder.append("      - targets: [").append(yamlString(host + ":" + puerto)).append("]\n\n");
            }

            Path path = Paths.get(prometheusConfigPath);
            Files.writeString(path, yamlBuilder.toString());
            log.info("Archivo prometheus.yml generado exitosamente con {} targets.", sistemasActivos.size());

            recargarPrometheusEnCaliente();

        } catch (Exception e) {
            log.error("Error al generar el archivo prometheus.yml", e);
        }
    }

    // Minimal YAML double-quoted string escape so aliases with spaces / special chars don't corrupt the file.
    private String yamlString(String raw) {
        String escaped = raw.replace("\\", "\\\\").replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }

    private void recargarPrometheusEnCaliente() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String reloadEndpoint = prometheusApiUrl + "/-/reload";
            restTemplate.postForEntity(reloadEndpoint, null, String.class);
            log.info("✅ Prometheus recargado exitosamente en: {}", reloadEndpoint);
        } catch (Exception e) {
            log.warn("⚠️ No se pudo recargar Prometheus en {}. ¿Está encendido el contenedor? Error: {}",
                    prometheusApiUrl, e.getMessage());
        }
    }
}
