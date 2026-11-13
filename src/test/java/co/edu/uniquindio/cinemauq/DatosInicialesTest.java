package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.datos.DatosIniciales;
import co.edu.uniquindio.cinemauq.servicio.Periodo;
import co.edu.uniquindio.cinemauq.servicio.ServicioEstadisticas;
import co.edu.uniquindio.cinemauq.util.RelojAjustable;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Los datos iniciales traen un historial con el que los reportes y gráficos tienen qué mostrar. */
class DatosInicialesTest {

    private final CinemaUQ cine = CinemaUQ.getInstance();
    private final ServicioEstadisticas est = cine.getEstadisticas();
    private final LocalDate hoy = LocalDate.now();
    private final Periodo historial = new Periodo(hoy.minusDays(DatosIniciales.DIAS_HISTORIAL), hoy.minusDays(1));

    @Test
    void elHistorialTieneVentasCancelacionesYRecargas() {
        assertTrue(est.cantidadFunciones(historial) >= 100, "funciones: " + est.cantidadFunciones(historial));
        assertTrue(est.cantidadCompras(historial) >= 250, "compras: " + est.cantidadCompras(historial));
        assertTrue(est.entradasVendidas(historial) >= 500, "entradas: " + est.entradasVendidas(historial));
        assertTrue(est.cantidadCancelaciones(historial) >= 10, "cancelaciones: " + est.cantidadCancelaciones(historial));
        assertTrue(est.totalRecargas(historial) > 0);
        assertFalse(est.confiteriaPorProducto(historial).isEmpty());
        assertEquals(est.ventasPorDia(historial).size(), DatosIniciales.DIAS_HISTORIAL);
        assertTrue(est.ventasPorDia(historial).values().stream().allMatch(v -> v > 0), "todos los días hay ventas");
    }

    @Test
    void hayPuntosRedimidosYUnaAlertaDeOcupacion() {
        assertTrue(est.puntosOtorgados(historial) > 0);
        assertTrue(est.puntosRedimidos(historial) > 0);
        assertFalse(cine.getMonitorOcupacion().getAlertas().isEmpty());
    }

    @Test
    void alTerminarElRelojVuelveALaHoraReal() {
        RelojAjustable reloj = (RelojAjustable) cine.getReloj();
        assertFalse(reloj.estaFijado());
        assertTrue(Duration.between(reloj.ahora(), LocalDateTime.now()).abs().toSeconds() < 5);
    }
}
