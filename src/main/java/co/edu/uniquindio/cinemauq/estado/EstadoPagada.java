package co.edu.uniquindio.cinemauq.estado;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;

/** STATE · estado concreto: la compra ya se cobró. No se cobra otra vez; sí se puede cancelar. */
public final class EstadoPagada implements EstadoDeCompra {

    public static final EstadoPagada INSTANCIA = new EstadoPagada();

    private EstadoPagada() {
    }

    @Override
    public EstadoDeCompra pagar(Compra compra) throws DatosInvalidosException {
        throw new DatosInvalidosException("La compra " + compra.getId() + " ya fue pagada: no se cobra dos veces.");
    }

    @Override
    public EstadoDeCompra cancelar(Compra compra) {
        return EstadoCancelada.INSTANCIA;
    }

    @Override
    public EstadoCompra tipo() {
        return EstadoCompra.PAGADA;
    }
}
