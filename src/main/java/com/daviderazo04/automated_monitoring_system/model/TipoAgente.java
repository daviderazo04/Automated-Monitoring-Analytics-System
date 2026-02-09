package com.daviderazo04.automated_monitoring_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipos_agente")
@Data
public class TipoAgente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;
}