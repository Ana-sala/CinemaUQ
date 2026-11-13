package co.edu.uniquindio.cinemauq.observador;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;

/** Aviso que recibe un cliente sobre sus compras. */
public record Notificacion(LocalDateTime fecha, String correoCliente, String mensaje) {

    @Override
    public String toString() {
        return Formato.fechaHora(fecha) + " · " + mensaje;
    }
}
