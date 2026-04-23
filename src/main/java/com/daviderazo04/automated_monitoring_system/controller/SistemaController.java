package com.daviderazo04.automated_monitoring_system.controller;

import com.daviderazo04.automated_monitoring_system.dto.SistemaRequestDTO;
import com.daviderazo04.automated_monitoring_system.dto.SistemaResponseDTO;
import com.daviderazo04.automated_monitoring_system.service.SistemaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sistemas")
@RequiredArgsConstructor
public class SistemaController {

    private final SistemaService sistemaService;

    @GetMapping
    public ResponseEntity<List<SistemaResponseDTO>> listarSistemas() {
        return ResponseEntity.ok(sistemaService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<SistemaResponseDTO> registrarSistema(
            @Valid @RequestBody SistemaRequestDTO dto,
            HttpServletRequest request) {

        String emailUsuario = "admin@local.com";
        
        String ipOrigen = request.getRemoteAddr();

        SistemaResponseDTO response = sistemaService.registrarSistema(dto, emailUsuario, ipOrigen);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}