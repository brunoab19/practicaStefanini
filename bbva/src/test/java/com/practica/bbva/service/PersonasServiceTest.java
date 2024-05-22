package com.practica.bbva.service;

import com.practica.bbva.model.Personas;
import com.practica.bbva.repository.PersonasRepository;
import com.practica.bbva.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PersonasServiceTest {

    @Mock
    private PersonasRepository personasRepository;

    @InjectMocks
    private PersonasService personasService;

    private Personas mockPersona;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockPersona = new Personas();
        mockPersona.setId(1L);
        mockPersona.setNombre("Juan");
        mockPersona.setApellido("Perez");
        mockPersona.setFechaNacimiento(LocalDate.of(2001, 11, 1));
        mockPersona.setDni("12345678");
    }

    @Test
    public void testFindAll() {
        when(personasRepository.findAll()).thenReturn(Arrays.asList(mockPersona));

        List<Personas> result = personasService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombre());
    }

    @Test
    public void testSaveSuccess() {
        when(personasRepository.save(any(Personas.class))).thenReturn(mockPersona);
        when(personasRepository.findByDni(mockPersona.getDni())).thenReturn(Optional.empty());

        Personas result = personasService.save(mockPersona);
        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        assertEquals("Perez", result.getApellido());
    }

    @Test
    public void testDeleteById() {
        doNothing().when(personasRepository).deleteById(1L);

        personasService.deleteById(1L);
        verify(personasRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testFindByDni() {
        when(personasRepository.findByDni("12345678")).thenReturn(Optional.of(mockPersona));

        Optional<Personas> result = personasService.findByDni("12345678");
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getNombre());
    }

    @Test
    public void testFindById() {
        when(personasRepository.findById(1L)).thenReturn(Optional.of(mockPersona));

        Optional<Personas> result = personasService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Juan", result.get().getNombre());
    }
}


