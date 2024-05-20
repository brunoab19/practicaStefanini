package com.practica.bbva.repository;

import com.practica.bbva.model.Personas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonasRepository extends JpaRepository<Personas,Long> {
    Optional<Personas> findByDni(String dni);
}
