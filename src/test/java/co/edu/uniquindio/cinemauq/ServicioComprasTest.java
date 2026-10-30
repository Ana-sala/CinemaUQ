package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.*;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioComprasTest {

    private final Escenario e = new Escenario();

    private Compra comprarA1yA2() throws Exception {
        Compra compra = e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntrada(e.silla("A1")).agregarEntrada(e.silla("A2")).build();
        return e.servicioCompras.pagar(compra);
    }

    @Test
    void pagarDescuentaSaldoOcupaSillasYDaPuntos() throws Exception {
        Compra compra = comprarA1yA2();
        assertEquals(EstadoCompra.PAGADA, compra.getEstado());
        assertEquals(80_000, e.cliente.getTarjeta().getSaldo());
        assertFalse(e.funcion.estaDisponible(e.silla("A1")));
        assertEquals(20, e.cliente.getCuentaPuntos().getDisponibles(Escenario.AHORA.toLocalDate()));
        assertEquals(1, e.cliente.getCompras().size());
    }

    @Test
    void unaSillaNoSeVendeDosVeces() throws Exception {
        Compra primera = e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("C3")).build();
        Compra segunda = e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("C3")).build();
        e.servicioCompras.pagar(primera);
        assertThrows(AsientoNoDisponibleException.class, () -> e.servicioCompras.pagar(segunda));
    }

    @Test
    void siFallaElPagoNoQuedanCambiosAMedias() throws Exception {
        e.tarjetas.bloquearTarjeta(e.admin, e.cliente);
        Compra compra = e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("A1")).build();
        assertThrows(TarjetaInactivaException.class, () -> e.servicioCompras.pagar(compra));
        assertTrue(e.funcion.estaDisponible(e.silla("A1")));
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
    }

    @Test
    void cancelarConMasDe24HorasDevuelveTodoYUnaSolaVez() throws Exception {
        Compra compra = comprarA1yA2();
        Reembolso reembolso = e.servicioCompras.cancelarCompra(e.cliente, compra);
        assertEquals(100, reembolso.getPorcentaje());
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
        assertTrue(e.funcion.estaDisponible(e.silla("A1")));                     // sillas liberadas
        assertEquals(0, e.cliente.getCuentaPuntos().getDisponibles(Escenario.AHORA.toLocalDate())); // puntos anulados
        assertThrows(CancelacionNoPermitidaException.class, () -> e.servicioCompras.cancelarCompra(e.cliente, compra));
    }

    @Test
    void cancelarEntre2y24HorasDevuelve80PorCiento() throws Exception {
        Compra compra = comprarA1yA2();
        e.hora = e.funcion.getFechaHora().minusHours(5);
        e.servicioCompras.cancelarCompra(e.cliente, compra);
        assertEquals(80_000 + 16_000, e.cliente.getTarjeta().getSaldo());
    }

    @Test
    void conMenosDe2HorasNoSeCancela() throws Exception {
        Compra compra = comprarA1yA2();
        e.hora = e.funcion.getFechaHora().minusMinutes(90);
        assertThrows(CancelacionNoPermitidaException.class, () -> e.servicioCompras.cancelarCompra(e.cliente, compra));
    }

    @Test
    void siElCineCancelaLaFuncionDevuelve100PorCientoAunqueFalteMenosDe2Horas() throws Exception {
        comprarA1yA2();
        e.hora = e.funcion.getFechaHora().minusMinutes(30);
        List<Reembolso> reembolsos = e.servicioCompras.cancelarFuncion(e.admin, e.funcion);
        assertEquals(1, reembolsos.size());
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
        assertFalse(e.funcion.admiteVentas(e.hora));
    }

    @Test
    void soloUnAdministradorCancelaFunciones() {
        assertThrows(OperacionNoAutorizadaException.class, () -> e.servicioCompras.cancelarFuncion(e.cliente, e.funcion));
    }

    @Test
    void redimirPuntosDescuentaLosPuntosYNoCobraEsaParte() throws Exception {
        e.cliente.getCuentaPuntos().acumular(150, Escenario.AHORA.toLocalDate().minusMonths(1), "previos");
        Compra compra = e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntradaConPuntos(e.silla("A1")).build();
        e.servicioCompras.pagar(compra);
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
        assertEquals(50, e.cliente.getCuentaPuntos().getDisponibles(Escenario.AHORA.toLocalDate()));
    }

    @Test
    void sinPuntosSuficientesNoSeRedime() throws Exception {
        Compra compra = e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntradaConPuntos(e.silla("A1")).build();
        assertThrows(PuntosInsuficientesException.class, () -> e.servicioCompras.pagar(compra));
    }
}
