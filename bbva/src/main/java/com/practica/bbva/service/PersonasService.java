package com.practica.bbva.service;

import com.practica.bbva.model.Personas;
import com.practica.bbva.repository.PersonasRepository;
import com.practica.bbva.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonasService {

    @Autowired
    private PersonasRepository personasRepository;

    public List<Personas> findAll() {
        return personasRepository.findAll();
    }

    public Personas save(Personas personas) {
        return personasRepository.save(personas);
    }

    public void deleteById(Long id) {
        personasRepository.deleteById(id);
    }

    public Optional<Personas> findById(Long id) {
        return personasRepository.findById(id);
    }

    public Optional<Personas> findByDni(String dni) {
        return personasRepository.findByDni(dni);
    }
}




