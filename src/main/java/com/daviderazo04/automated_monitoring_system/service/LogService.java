package com.daviderazo04.automated_monitoring_system.service;

import com.daviderazo04.automated_monitoring_system.model.Log;
import com.daviderazo04.automated_monitoring_system.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Async // Ejecuta en un hilo separado para no bloquear el sistema
    public void registrarAccion(String nivel, String origen, String accion, String detalle, String usuario, String ip) {
        Log log = new Log();
        log.setNivel(nivel);
        log.setOrigen(origen);
        log.setAccion(accion);
        log.setDetalle(detalle);
        log.setUsuario(usuario);
        log.setIp(ip);

        logRepository.save(log);
    }
}