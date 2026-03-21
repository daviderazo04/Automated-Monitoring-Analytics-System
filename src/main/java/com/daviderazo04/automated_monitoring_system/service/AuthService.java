package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.dto.AuthRequestDTO;
import com.daviderazo04.automated_monitoring_system.dto.AuthResponseDTO;
import com.daviderazo04.automated_monitoring_system.dto.RegisterRequestDTO;
import com.daviderazo04.automated_monitoring_system.model.Usuario;
import com.daviderazo04.automated_monitoring_system.repository.UsuarioRepository;
import com.daviderazo04.automated_monitoring_system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO registrar(RegisterRequestDTO request) {
        var user = new Usuario();
        user.setNombreCompleto(request.getNombreCompleto());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActivo(true);
        
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponseDTO autenticar(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}