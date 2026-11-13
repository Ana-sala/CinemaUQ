package co.edu.uniquindio.cinemauq.estado;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;

/**
 * PATRÓN DE COMPORTAMIENTO · STATE (estado).
 * Una compra se comporta distinto según su estado: pendiente, pagada o cancelada.
 * Cada estado es una clase que decide, en un solo lugar, qué operación permite y cuál
 * es el estado siguiente. Compra (el contexto) delega en su estado, así que ni ella ni
 * ServicioCompras tienen condicionales sobre el estado (evita cobrar o reembolsar dos veces).
 *
 * Los estados no guardan datos: devuelven el estado siguiente sin modificar la compra,
 * por eso se pueden consultar antes de aplicar cambios.
 */
public interface EstadoDeCompra {

    /** Estado al que pasa la compra al pagarse, o excepción si no se puede pagar. */
    EstadoDeCompra pagar(Compra compra) throws CinemaUQException;

    /** Estado al que pasa la compra al cancelarse, o excepción si no se puede cancelar. */
    EstadoDeCompra cancelar(Compra compra) throws CinemaUQException;

    /** Valor del enum, solo para mostrar el estado en pantalla y en reportes. */
    EstadoCompra tipo();
}
