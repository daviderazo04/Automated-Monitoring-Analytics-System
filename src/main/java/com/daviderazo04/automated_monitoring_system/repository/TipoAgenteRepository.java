package com.daviderazo04.automated_monitoring_system.repository;

import com.daviderazo04.automated_monitoring_system.model.TipoAgente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoAgenteRepository extends JpaRepository<TipoAgente, Integer> {
    Optional<TipoAgente> findByNombre(String nombre);
}