package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.enums.AlcancePromocion;
import co.edu.uniquindio.cinemauq.precio.CalculadoraPrecio;
import co.edu.uniquindio.cinemauq.precio.DescuentoConfiteria;
import co.edu.uniquindio.cinemauq.precio.DescuentoEntradas;
import co.edu.uniquindio.cinemauq.precio.PrecioBase;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Decorator: las promociones envuelven el precio y se apilan. */
class PrecioDecoratorTest {

    private final Escenario e = new Escenario();

    @Test
    void losDecoradoresSeApilanYCadaUnoRebajaSuParte() throws Exception {
        var builder = e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntrada(e.silla("A1")).agregarEntrada(e.silla("A2"))   // 20.000
                .agregarConfiteria(e.gaseosa, 2);                                 // 14.000
        CalculadoraPrecio precio = new PrecioBase(builder.getEntradas(), builder.getLineas());
        precio = new DescuentoEntradas(precio, "Martes", 50);
        precio = new DescuentoConfiteria(precio, "Dulce", 10);
        precio = new DescuentoEntradas(precio, "Estudiante", 20);

        assertEquals(8_000, precio.getSubtotalEntradas());      // 20.000 × 0,5 × 0,8
        assertEquals(12_600, precio.getSubtotalConfiteria());   // 14.000 × 0,9
        assertEquals(20_600, precio.getTotal());
        assertEquals(5, precio.getDetalle().size());             // 2 subtotales + 3 descuentos
    }

    @Test
    void unaPromocionDeMartesSoloAplicaAFuncionesDeMartes() throws Exception {
        DayOfWeek diaFuncion = e.funcion.getFechaHora().getDayOfWeek();
        e.promociones.registrarPromocion("Hoy no", 50, AlcancePromocion.ENTRADAS, diaFuncion.plus(1),
                Escenario.AHORA.toLocalDate(), Escenario.AHORA.toLocalDate().plusMonths(1));
        Compra sinPromo = e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("A1")).build();
        assertEquals(10_000, sinPromo.getValorAPagar());

        e.promociones.registrarPromocion("Hoy sí", 50, AlcancePromocion.ENTRADAS, diaFuncion,
                Escenario.AHORA.toLocalDate(), Escenario.AHORA.toLocalDate().plusMonths(1));
        Compra conPromo = e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("A2")).build();
        assertEquals(5_000, conPromo.getValorAPagar());
        assertTrue(conPromo.getDetallePrecio().stream().anyMatch(l -> l.startsWith("Hoy sí")));
    }

    @Test
    void elPagoCobraElPrecioConDescuentoYDaPuntosSobreEse() throws Exception {
        e.promociones.registrarPromocion("Todo el mes", 50, AlcancePromocion.ENTRADAS, null,
                Escenario.AHORA.toLocalDate(), Escenario.AHORA.toLocalDate().plusMonths(1));
        Compra compra = e.servicioCompras.pagar(e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntrada(e.silla("A1")).agregarEntrada(e.silla("A2")).build());
        assertEquals(10_000, compra.getValorPagado());
        assertEquals(90_000, e.cliente.getTarjeta().getSaldo());
        assertEquals(10, compra.getPuntosGanados());
    }
}
