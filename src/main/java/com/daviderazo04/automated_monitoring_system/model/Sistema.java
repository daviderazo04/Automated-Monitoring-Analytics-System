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

    @Column(length = 100)
    private String path = "/actuator/prometheus";

    @Column(length = 10)
    private String intervalo = "30s";

    private boolean monitoreado = true;

    @Column(name = "ultima_sincronizacion")
    private LocalDateTime ultimaSincronizacion;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "db_user")
    private String dbUser;

    @Column(name = "db_password")
    private String dbPassword;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "tipo_agente_id")
    private TipoAgente tipoAgente;
}