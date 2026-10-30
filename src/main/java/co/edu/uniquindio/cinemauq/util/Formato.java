package co.edu.uniquindio.cinemauq.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilidades de formato para mostrar dinero y fechas en la interfaz y los reportes. */
public final class Formato {

    private static final Locale CO = Locale.of("es", "CO");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("EEE dd/MM/yyyy HH:mm", CO);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", CO);

    private Formato() {
    }

    public static String moneda(double valor) {
        return "$" + String.format(CO, "%,.0f", valor);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha.format(FECHA_HORA);
    }

    public static String fecha(LocalDate fecha) {
        return fecha.format(FECHA);
    }
}
