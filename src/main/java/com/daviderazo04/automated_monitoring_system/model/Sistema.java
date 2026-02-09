package com.daviderazo04.automated_monitoring_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sistemas")
@Data
public class Sistema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer puerto;

    @Column(columnDefinition = "VARCHAR(100) DEFAULT '/actuator/prometheus'")
    private String path;

    @Column(columnDefinition = "VARCHAR(10) DEFAULT '30s'")
    private String intervalo;

    private boolean monitoreado = true;

    @Column(name = "ultima_sincronizacion")
    private LocalDateTime ultimaSincronizacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "tipo_agente_id")
    private TipoAgente tipoAgente;
}