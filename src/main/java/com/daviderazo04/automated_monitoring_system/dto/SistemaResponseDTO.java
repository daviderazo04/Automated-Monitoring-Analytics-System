package com.daviderazo04.automated_monitoring_system.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SistemaResponseDTO {
    private Long id;
    private String alias;
    private String host;
    private Integer puerto;
    private String path;
    private String intervalo;
    private boolean monitoreado;
    private LocalDateTime ultimaSincronizacion;
    private String tipoAgenteNombre;
    private String dbName;
    private String dbUser;
    private String dbPassword;
}