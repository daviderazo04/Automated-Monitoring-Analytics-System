package com.daviderazo04.automated_monitoring_system.controller;

import com.daviderazo04.automated_monitoring_system.model.TipoAgente;
import com.daviderazo04.automated_monitoring_system.repository.TipoAgenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-agente")
@RequiredArgsConstructor
public class TipoAgenteController {

    private final TipoAgenteRepository tipoAgenteRepository;

    @GetMapping
    public List<TipoAgente> listarTodos() {
        return tipoAgenteRepository.findAll();
    }
}