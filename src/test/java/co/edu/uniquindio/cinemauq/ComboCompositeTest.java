package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Combo;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Producto;
import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Composite: un combo puede contener productos y otros combos. */
class ComboCompositeTest {

    private final Escenario e = new Escenario();

    @Test
    void elPrecioDeUnComboDentroDeOtroAplicaCadaDescuentoEnSuNivel() throws Exception {
        Producto crispetas = e.confiteria.registrarProducto("Crispetas", 16_000, CategoriaProducto.CRISPETAS);
        Producto nachos = e.confiteria.registrarProducto("Nachos", 14_000, CategoriaProducto.SNACK);
        Combo pareja = e.confiteria.registrarCombo("Pareja", 10, List.of(crispetas, e.gaseosa, e.gaseosa));
        Combo mega = e.confiteria.registrarCombo("Mega", 5, List.<ItemConfiteria>of(pareja, nachos));

        assertEquals(27_000, pareja.getPrecio());                    // (16.000 + 7.000 + 7.000) × 0,90
        assertEquals(Math.round((27_000 + 14_000) * 0.95), mega.getPrecio());
        assertEquals(4, mega.contarProductos());
        assertTrue(mega.describir("").contains("   + Pareja"));       // el combo interno aparece con sangría
    }

    @Test
    void unComboNoPuedeContenerseASiMismo() throws Exception {
        Combo a = e.confiteria.registrarCombo("A", 0, List.of(e.gaseosa, e.gaseosa));
        Combo b = e.confiteria.registrarCombo("B", 0, List.<ItemConfiteria>of(a, e.gaseosa));
        assertThrows(DatosInvalidosException.class, () -> a.agregar(b));
        assertThrows(DatosInvalidosException.class, () -> a.agregar(a));
    }

    @Test
    void siUnProductoInternoNoEstaDisponibleElComboTampoco() throws Exception {
        Combo combo = e.confiteria.registrarCombo("C", 0, List.of(e.gaseosa, e.gaseosa));
        e.gaseosa.setDisponible(false);
        assertFalse(combo.isDisponible());
    }
}
