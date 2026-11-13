package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Rango de fechas (ambas incluidas) sobre el que se calculan estadísticas y reportes. */
public record Periodo(LocalDate desde, LocalDate hasta) {

    /** Crea el periodo validando que las fechas existan y estén en orden. */
    public static Periodo de(LocalDate desde, LocalDate hasta) throws DatosInvalidosException {
        if (desde == null || hasta == null) {
            throw new DatosInvalidosException("Elija la fecha inicial y la final del periodo.");
        }
        if (hasta.isBefore(desde)) {
            throw new DatosInvalidosException("La fecha final no puede ser anterior a la inicial.");
        }
        return new Periodo(desde, hasta);
    }

    /** Periodo que cubre todo lo registrado. */
    public static Periodo todo() {
        return new Periodo(LocalDate.of(2000, 1, 1), LocalDate.of(2100, 12, 31));
    }

    public boolean contiene(LocalDateTime momento) {
        return momento != null && contiene(momento.toLocalDate());
    }

    public boolean contiene(LocalDate dia) {
        return dia != null && !dia.isBefore(desde) && !dia.isAfter(hasta);
    }

    /** Todos los días del periodo, en orden (para los gráficos por día). */
    public List<LocalDate> dias() {
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
            dias.add(d);
        }
        return dias;
    }

    @Override
    public String toString() {
        return Formato.fecha(desde) + " a " + Formato.fecha(hasta);
    }
}
