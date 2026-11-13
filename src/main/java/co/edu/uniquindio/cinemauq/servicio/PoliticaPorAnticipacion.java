package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * STRATEGY · estrategia concreta para cuando cancela el cliente (RN-04).
 * El porcentaje depende de cuánto falta para la función:
 * más de 24 h → 100 %; entre 2 y 24 h → 80 %; menos de 2 h → no se permite.
 */
public class PoliticaPorAnticipacion implements PoliticaReembolso {

    private static final long MINUTOS_TOTAL = 24 * 60;
    private static final long MINUTOS_MINIMO = 2 * 60;

    @Override
    public int porcentajeReembolso(LocalDateTime inicioFuncion, LocalDateTime ahora) throws CancelacionNoPermitidaException {
        long minutos = Duration.between(ahora, inicioFuncion).toMinutes();
        if (minutos > MINUTOS_TOTAL) {
            return 100;
        }
        if (minutos >= MINUTOS_MINIMO) {
            return 80;
        }
        throw new CancelacionNoPermitidaException("Faltan menos de 2 horas para la función: la compra ya no se puede cancelar.");
    }

    @Override
    public MotivoReembolso motivo() {
        return MotivoReembolso.CANCELACION_CLIENTE;
    }

    @Override
    public String descripcion() {
        return "Más de 24 h: 100 % · entre 2 y 24 h: 80 % · menos de 2 h: no se puede cancelar";
    }
}
