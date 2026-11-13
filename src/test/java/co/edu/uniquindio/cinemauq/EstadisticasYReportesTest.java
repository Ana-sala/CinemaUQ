package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.reporte.ExportadorPdf;
import co.edu.uniquindio.cinemauq.reporte.ExportadorTxt;
import co.edu.uniquindio.cinemauq.reporte.Reporte;
import co.edu.uniquindio.cinemauq.reporte.ServicioReportes;
import co.edu.uniquindio.cinemauq.reporte.TipoReporte;
import co.edu.uniquindio.cinemauq.servicio.Periodo;
import co.edu.uniquindio.cinemauq.servicio.ServicioEstadisticas;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/** Estadísticas por periodo, los 9 reportes y los dos formatos de exportación. */
class EstadisticasYReportesTest {

    private final Escenario e = new Escenario();
    private final ServicioEstadisticas estadisticas =
            new ServicioEstadisticas(e.compras::listar, e.funciones::listar, e.autenticacion::listarClientes);
    private final ServicioReportes reportes = new ServicioReportes(estadisticas, e.reloj);
    private final LocalDate hoy = Escenario.AHORA.toLocalDate();

    /** Dos compras hoy (una se cancela mañana) y la recarga inicial de 100.000. */
    private void operar() throws Exception {
        e.servicioCompras.pagar(e.servicioCompras.nuevaCompra(e.cliente, e.funcion)
                .agregarEntrada(e.silla("A1")).agregarEntrada(e.silla("A2")).agregarConfiteria(e.gaseosa, 2).build());
        Compra otra = e.servicioCompras.pagar(e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("B1")).build());
        e.hora = e.hora.plusDays(1);
        e.servicioCompras.cancelarCompra(e.cliente, otra);
    }

    @Test
    void losIndicadoresSeCalculanPorPeriodo() throws Exception {
        operar();
        Periodo soloHoy = Periodo.de(hoy, hoy);
        assertEquals(44_000, estadisticas.ventasBrutas(soloHoy));            // 2 entradas + 2 gaseosas + 1 entrada
        assertEquals(0, estadisticas.totalReembolsado(soloHoy));              // el reembolso fue mañana
        assertEquals(2, estadisticas.cantidadCompras(soloHoy));
        assertEquals(2, estadisticas.entradasVendidas(soloHoy));              // la cancelada no cuenta
        assertEquals(100_000, estadisticas.totalRecargas(soloHoy));
        Periodo dosDias = Periodo.de(hoy, hoy.plusDays(1));
        assertEquals(10_000, estadisticas.totalReembolsado(dosDias));
        assertEquals(34_000, estadisticas.ventasNetas(dosDias));
        assertEquals(1, estadisticas.cantidadCancelaciones(dosDias));
        assertEquals(14, estadisticas.indicadores(dosDias).size());
        assertEquals("Prueba", estadisticas.peliculaMasVista(dosDias));
        assertEquals(2, estadisticas.ventasPorDia(dosDias).size());          // un valor por día
    }

    @Test
    void elPeriodoValidaLasFechas() {
        assertThrows(DatosInvalidosException.class, () -> Periodo.de(hoy, hoy.minusDays(1)));
        assertThrows(DatosInvalidosException.class, () -> Periodo.de(null, hoy));
    }

    @Test
    void elTxtTieneTituloPeriodoTablaYTotales() throws Exception {
        operar();
        Reporte r = reportes.generar(TipoReporte.CANCELACIONES, Periodo.de(hoy, hoy.plusDays(1)));
        String txt = new ExportadorTxt().formatear(r);
        assertTrue(txt.startsWith("CINEMAUQ · CANCELACIONES Y REEMBOLSOS"));
        assertTrue(txt.contains("Periodo: "));
        assertTrue(txt.contains("Cancelación del cliente"));
        assertTrue(txt.contains("Total devuelto:"));
    }

    @Test
    void seGeneranLosNueveReportes() throws Exception {
        operar();
        Periodo p = Periodo.de(hoy, hoy.plusDays(3));                     // incluye la función de dentro de 3 días
        for (TipoReporte tipo : TipoReporte.values()) {
            Reporte r = reportes.generar(tipo, p);
            assertEquals(tipo.getTitulo(), r.getTitulo());
            assertFalse(r.getColumnas().isEmpty(), tipo + " sin columnas");
            assertFalse(r.getFilas().isEmpty(), tipo + " sin filas");
            assertFalse(r.getTotales().isEmpty(), tipo + " sin totales");
        }
        assertEquals(9, TipoReporte.values().length);
    }

    @Test
    void elPdfEsValidoYPaginaLaTabla() throws Exception {
        Reporte r = new Reporte("Prueba de páginas", Periodo.de(hoy, hoy), Escenario.AHORA)
                .columna("Número", true).columna("Descripción (con tildes: ñ, á)", false);
        for (int i = 1; i <= 80; i++) {
            r.fila(String.valueOf(i), "Fila número " + i);
        }
        r.total("Filas", "80");
        byte[] pdf = new ExportadorPdf().generar(r);
        String texto = new String(pdf, StandardCharsets.ISO_8859_1);
        assertTrue(texto.startsWith("%PDF-1.4"));
        assertTrue(texto.trim().endsWith("%%EOF"));
        assertTrue(texto.contains("/Count 3"));                              // 80 filas → 3 páginas
        assertTrue(texto.contains("(Página 3 de 3)"));
        // cada entrada de la tabla xref apunta al inicio de su objeto
        Matcher m = Pattern.compile("(\\d{10}) 00000 n").matcher(texto);
        int objeto = 1;
        while (m.find()) {
            int offset = Integer.parseInt(m.group(1));
            assertTrue(texto.startsWith(objeto + " 0 obj", offset), "objeto " + objeto);
            objeto++;
        }
        assertEquals(10, objeto - 1);                                         // 4 fijos + 2 por página
    }
}
