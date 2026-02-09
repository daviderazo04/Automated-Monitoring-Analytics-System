package com.daviderazo04.automated_monitoring_system.repository;

import com.daviderazo04.automated_monitoring_system.model.Sistema;
import com.daviderazo04.automated_monitoring_system.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SistemaRepository extends JpaRepository<Sistema, Long> {
    List<Sistema> findByUsuario(Usuario usuario);

    List<Sistema> findByMonitoreadoTrue();
}