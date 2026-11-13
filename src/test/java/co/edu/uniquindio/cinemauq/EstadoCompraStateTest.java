package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.estado.EstadoCancelada;
import co.edu.uniquindio.cinemauq.estado.EstadoPagada;
import co.edu.uniquindio.cinemauq.estado.EstadoPendiente;
import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** State: cada estado de la compra decide qué operación permite. */
class EstadoCompraStateTest {

    private final Escenario e = new Escenario();

    private Compra nueva() throws Exception {
        return e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("B1")).build();
    }

    @Test
    void unaCompraPendienteNoSeCancela() throws Exception {
        Compra compra = nueva();
        assertSame(EstadoPendiente.INSTANCIA, compra.getEstadoActual());
        assertThrows(CancelacionNoPermitidaException.class, () -> e.servicioCompras.cancelarCompra(e.cliente, compra));
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
    }

    @Test
    void unaCompraPagadaNoSeCobraDosVeces() throws Exception {
        Compra compra = e.servicioCompras.pagar(nueva());
        assertSame(EstadoPagada.INSTANCIA, compra.getEstadoActual());
        assertThrows(DatosInvalidosException.class, () -> e.servicioCompras.pagar(compra));
        assertEquals(90_000, e.cliente.getTarjeta().getSaldo());      // se cobró una sola vez
    }

    @Test
    void unaCompraCanceladaNoAdmiteNada() throws Exception {
        Compra compra = e.servicioCompras.pagar(nueva());
        e.servicioCompras.cancelarCompra(e.cliente, compra);
        assertSame(EstadoCancelada.INSTANCIA, compra.getEstadoActual());
        assertThrows(ReembolsoDuplicadoException.class, () -> e.servicioCompras.cancelarCompra(e.cliente, compra));
        assertThrows(DatosInvalidosException.class, () -> e.servicioCompras.pagar(compra));
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());     // se devolvió una sola vez
    }
}
