package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.model.Sistema;
import com.daviderazo04.automated_monitoring_system.repository.SistemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SistemaService {

    private final SistemaRepository sistemaRepository;
    private final PrometheusOrquestadorService orquestadorService;
    private final LogService logService;

    public List<Sistema> obtenerTodos() {
        return sistemaRepository.findAll();
    }

    @Transactional
    public Sistema registrarSistema(Sistema nuevoSistema, String usuarioActual, String ip) {
        nuevoSistema.setUltimaSincronizacion(LocalDateTime.now());
        Sistema sistemaGuardado = sistemaRepository.save(nuevoSistema);

        // Disparamos la actualización de Prometheus
        orquestadorService.generarArchivoConfiguracion();

        // Registramos la acción en la auditoría
        logService.registrarAccion(
                "INFO", "MONITOR", "REGISTRO_SISTEMA",
                "Se registró el sistema: " + sistemaGuardado.getAlias() + " en " + sistemaGuardado.getHost(),
                usuarioActual, ip
        );

        return sistemaGuardado;
    }

    @Transactional
    public void eliminarSistema(Long id, String usuarioActual, String ip) {
        sistemaRepository.findById(id).ifPresent(sistema -> {
            sistemaRepository.delete(sistema);

            // Re-generamos el archivo YAML ya sin este sistema
            orquestadorService.generarArchivoConfiguracion();

            logService.registrarAccion(
                    "WARN", "MONITOR", "ELIMINACION_SISTEMA",
                    "Se eliminó el sistema: " + sistema.getAlias(),
                    usuarioActual, ip
            );
        });
    }
}