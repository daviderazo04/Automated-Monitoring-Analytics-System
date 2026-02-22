package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.dto.SistemaRequestDTO;
import com.daviderazo04.automated_monitoring_system.dto.SistemaResponseDTO;
import com.daviderazo04.automated_monitoring_system.model.Sistema;
import com.daviderazo04.automated_monitoring_system.model.TipoAgente;
import com.daviderazo04.automated_monitoring_system.repository.SistemaRepository;
import com.daviderazo04.automated_monitoring_system.repository.TipoAgenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SistemaService {

    private final SistemaRepository sistemaRepository;
    private final TipoAgenteRepository tipoAgenteRepository;
    private final PrometheusOrquestadorService orquestadorService;
    private final LogService logService;

    public List<SistemaResponseDTO> obtenerTodos() {
        return sistemaRepository.findAll().stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    // MÉTODOS PÚBLICOS (El flujo principal se lee claramente)
    @Transactional
    public SistemaResponseDTO registrarSistema(SistemaRequestDTO dto, String usuarioActual, String ip) {
        Sistema sistemaGuardado = persistirNuevoSistema(dto);
        actualizarInfraestructuraPrometheus();
        registrarAuditoriaDeCreacion(sistemaGuardado, usuarioActual, ip);

        return mapearAResponseDTO(sistemaGuardado);
    }

    // MÉTODOS ATÓMICOS PRIVADOS (Detalles de implementación)
    private Sistema persistirNuevoSistema(SistemaRequestDTO dto) {
        Sistema nuevoSistema = construirEntidadDesdeDTO(dto);
        return sistemaRepository.save(nuevoSistema);
    }

    private Sistema construirEntidadDesdeDTO(SistemaRequestDTO dto) {
        Sistema sistema = new Sistema();
        sistema.setAlias(dto.getAlias());
        sistema.setHost(dto.getHost());
        sistema.setPuerto(dto.getPuerto());
        sistema.setPath(dto.getPath() != null ? dto.getPath() : "/actuator/prometheus");
        sistema.setIntervalo(dto.getIntervalo() != null ? dto.getIntervalo() : "30s");
        sistema.setUltimaSincronizacion(LocalDateTime.now());
        sistema.setMonitoreado(true);

        TipoAgente tipo = tipoAgenteRepository.findById(dto.getTipoAgenteId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Agente no válido"));
        sistema.setTipoAgente(tipo);

        // TODO: Cuando implementemos JWT, aquí setearemos el usuario basado en el token
        return sistema;
    }

    private void actualizarInfraestructuraPrometheus() {
        orquestadorService.generarArchivoConfiguracion();
    }

    private void registrarAuditoriaDeCreacion(Sistema sistema, String usuario, String ip) {
        logService.registrarAccion(
                "INFO", "MONITOR", "REGISTRO_SISTEMA",
                "Se registró el sistema: " + sistema.getAlias() + " en " + sistema.getHost(),
                usuario, ip
        );
    }

    private SistemaResponseDTO mapearAResponseDTO(Sistema sistema) {
        SistemaResponseDTO dto = new SistemaResponseDTO();
        dto.setId(sistema.getId());
        dto.setAlias(sistema.getAlias());
        dto.setHost(sistema.getHost());
        dto.setPuerto(sistema.getPuerto());
        dto.setPath(sistema.getPath());
        dto.setIntervalo(sistema.getIntervalo());
        dto.setMonitoreado(sistema.isMonitoreado());
        dto.setUltimaSincronizacion(sistema.getUltimaSincronizacion());
        dto.setTipoAgenteNombre(sistema.getTipoAgente().getNombre());
        return dto;
    }
}