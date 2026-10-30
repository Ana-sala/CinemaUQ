package co.edu.uniquindio.cinemauq.util;

import java.time.LocalDateTime;

/**
 * Abstraccion de la hora actual (DIP). Las reglas que dependen del tiempo
 * (cancelaciones, vencimiento de puntos) piden la hora a esta interfaz,
 * asi las pruebas pueden usar una hora fija.
 */
@FunctionalInterface
public interface Reloj {
    LocalDateTime ahora();
}
