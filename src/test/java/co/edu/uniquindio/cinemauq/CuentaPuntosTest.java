package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.cinemauq.modelo.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.CuentaPuntos;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Usa el ejemplo de la tabla del enunciado: 300, 500 y 200 puntos en tres fechas. */
class CuentaPuntosTest {

    private CuentaPuntos cuentaDelEnunciado() throws Exception {
        CuentaPuntos cuenta = new CuentaPuntos();
        cuenta.acumular(300, LocalDate.of(2026, 9, 10), "c1");
        cuenta.acumular(500, LocalDate.of(2026, 11, 20), "c2");
        cuenta.acumular(200, LocalDate.of(2027, 2, 5), "c3");
        return cuenta;
    }

    @Test
    void redimePrimeroLosMasProximosAVencer() throws Exception {
        CuentaPuntos cuenta = cuentaDelEnunciado();
        cuenta.redimir(400, LocalDate.of(2027, 3, 1));
        List<AcumulacionPuntos> lotes = cuenta.getAcumulaciones();
        assertEquals(0, lotes.get(0).getPuntosDisponibles());    // vence 10/09/2027: se usa completo
        assertEquals(400, lotes.get(1).getPuntosDisponibles());  // vence 20/11/2027: se usan 100
        assertEquals(200, lotes.get(2).getPuntosDisponibles());
    }

    @Test
    void losPuntosVencidosNoCuentanNiSeUsan() throws Exception {
        CuentaPuntos cuenta = cuentaDelEnunciado();
        LocalDate despuesDelPrimerVencimiento = LocalDate.of(2027, 9, 10);
        assertEquals(700, cuenta.getDisponibles(despuesDelPrimerVencimiento));
        assertThrows(PuntosInsuficientesException.class, () -> cuenta.redimir(800, despuesDelPrimerVencimiento));
    }

    @Test
    void verificaLaCantidadAntesDeRedimir() throws Exception {
        CuentaPuntos cuenta = cuentaDelEnunciado();
        assertThrows(PuntosInsuficientesException.class, () -> cuenta.redimir(1_001, LocalDate.of(2027, 3, 1)));
        assertEquals(1_000, cuenta.getDisponibles(LocalDate.of(2027, 3, 1)));   // no tocó nada
    }
}
