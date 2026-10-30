package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.fabrica.CatalogoFabricasEntrada;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.EntradaGeneral;
import co.edu.uniquindio.cinemauq.modelo.EntradaPreferencial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Factory Method: la clase de entrada depende del tipo de asiento. */
class FabricaEntradaTest {

    private final Escenario e = new Escenario();
    private final CatalogoFabricasEntrada fabricas = new CatalogoFabricasEntrada();

    @Test
    void asientoGeneralProduceEntradaGeneral() throws Exception {
        Entrada entrada = fabricas.emitir(e.funcion, e.silla("A1"));
        assertInstanceOf(EntradaGeneral.class, entrada);
        assertEquals(10_000, entrada.getPrecio());
    }

    @Test
    void asientoPreferencialCuesta30PorCientoMas() throws Exception {
        Entrada entrada = fabricas.emitir(e.funcion, e.silla("E1"));
        assertInstanceOf(EntradaPreferencial.class, entrada);
        assertEquals(13_000, entrada.getPrecio());
    }

    @Test
    void noEmiteEntradaParaUnAsientoVendido() throws Exception {
        e.funcion.ocupar(e.silla("A1"));
        assertThrows(AsientoNoDisponibleException.class, () -> fabricas.emitir(e.funcion, e.silla("A1")));
    }
}
