package co.edu.uniquindio.cinemauq.observador;

import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;

/** Alerta para el administrador: una función superó el umbral de ocupación. */
public record AlertaOcupacion(LocalDateTime fecha, Funcion funcion, double porcentaje) {

    @Override
    public String toString() {
        return Formato.fechaHora(fecha) + " · " + funcion.getPelicula().getTitulo() + " · " + funcion.getSala().getNombre()
                + " · función " + Formato.fechaHora(funcion.getFechaHora()) + " · " + String.format("%.0f %%", porcentaje) + " ocupada";
    }
}
