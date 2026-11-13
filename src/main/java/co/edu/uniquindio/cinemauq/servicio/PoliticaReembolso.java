package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;

import java.time.LocalDateTime;

/**
 * PATRÓN DE COMPORTAMIENTO · STRATEGY (estrategia).
 * Familia de reglas de reembolso intercambiables. ServicioCompras (el contexto) recibe
 * una para las cancelaciones del cliente y otra para las del cine, y no sabe cómo
 * calcula cada una su porcentaje. Una política nueva (festivos, clientes VIP) es una
 * clase nueva que implementa esta interfaz: el servicio no se modifica (OCP).
 */
public interface PoliticaReembolso {

    /**
     * Porcentaje del valor pagado que se devuelve (0 a 100).
     * @throws CancelacionNoPermitidaException si con esta política ya no se puede cancelar
     */
    int porcentajeReembolso(LocalDateTime inicioFuncion, LocalDateTime ahora) throws CancelacionNoPermitidaException;

    /** Motivo con el que queda registrado el reembolso. */
    MotivoReembolso motivo();

    /** Texto corto de la regla, para mostrarlo en la interfaz y en los reportes. */
    String descripcion();
}
