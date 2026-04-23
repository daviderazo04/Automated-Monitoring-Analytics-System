package com.daviderazo04.automated_monitoring_system.controller;

import com.daviderazo04.automated_monitoring_system.service.PostgresMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class PostgresMetricsController {

    private final PostgresMonitoringService postgresMonitoringService;

    @GetMapping(value = "/postgres/{id}", produces = "text/plain; version=0.0.4; charset=utf-8")
    public String getPostgresMetrics(@PathVariable Long id) {
        return postgresMonitoringService.getMetrics(id);
    }
}