package com.practica.bbva.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ValidationUtil {

    public static boolean isValidDni(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    public static boolean isValidNombreApellido(String nombreApellido) {
        return nombreApellido != null && nombreApellido.matches("[a-zA-Z]+");
    }

    public static boolean isValidFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            formatter.format(fechaNacimiento);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}

