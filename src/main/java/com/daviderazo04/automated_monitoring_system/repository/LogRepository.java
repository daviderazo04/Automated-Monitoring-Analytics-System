package com.daviderazo04.automated_monitoring_system.repository;

import com.daviderazo04.automated_monitoring_system.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByNivelOrderByFechaDesc(String nivel);
}