# 📊 Automated Monitoring & Analytics System

![Java 17](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Flexible_DB-blue.svg)
![Prometheus](https://img.shields.io/badge/Prometheus-Auto_Config-e6522c.svg)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

## 📝 Descripción del Proyecto
[span_1](start_span)Este proyecto es un prototipo de sistema de monitoreo de rendimiento desarrollado como plan de disertación para la Pontificia Universidad Católica del Ecuador (PUCE)[span_1](end_span). 

[span_2](start_span)Las empresas de desarrollo de software suelen enfrentar problemas de visibilidad por tener mecanismos de supervisión dispersos o basados en revisiones manuales[span_2](end_span). [span_3](start_span)[span_4](start_span)Este backend actúa como un **Orquestador Centralizado** que soluciona la falta de visibilidad en infraestructuras tecnológicas, permitiendo centralizar la información sobre el estado y las métricas de uso de recursos de múltiples servicios[span_3](end_span)[span_4](end_span). 

[span_5](start_span)[span_6](start_span)El sistema incluye un mecanismo de registro semi-automático que actualiza la configuración de Prometheus sin necesidad de editar archivos manualmente, facilitando el monitoreo continuo de nuevas aplicaciones[span_5](end_span)[span_6](end_span).

## ✨ Características Principales
* **[span_7](start_span)[span_8](start_span)Base de Datos Flexible:** Soporta el registro y categorización de diversos tipos de agentes, desde aplicaciones modernas con Spring Boot (Micrometer) hasta sistemas legacy mediante `jmx_exporter`[span_7](end_span)[span_8](end_span).
* **[span_9](start_span)Auto-Configuración de Prometheus:** Motor interno que lee los sistemas activos en la base de datos y genera dinámicamente el archivo `prometheus.yml`[span_9](end_span).
* **Módulo de Seguridad Integral:** Autenticación basada en roles (RBAC) para proteger el registro de las plataformas y el acceso a los endpoints.
* **Isla de Auditoría (Logs):** Registro asíncrono y persistente de todas las acciones del sistema para trazabilidad y auditoría forense, sin comprometer el rendimiento.
* **[span_10](start_span)Integración con Grafana:** Preparado para exponer métricas que alimentan dashboards dinámicos para el análisis del comportamiento histórico y en tiempo real[span_10](end_span).

## 🛠️ Stack Tecnológico
* **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Security (JWT).
* **Base de Datos:** PostgreSQL (Diseño normalizado en 3NF).
* **[span_11](start_span)Observabilidad:** Actuator, Micrometer, Prometheus, Grafana[span_11](end_span).
* **Construcción:** Gradle.

## 🚀 Instalación y Uso

### Prerrequisitos
* Java 17 o superior.
* PostgreSQL 14+ corriendo localmente o en la nube.
* Prometheus y Grafana (Opcional, para la visualización completa).

### Configuración Local
1. Clonar el repositorio:
   ```bash
   git clone [https://github.com/daviderazo04/automated-monitoring-analyzer.git](https://github.com/daviderazo04/automated-monitoring-analyzer.git)
