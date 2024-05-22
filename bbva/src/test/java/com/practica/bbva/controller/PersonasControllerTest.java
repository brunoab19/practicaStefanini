package com.practica.bbva.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practica.bbva.model.Personas;
import com.practica.bbva.repository.PersonasRepository;
import com.practica.bbva.service.PersonasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(PersonasController.class)
public class PersonasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonasRepository personasRepository;

    @MockBean
    PersonasService personasService;

    @InjectMocks
    private PersonasController personasController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Personas mockPersona;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(personasController).build();

        mockPersona = new Personas();
        mockPersona.setId(1L);
        mockPersona.setNombre("Juan");
        mockPersona.setApellido("Perez");
        mockPersona.setFechaNacimiento(LocalDate.of(2001, 11, 1));
        mockPersona.setDni("12345678");
    }

    @Test
    public void testGetAllPersons() throws Exception {
        when(personasRepository.findAll()).thenReturn(Arrays.asList(mockPersona));

        mockMvc.perform(get("/api/personas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].apellido").value("Perez"));
    }

    @Test
    public void testGetPersonByIdFound() throws Exception {
        when(personasRepository.findById(1L)).thenReturn(Optional.of(mockPersona));

        mockMvc.perform(get("/api/personas/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    public void testGetPersonByIdNotFound() throws Exception {
        when(personasRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/personas/id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró ninguna persona con el ID proporcionado."));
    }

    @Test
    public void testGetPersonByDniFound() throws Exception {
        when(personasRepository.findByDni("12345678")).thenReturn(Optional.of(mockPersona));

        mockMvc.perform(get("/api/personas/dni/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    public void testGetPersonByDniNotFound() throws Exception {
        when(personasRepository.findByDni("12345678")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/personas/dni/12345678"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró ninguna persona con el DNI proporcionado."));
    }

    @Test
    public void testCreatePersonSuccess() throws Exception {
        when(personasService.save(any(Personas.class))).thenReturn(mockPersona);

        ResultActions result = mockMvc.perform(post("/api/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockPersona)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    public void testCreatePersonInvalidData() throws Exception {
        Personas invalidPersona = new Personas();
        invalidPersona.setNombre("123");

        when(personasService.save(any(Personas.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        ResultActions result = mockMvc.perform(post("/api/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPersona)));

        result.andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    public void testUpdatePersonSuccess() throws Exception {
        when(personasService.findById(1L)).thenReturn(Optional.of(mockPersona));
        when(personasService.save(any(Personas.class))).thenReturn(mockPersona);

        ResultActions result = mockMvc.perform(put("/api/personas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockPersona)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    public void testUpdatePersonNotFound() throws Exception {
        when(personasService.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(put("/api/personas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockPersona)));

        result.andExpect(status().isBadRequest())
                .andExpect(content().string("No se encontró ninguna persona con el ID proporcionado"));
    }

    @Test
    public void testUpdatePersonInvalidData() throws Exception {
        when(personasService.findById(1L)).thenReturn(Optional.of(mockPersona));
        when(personasService.save(any(Personas.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        ResultActions result = mockMvc.perform(put("/api/personas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockPersona)));

        result.andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    public void testDeletePersonSuccess() throws Exception {
        doNothing().when(personasService).deleteById(1L);

        mockMvc.perform(delete("/api/personas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("La persona con ID 1 se eliminó correctamente."));
    }

    @Test
    public void testDeletePersonNotFound() throws Exception {
        doThrow(new NoSuchElementException()).when(personasService).deleteById(1L);

        mockMvc.perform(delete("/api/personas/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró ninguna persona con el ID proporcionado."));
    }
}
