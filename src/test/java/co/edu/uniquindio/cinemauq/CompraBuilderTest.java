package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompraBuilderTest {

    private final Escenario e = new Escenario();

    @Test
    void construyeUnaCompraPasoAPaso() throws Exception {
        Compra compra = e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntrada(e.silla("A1"))
                .agregarEntrada(e.silla("E2"))
                .agregarConfiteria(e.gaseosa, 2)
                .build();
        assertEquals(EstadoCompra.PENDIENTE, compra.getEstado());
        assertEquals(2, compra.getEntradas().size());
        assertEquals(10_000 + 13_000 + 14_000, compra.getValorAPagar());
    }

    @Test
    void lasPartesConPuntosNoSeCobranYCalculanSusPuntos() throws Exception {
        Compra compra = e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntradaConPuntos(e.silla("A1"))
                .agregarConfiteria(e.gaseosa, 1)
                .build();
        assertEquals(7_000, compra.getValorAPagar());
        assertEquals(100, compra.getPuntosRedimidos());     // $10.000 / $100 por punto
    }

    @Test
    void noPermiteCompraVaciaNiSillaRepetida() throws Exception {
        CompraBuilder builder = e.servicioCompras.nuevaCompra(e.cliente, e.funcion);
        assertThrows(DatosInvalidosException.class, builder::build);
        builder.agregarEntrada(e.silla("A1"));
        assertThrows(AsientoNoDisponibleException.class, () -> builder.agregarEntrada(e.silla("A1")));
    }

    @Test
    void maximoDiezEntradasPorCompra() throws Exception {
        CompraBuilder builder = e.servicioCompras.nuevaCompra(e.cliente, e.funcion);
        for (int i = 1; i <= 6; i++) {
            builder.agregarEntrada(e.silla("A" + i));
        }
        for (int i = 1; i <= 4; i++) {
            builder.agregarEntrada(e.silla("B" + i));
        }
        assertThrows(DatosInvalidosException.class, () -> builder.agregarEntrada(e.silla("B5")));
    }
}
