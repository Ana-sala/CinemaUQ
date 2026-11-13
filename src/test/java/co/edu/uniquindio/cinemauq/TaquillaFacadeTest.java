package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.fachada.Cotizacion;
import co.edu.uniquindio.cinemauq.fachada.PedidoConfiteria;
import co.edu.uniquindio.cinemauq.fachada.TaquillaFacade;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.servicio.EquivalenciaPuntos;
import co.edu.uniquindio.cinemauq.servicio.ServicioPuntos;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Facade: tres operaciones simples sobre todo el subsistema de compra. */
class TaquillaFacadeTest {

    private final Escenario e = new Escenario();
    private final TaquillaFacade taquilla = new TaquillaFacade(e.servicioCompras,
            new ServicioPuntos(new EquivalenciaPuntos(100), e.reloj));

    @Test
    void cotizarNoCobraYComprarCobraLoMismoQueSeCotizo() throws Exception {
        List<PedidoConfiteria> pedido = List.of(new PedidoConfiteria(e.gaseosa, 2, false));
        Cotizacion cotizacion = taquilla.cotizar(e.cliente, e.funcion, List.of(e.silla("A1")), false, pedido);
        assertEquals(24_000, cotizacion.total());
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());          // cotizar no cobra

        Compra compra = taquilla.comprar(e.cliente, e.funcion, List.of(e.silla("A1")), false, pedido);
        assertEquals(cotizacion.total(), compra.getValorPagado());
        assertEquals(76_000, e.cliente.getTarjeta().getSaldo());
    }

    @Test
    void cancelarPorLaFachadaDevuelveElDinero() throws Exception {
        Compra compra = taquilla.comprar(e.cliente, e.funcion, List.of(e.silla("B1")), false, List.of());
        Reembolso reembolso = taquilla.cancelar(e.cliente, compra);
        assertEquals(100, reembolso.getPorcentaje());
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
    }

    @Test
    void lasExcepcionesDelSubsistemaLleganIgualAlCliente() {
        List<PedidoConfiteria> mucho = List.of(new PedidoConfiteria(e.gaseosa, 20, false));   // 140.000
        assertThrows(SaldoInsuficienteException.class,
                () -> taquilla.comprar(e.cliente, e.funcion, List.of(), false, mucho));
    }
}
