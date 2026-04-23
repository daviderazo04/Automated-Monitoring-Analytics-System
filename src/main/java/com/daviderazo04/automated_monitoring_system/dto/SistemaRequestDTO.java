package com.daviderazo04.automated_monitoring_system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SistemaRequestDTO {

    @NotBlank(message = "El alias es obligatorio")
    private String alias;

    @NotBlank(message = "El host es obligatorio")
    private String host;

    @NotNull(message = "El puerto es obligatorio")
    @Min(value = 1, message = "El puerto no puede ser menor a 1")
    @Max(value = 65535, message = "El puerto no puede ser mayor a 65535")
    private Integer puerto;

    private String path = "/actuator/prometheus";

    private String intervalo = "30s";

    @NotNull(message = "El ID del tipo de agente es obligatorio")
    private Integer tipoAgenteId;

    private String dbName;
    private String dbUser;
    private String dbPassword;

    // Temporalmente lo pedimos por request. Más adelante, con JWT,
    // este ID se extraerá directamente del token de sesión del usuario logueado.
    private Long usuarioId;
}