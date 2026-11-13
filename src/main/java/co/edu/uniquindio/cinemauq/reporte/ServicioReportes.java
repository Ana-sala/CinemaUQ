package co.edu.uniquindio.cinemauq.reporte;

import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;
import co.edu.uniquindio.cinemauq.servicio.Periodo;
import co.edu.uniquindio.cinemauq.servicio.ServicioEstadisticas;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Arma los reportes (RF-025, RF-026). Solo arma el contenido: los números salen de
 * ServicioEstadisticas y el archivo lo escribe un ExportadorReporte (SRP).
 */
public class ServicioReportes {

    private final ServicioEstadisticas estadisticas;
    private final Reloj reloj;
    private final List<ExportadorReporte> exportadores = List.of(new ExportadorTxt(), new ExportadorPdf());

    public ServicioReportes(ServicioEstadisticas estadisticas, Reloj reloj) {
        this.estadisticas = estadisticas;
        this.reloj = reloj;
    }

    public List<ExportadorReporte> getExportadores() {
        return exportadores;
    }

    public Reporte generar(TipoReporte tipo, Periodo p) {
        Reporte r = new Reporte(tipo.getTitulo(), p, reloj.ahora());
        switch (tipo) {
            case RESUMEN -> resumen(r, p);
            case VENTAS_POR_DIA -> ventasPorDia(r, p);
            case ENTRADAS_POR_PELICULA -> entradasPorPelicula(r, p);
            case OCUPACION_POR_FUNCION -> ocupacionPorFuncion(r, p);
            case OCUPACION_POR_SALA -> ocupacionPorSala(r, p);
            case CONFITERIA -> confiteria(r, p);
            case RECARGAS -> recargas(r, p);
            case CANCELACIONES -> cancelaciones(r, p);
            case PUNTOS -> puntos(r, p);
        }
        return r;
    }

    private void resumen(Reporte r, Periodo p) {
        r.columna("Indicador", false).columna("Valor", true);
        estadisticas.indicadores(p).forEach(r::fila);
        r.total("Indicadores", String.valueOf(r.getFilas().size()));
    }

    private void ventasPorDia(Reporte r, Periodo p) {
        r.columna("Día", false).columna("Compras", true).columna("Entradas", true).columna("Valor cobrado", true);
        List<Compra> cobradas = estadisticas.comprasCobradas(p);
        for (LocalDate dia : p.dias()) {
            List<Compra> delDia = cobradas.stream().filter(c -> c.getFecha().toLocalDate().equals(dia)).toList();
            if (delDia.isEmpty()) {
                continue;
            }
            r.fila(Formato.fecha(dia), String.valueOf(delDia.size()),
                    String.valueOf(delDia.stream().mapToInt(c -> c.getEntradas().size()).sum()),
                    Formato.moneda(delDia.stream().mapToDouble(Compra::getValorPagado).sum()));
        }
        r.total("Ventas brutas", Formato.moneda(estadisticas.ventasBrutas(p)))
                .total("Reembolsado", Formato.moneda(estadisticas.totalReembolsado(p)))
                .total("Ventas netas", Formato.moneda(estadisticas.ventasNetas(p)));
    }

    private void entradasPorPelicula(Reporte r, Periodo p) {
        r.columna("Película", false).columna("Entradas", true).columna("Participación", true);
        Map<String, Long> mapa = estadisticas.entradasPorPelicula(p);
        long total = mapa.values().stream().mapToLong(Long::longValue).sum();
        mapa.forEach((peli, n) -> r.fila(peli, String.valueOf(n), porcentaje(100.0 * n / total)));
        r.total("Entradas vendidas", String.valueOf(total));
    }

    private void ocupacionPorFuncion(Reporte r, Periodo p) {
        r.columna("Fecha y hora", false).columna("Película", false).columna("Sala", false)
                .columna("Vendidas", true).columna("Capacidad", true).columna("Ocupación", true);
        List<Funcion> funciones = estadisticas.funcionesRealizadas(p);
        for (Funcion f : funciones) {
            int capacidad = f.getSala().getCapacidad();
            r.fila(Formato.fechaHora(f.getFechaHora()), f.getPelicula().getTitulo(), f.getSala().getNombre(),
                    String.valueOf(capacidad - f.getDisponibles()), String.valueOf(capacidad), porcentaje(f.getPorcentajeOcupacion()));
        }
        r.total("Funciones", String.valueOf(funciones.size()))
                .total("Ocupación promedio", porcentaje(estadisticas.ocupacionPromedio(p)));
    }

    private void ocupacionPorSala(Reporte r, Periodo p) {
        r.columna("Sala", false).columna("Funciones", true).columna("Ocupación promedio", true);
        Map<String, Long> funciones = estadisticas.funcionesPorSala(p);
        estadisticas.ocupacionPorSala(p).forEach((sala, o) -> r.fila(sala, String.valueOf(funciones.get(sala)), porcentaje(o)));
        r.total("Ocupación promedio general", porcentaje(estadisticas.ocupacionPromedio(p)));
    }

    private void confiteria(Reporte r, Periodo p) {
        r.columna("Producto o combo", false).columna("Unidades", true).columna("Valor", true);
        Map<String, Double> valor = estadisticas.confiteriaValorPorProducto(p);
        Map<String, Long> unidades = estadisticas.confiteriaPorProducto(p);
        unidades.forEach((item, n) -> r.fila(item, String.valueOf(n), Formato.moneda(valor.get(item))));
        r.total("Unidades vendidas", String.valueOf(unidades.values().stream().mapToLong(Long::longValue).sum()))
                .total("Valor de lista", Formato.moneda(valor.values().stream().mapToDouble(Double::doubleValue).sum()));
    }

    private void recargas(Reporte r, Periodo p) {
        r.columna("Fecha", false).columna("Cliente", false).columna("Tarjeta", false).columna("Valor", true);
        List<Map.Entry<Cliente, Movimiento>> lista = estadisticas.recargas(p);
        for (Map.Entry<Cliente, Movimiento> e : lista) {
            r.fila(Formato.fechaHora(e.getValue().getFecha()), e.getKey().getNombre(), e.getKey().getTarjeta().getNumero(),
                    Formato.moneda(e.getValue().getValor()));
        }
        r.total("Recargas", String.valueOf(lista.size())).total("Valor recargado", Formato.moneda(estadisticas.totalRecargas(p)));
    }

    private void cancelaciones(Reporte r, Periodo p) {
        r.columna("Fecha", false).columna("Compra", false).columna("Cliente", false).columna("Motivo", false)
                .columna("%", true).columna("Devuelto", true);
        List<Compra> lista = estadisticas.comprasReembolsadas(p);
        for (Compra c : lista) {
            Reembolso re = c.getReembolso();
            r.fila(Formato.fechaHora(re.getFecha()), c.getId(), c.getCliente().getNombre(),
                    re.getMotivo() == MotivoReembolso.FUNCION_CANCELADA ? "Función cancelada" : "Cancelación del cliente",
                    re.getPorcentaje() + " %", Formato.moneda(re.getValor()));
        }
        r.total("Cancelaciones", String.valueOf(lista.size()))
                .total("Total devuelto", Formato.moneda(estadisticas.totalReembolsado(p)));
    }

    private void puntos(Reporte r, Periodo p) {
        r.columna("Cliente", false).columna("Correo", false).columna("Ganados", true).columna("Redimidos", true);
        estadisticas.puntosPorCliente(p).forEach((c, v) -> r.fila(c.getNombre(), c.getCorreo(), String.valueOf(v[0]), String.valueOf(v[1])));
        r.total("Puntos otorgados", String.valueOf(estadisticas.puntosOtorgados(p)))
                .total("Puntos redimidos", String.valueOf(estadisticas.puntosRedimidos(p)))
                .total("Equivalencia", "1 punto por cada $1.000 pagados · 1 punto = $100 al redimir");
    }

    private static String porcentaje(double v) {
        return String.format("%.1f %%", v);
    }
}
