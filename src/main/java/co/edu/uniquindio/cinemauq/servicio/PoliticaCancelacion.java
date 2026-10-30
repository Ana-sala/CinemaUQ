package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Porcentaje a devolver según el tiempo que falta para la función (RN-04):
 * más de 24 h → 100 %; entre 2 y 24 h → 80 %; menos de 2 h → no se permite.
 */
public class PoliticaCancelacion {

    public int porcentajeReembolso(LocalDateTime inicioFuncion, LocalDateTime ahora) throws CancelacionNoPermitidaException {
        long minutos = Duration.between(ahora, inicioFuncion).toMinutes();
        if (minutos > 24 * 60) {
            return 100;
        }
        if (minutos >= 2 * 60) {
            return 80;
        }
        throw new CancelacionNoPermitidaException("Faltan menos de 2 horas para la función: la compra ya no se puede cancelar.");
    }
}
