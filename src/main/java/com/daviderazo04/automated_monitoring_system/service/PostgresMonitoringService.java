package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.model.Sistema;
import com.daviderazo04.automated_monitoring_system.repository.SistemaRepository;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostgresMonitoringService {

    private final SistemaRepository sistemaRepository;
    private final Map<Long, DataSource> dataSources = new ConcurrentHashMap<>();
    private final Map<Long, PrometheusMeterRegistry> registries = new ConcurrentHashMap<>();

    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("🚀 Inicializando monitoreo de bases de datos Postgres...");
        sistemaRepository.findByMonitoreadoTrue().stream()
                .filter(s -> s.getTipoAgente() != null && "dbpostgres".equals(s.getTipoAgente().getNombre()))
                .forEach(this::registrarMetricasPostgres);
    }

    public void registrarMetricasPostgres(Sistema sistema) {
        if (sistema.getTipoAgente() == null || !"dbpostgres".equals(sistema.getTipoAgente().getNombre())) {
            return;
        }

        // Skip if already registered (idempotent — prevents duplicate registries on re-invocation)
        if (registries.containsKey(sistema.getId())) {
            log.debug("Métricas ya registradas para {} (id={}), se omite", sistema.getAlias(), sistema.getId());
            return;
        }

        try {
            String url = String.format("jdbc:postgresql://%s:%d/%s",
                    sistema.getHost(), sistema.getPuerto(), sistema.getDbName());

            DataSource dataSource = DataSourceBuilder.create()
                    .url(url)
                    .username(sistema.getDbUser())
                    .password(sistema.getDbPassword())
                    .driverClassName("org.postgresql.Driver")
                    .build();

            PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

            new PostgreSQLDatabaseMetrics(dataSource, sistema.getDbName())
                    .bindTo(registry);

            dataSources.put(sistema.getId(), dataSource);
            registries.put(sistema.getId(), registry);
            log.info("✅ Métricas de Postgres aisladas registradas para el sistema: {} ({}:{}/{})",
                    sistema.getAlias(), sistema.getHost(), sistema.getPuerto(), sistema.getDbName());
        } catch (Exception e) {
            log.error("❌ Error al registrar métricas de Postgres para {}: {}", sistema.getAlias(), e.getMessage());
        }
    }

    public String getMetrics(Long sistemaId) {
        PrometheusMeterRegistry registry = registries.get(sistemaId);
        if (registry != null) {
            return registry.scrape();
        }
        return "# Sistema no encontrado o no es de tipo dbpostgres";
    }

    public DataSource getDataSource(Long sistemaId) {
        return dataSources.get(sistemaId);
    }
}
