package com.practica.bbva.controller;

import com.practica.bbva.model.Personas;
import com.practica.bbva.repository.PersonasRepository;
import com.practica.bbva.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
    public class PersonasController {

        @Autowired
        private PersonasRepository personasService;

        @GetMapping
        public List<Personas> getAllPersons() {
            return personasService.findAll();
        }

        @GetMapping("/id/{id}")
        public ResponseEntity<?> getPersonById(@PathVariable Long id) {
            Optional<Personas> personas = personasService.findById(id);
            if (personas.isPresent()) {
                return ResponseEntity.ok(personas.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ninguna persona con el ID proporcionado.");
            }
        }

        @GetMapping("/dni/{dni}")
        public ResponseEntity<?> getPersonByDni(@PathVariable String dni) {
            Optional<Personas> personas = personasService.findByDni(dni);
            if (personas.isPresent()) {
                return ResponseEntity.ok(personas.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ninguna persona con el DNI proporcionado.");
            }
        }

        @PostMapping
        public ResponseEntity<?> createPerson(@RequestBody Personas personas) {
            if (!ValidationUtil.isValidNombreApellido(personas.getNombre()) || !ValidationUtil.isValidNombreApellido(personas.getApellido())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nombre y apellido debe contener solo letras");
            }
            if (!ValidationUtil.isValidFechaNacimiento(personas.getFechaNacimiento())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha de nacimiento no debe estar vacía y debe seguir el formato AAAA-MM-DD");
            }
            if (!ValidationUtil.isValidDni(personas.getDni())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El DNI debe contener exactamente 8 dígitos");
            }


            Optional<Personas> existingPerson = personasService.findByDni(personas.getDni());
            if (existingPerson.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El DNI ya está en uso");
            }

            Personas savedPerson = personasService.save(personas);
            return ResponseEntity.ok(savedPerson);
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> updatePerson(@PathVariable Long id, @RequestBody Personas personDetails) {
            if (!ValidationUtil.isValidNombreApellido(personDetails.getNombre()) || !ValidationUtil.isValidNombreApellido(personDetails.getApellido())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nombre y apellido debe contener solo letras");
            }
            if (!ValidationUtil.isValidFechaNacimiento(personDetails.getFechaNacimiento())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha de nacimiento no debe estar vacía y debe seguir el formato AAAA-MM-DD");
            }
            if (!ValidationUtil.isValidDni(personDetails.getDni())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El DNI debe contener únicamente letras y números y tener exactamente 8 caracteres");
            }

            Optional<Personas> personaOptional = personasService.findById(id);
            if (personaOptional.isPresent()) {
                Personas persona = personaOptional.get();


                Optional<Personas> existingPersonWithDni = personasService.findByDni(personDetails.getDni());
                if (existingPersonWithDni.isPresent() && !existingPersonWithDni.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El DNI ya está en uso");
                }

                persona.setNombre(personDetails.getNombre());
                persona.setApellido(personDetails.getApellido());
                persona.setFechaNacimiento(personDetails.getFechaNacimiento());
                persona.setDni(personDetails.getDni());
                personasService.save(persona);
                return ResponseEntity.ok(persona);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró ninguna persona con el ID proporcionado");
            }
        }



        @DeleteMapping("/{id}")
        public ResponseEntity<String> deletePerson(@PathVariable Long id) {
            try {
                personasService.deleteById(id);
                return ResponseEntity.ok("La persona con ID " + id + " se eliminó correctamente.");
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ninguna persona con el ID proporcionado.");
            }
        }
    }


