package co.edu.uniquindio.cinemauq.estado;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;

/** STATE · estado final: la compra fue cancelada y reembolsada. No admite ninguna operación (RN-04). */
public final class EstadoCancelada implements EstadoDeCompra {

    public static final EstadoCancelada INSTANCIA = new EstadoCancelada();

    private EstadoCancelada() {
    }

    @Override
    public EstadoDeCompra pagar(Compra compra) throws DatosInvalidosException {
        throw new DatosInvalidosException("La compra " + compra.getId() + " fue cancelada: no se puede pagar.");
    }

    @Override
    public EstadoDeCompra cancelar(Compra compra) throws ReembolsoDuplicadoException {
        throw new ReembolsoDuplicadoException("La compra " + compra.getId() + " ya fue reembolsada.");
    }

    @Override
    public EstadoCompra tipo() {
        return EstadoCompra.CANCELADA;
    }
}
