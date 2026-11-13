package co.edu.uniquindio.cinemauq.estado;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;

/** STATE · estado concreto: la compra se armó pero aún no se cobra. Solo admite pagar. */
public final class EstadoPendiente implements EstadoDeCompra {

    public static final EstadoPendiente INSTANCIA = new EstadoPendiente();

    private EstadoPendiente() {
    }

    @Override
    public EstadoDeCompra pagar(Compra compra) {
        return EstadoPagada.INSTANCIA;
    }

    @Override
    public EstadoDeCompra cancelar(Compra compra) throws CancelacionNoPermitidaException {
        throw new CancelacionNoPermitidaException("La compra " + compra.getId() + " no se ha pagado: no hay nada que reembolsar.");
    }

    @Override
    public EstadoCompra tipo() {
        return EstadoCompra.PENDIENTE;
    }
}
