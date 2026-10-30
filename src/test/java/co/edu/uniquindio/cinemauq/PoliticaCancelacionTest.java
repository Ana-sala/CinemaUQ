package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.servicio.PoliticaCancelacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaCancelacionTest {

    private final PoliticaCancelacion politica = new PoliticaCancelacion();
    private final LocalDateTime funcion = LocalDateTime.of(2026, 11, 10, 19, 0);

    @Test
    void masDe24HorasDevuelveTodo() throws Exception {
        assertEquals(100, politica.porcentajeReembolso(funcion, funcion.minusHours(25)));
    }

    @Test
    void entre2y24HorasDevuelve80() throws Exception {
        assertEquals(80, politica.porcentajeReembolso(funcion, funcion.minusHours(24)));
        assertEquals(80, politica.porcentajeReembolso(funcion, funcion.minusHours(2)));
    }

    @Test
    void menosDe2HorasNoSePermite() {
        assertThrows(CancelacionNoPermitidaException.class,
                () -> politica.porcentajeReembolso(funcion, funcion.minusMinutes(119)));
    }
}
