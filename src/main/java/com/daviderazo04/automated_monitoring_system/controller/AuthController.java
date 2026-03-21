package com.daviderazo04.automated_monitoring_system.controller;

import com.daviderazo04.automated_monitoring_system.dto.AuthRequestDTO;
import com.daviderazo04.automated_monitoring_system.dto.AuthResponseDTO;
import com.daviderazo04.automated_monitoring_system.dto.RegisterRequestDTO;
import com.daviderazo04.automated_monitoring_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(
            @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.ok(service.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> autenticar(
            @RequestBody AuthRequestDTO request
    ) {
        return ResponseEntity.ok(service.autenticar(request));
    }
}