package com.daviderazo04.automated_monitoring_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Data
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha = LocalDateTime.now();

    private String nivel;

    private String origen;

    private String accion;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    private String usuario;

    private String ip;
}