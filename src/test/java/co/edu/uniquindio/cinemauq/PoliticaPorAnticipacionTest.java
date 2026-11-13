package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;
import co.edu.uniquindio.cinemauq.servicio.PoliticaPorAnticipacion;
import co.edu.uniquindio.cinemauq.servicio.PoliticaReembolso;
import co.edu.uniquindio.cinemauq.servicio.PoliticaReembolsoTotal;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Strategy: cada política de reembolso se prueba sola, sin servicio ni datos. */
class PoliticaPorAnticipacionTest {

    private final PoliticaReembolso cliente = new PoliticaPorAnticipacion();
    private final PoliticaReembolso cine = new PoliticaReembolsoTotal();
    private final LocalDateTime funcion = LocalDateTime.of(2026, 11, 20, 19, 0);

    @Test
    void masDe24HorasDevuelveTodo() throws Exception {
        assertEquals(100, cliente.porcentajeReembolso(funcion, funcion.minusHours(25)));
        assertEquals(MotivoReembolso.CANCELACION_CLIENTE, cliente.motivo());
    }

    @Test
    void entre2y24HorasDevuelve80() throws Exception {
        assertEquals(80, cliente.porcentajeReembolso(funcion, funcion.minusHours(24)));
        assertEquals(80, cliente.porcentajeReembolso(funcion, funcion.minusHours(2)));
    }

    @Test
    void menosDe2HorasNoSePermite() {
        assertThrows(CancelacionNoPermitidaException.class,
                () -> cliente.porcentajeReembolso(funcion, funcion.minusMinutes(119)));
    }

    @Test
    void elCineSiempreDevuelveTodo() throws Exception {
        assertEquals(100, cine.porcentajeReembolso(funcion, funcion.minusMinutes(10)));
        assertEquals(MotivoReembolso.FUNCION_CANCELADA, cine.motivo());
    }
}
