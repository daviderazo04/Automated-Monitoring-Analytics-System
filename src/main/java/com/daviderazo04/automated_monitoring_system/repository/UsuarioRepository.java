package com.daviderazo04.automated_monitoring_system.repository;

import com.daviderazo04.automated_monitoring_system.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);
}