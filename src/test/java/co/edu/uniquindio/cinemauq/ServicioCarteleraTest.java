package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.ConflictoHorarioException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioCarteleraTest {

    private final Escenario e = new Escenario();

    @Test
    void noSeCruzanDosFuncionesEnLaMismaSala() {
        // la función existente dura 100 min + 20 de limpieza
        assertThrows(ConflictoHorarioException.class, () -> e.cartelera.programarFuncion(e.pelicula, e.sala,
                e.funcion.getFechaHora().plusMinutes(110), 10_000));
    }

    @Test
    void noSeProgramaEnElPasado() {
        assertThrows(DatosInvalidosException.class, () -> e.cartelera.programarFuncion(e.pelicula, e.sala,
                Escenario.AHORA.minusDays(1), 10_000));
    }

    @Test
    void prototypeDuplicaLaFuncionConOtraFechaYSinSillasVendidas() throws Exception {
        e.funcion.ocupar(e.silla("A1"));
        Funcion copia = e.cartelera.duplicarFuncion(e.funcion, e.funcion.getFechaHora().plusDays(1));
        assertNotEquals(e.funcion.getId(), copia.getId());
        assertSame(e.funcion.getPelicula(), copia.getPelicula());
        assertSame(e.funcion.getSala(), copia.getSala());
        assertEquals(e.funcion.getPrecioBase(), copia.getPrecioBase());
        assertEquals(e.funcion.getFechaHora().plusDays(1), copia.getFechaHora());
        assertTrue(copia.estaDisponible(e.silla("A1")));
    }
}
