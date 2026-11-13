package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Estadísticas de operación por periodo (RF-024). Solo lee compras, funciones y tarjetas;
 * no modifica nada (SRP). Los reportes (ServicioReportes) y los gráficos de la pantalla
 * salen de aquí, así que las cifras coinciden en todas partes.
 *
 * Criterios: una compra cuenta en el día en que se pagó. Las ventas brutas incluyen las
 * compras que después se cancelaron; lo devuelto se resta en «reembolsado» el día del
 * reembolso. Entradas, puntos y películas solo cuentan compras vigentes (PAGADA).
 */
public class ServicioEstadisticas {

    private final Supplier<List<Compra>> compras;
    private final Supplier<List<Funcion>> funciones;
    private final Supplier<List<Cliente>> clientes;

    public ServicioEstadisticas(Supplier<List<Compra>> compras, Supplier<List<Funcion>> funciones,
                                Supplier<List<Cliente>> clientes) {
        this.compras = compras;
        this.funciones = funciones;
        this.clientes = clientes;
    }

    // ===================== Datos base del periodo =====================

    /** Compras cobradas en el periodo (vigentes o canceladas después). */
    public List<Compra> comprasCobradas(Periodo p) {
        return compras.get().stream().filter(c -> c.getEstado() != EstadoCompra.PENDIENTE && p.contiene(c.getFecha()))
                .sorted(Comparator.comparing(Compra::getFecha)).toList();
    }

    /** Compras pagadas en el periodo que siguen vigentes. */
    public List<Compra> comprasVigentes(Periodo p) {
        return comprasCobradas(p).stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).toList();
    }

    /** Compras reembolsadas cuyo reembolso ocurrió en el periodo. */
    public List<Compra> comprasReembolsadas(Periodo p) {
        return compras.get().stream().filter(c -> c.isReembolsada() && p.contiene(c.getReembolso().getFecha()))
                .sorted(Comparator.comparing((Compra c) -> c.getReembolso().getFecha())).toList();
    }

    /** Funciones del periodo que no fueron canceladas. */
    public List<Funcion> funcionesRealizadas(Periodo p) {
        return funciones.get().stream().filter(f -> f.getEstado() == EstadoFuncion.PROGRAMADA && p.contiene(f.getFechaHora()))
                .sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
    }

    /** Recargas de saldo del periodo, con su cliente. */
    public List<Map.Entry<Cliente, Movimiento>> recargas(Periodo p) {
        return clientes.get().stream()
                .flatMap(c -> c.getTarjeta().getMovimientos().stream()
                        .filter(m -> m.getTipo() == TipoMovimiento.RECARGA && p.contiene(m.getFecha()))
                        .map(m -> Map.entry(c, m)))
                .sorted(Comparator.comparing(e -> e.getValue().getFecha())).toList();
    }

    // ===================== Los 14 indicadores =====================

    public double ventasBrutas(Periodo p) {
        return comprasCobradas(p).stream().mapToDouble(Compra::getValorPagado).sum();
    }

    public double totalReembolsado(Periodo p) {
        return comprasReembolsadas(p).stream().mapToDouble(c -> c.getReembolso().getValor()).sum();
    }

    public double ventasNetas(Periodo p) {
        return ventasBrutas(p) - totalReembolsado(p);
    }

    public long cantidadCompras(Periodo p) {
        return comprasCobradas(p).size();
    }

    public double ticketPromedio(Periodo p) {
        long n = cantidadCompras(p);
        return n == 0 ? 0 : ventasBrutas(p) / n;
    }

    public long entradasVendidas(Periodo p) {
        return comprasVigentes(p).stream().mapToLong(c -> c.getEntradas().size()).sum();
    }

    public long cantidadFunciones(Periodo p) {
        return funcionesRealizadas(p).size();
    }

    public double ocupacionPromedio(Periodo p) {
        return funcionesRealizadas(p).stream().mapToDouble(Funcion::getPorcentajeOcupacion).average().orElse(0);
    }

    public long cantidadCancelaciones(Periodo p) {
        return comprasReembolsadas(p).size();
    }

    public double totalRecargas(Periodo p) {
        return recargas(p).stream().mapToDouble(e -> e.getValue().getValor()).sum();
    }

    public long puntosOtorgados(Periodo p) {
        return comprasVigentes(p).stream().mapToLong(Compra::getPuntosGanados).sum();
    }

    public long puntosRedimidos(Periodo p) {
        return comprasVigentes(p).stream().mapToLong(Compra::getPuntosRedimidos).sum();
    }

    public String peliculaMasVista(Periodo p) {
        return primero(entradasPorPelicula(p));
    }

    public String productoMasVendido(Periodo p) {
        return primero(confiteriaPorProducto(p));
    }

    /** Los 14 indicadores con su nombre y su valor ya formateado, en orden. */
    public Map<String, String> indicadores(Periodo p) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Ventas brutas", Formato.moneda(ventasBrutas(p)));
        m.put("Reembolsado", Formato.moneda(totalReembolsado(p)));
        m.put("Ventas netas", Formato.moneda(ventasNetas(p)));
        m.put("Compras", String.valueOf(cantidadCompras(p)));
        m.put("Ticket promedio", Formato.moneda(Math.round(ticketPromedio(p))));
        m.put("Entradas vendidas", String.valueOf(entradasVendidas(p)));
        m.put("Funciones", String.valueOf(cantidadFunciones(p)));
        m.put("Ocupación promedio", String.format("%.1f %%", ocupacionPromedio(p)));
        m.put("Cancelaciones", String.valueOf(cantidadCancelaciones(p)));
        m.put("Recargas", Formato.moneda(totalRecargas(p)));
        m.put("Puntos otorgados", String.valueOf(puntosOtorgados(p)));
        m.put("Puntos redimidos", String.valueOf(puntosRedimidos(p)));
        m.put("Película más vista", peliculaMasVista(p));
        m.put("Producto más vendido", productoMasVendido(p));
        return m;
    }

    // ===================== Series para gráficos y reportes =====================

    /** Valor cobrado por día (todos los días del periodo, aunque sea 0). */
    public Map<LocalDate, Double> ventasPorDia(Periodo p) {
        Map<LocalDate, Double> serie = diasEnCero(p);
        comprasCobradas(p).forEach(c -> serie.merge(c.getFecha().toLocalDate(), c.getValorPagado(), Double::sum));
        return serie;
    }

    public Map<LocalDate, Double> recargasPorDia(Periodo p) {
        Map<LocalDate, Double> serie = diasEnCero(p);
        recargas(p).forEach(e -> serie.merge(e.getValue().getFecha().toLocalDate(), e.getValue().getValor(), Double::sum));
        return serie;
    }

    public Map<String, Long> entradasPorPelicula(Periodo p) {
        Map<String, Long> conteo = new TreeMap<>();
        comprasVigentes(p).forEach(c -> conteo.merge(c.getFuncion().getPelicula().getTitulo(), (long) c.getEntradas().size(), Long::sum));
        return ordenarDesc(conteo);
    }

    /** Unidades vendidas de cada producto o combo. */
    public Map<String, Long> confiteriaPorProducto(Periodo p) {
        Map<String, Long> conteo = new TreeMap<>();
        for (Compra c : comprasVigentes(p)) {
            for (LineaConfiteria l : c.getLineas()) {
                conteo.merge(l.getItem().getNombre(), (long) l.getCantidad(), Long::sum);
            }
        }
        return ordenarDesc(conteo);
    }

    /** Valor de lista de lo vendido de cada producto o combo. */
    public Map<String, Double> confiteriaValorPorProducto(Periodo p) {
        Map<String, Double> valor = new TreeMap<>();
        for (Compra c : comprasVigentes(p)) {
            for (LineaConfiteria l : c.getLineas()) {
                valor.merge(l.getItem().getNombre(), l.getSubtotal(), Double::sum);
            }
        }
        return valor;
    }

    /** Ocupación promedio (%) de cada sala en las funciones del periodo. */
    public Map<String, Double> ocupacionPorSala(Periodo p) {
        Map<String, double[]> acumulado = new TreeMap<>();
        for (Funcion f : funcionesRealizadas(p)) {
            double[] a = acumulado.computeIfAbsent(f.getSala().getNombre(), k -> new double[2]);
            a[0] += f.getPorcentajeOcupacion();
            a[1]++;
        }
        Map<String, Double> resultado = new LinkedHashMap<>();
        acumulado.forEach((sala, a) -> resultado.put(sala, a[0] / a[1]));
        return resultado;
    }

    public Map<String, Long> funcionesPorSala(Periodo p) {
        Map<String, Long> conteo = new TreeMap<>();
        funcionesRealizadas(p).forEach(f -> conteo.merge(f.getSala().getNombre(), 1L, Long::sum));
        return conteo;
    }

    /** Puntos ganados [0] y redimidos [1] por cliente en el periodo. */
    public Map<Cliente, long[]> puntosPorCliente(Periodo p) {
        Map<Cliente, long[]> mapa = new TreeMap<>(Comparator.comparing(Cliente::getNombre));
        for (Compra c : comprasVigentes(p)) {
            long[] v = mapa.computeIfAbsent(c.getCliente(), k -> new long[2]);
            v[0] += c.getPuntosGanados();
            v[1] += c.getPuntosRedimidos();
        }
        return mapa;
    }

    public List<Reembolso> reembolsos(Periodo p) {
        return comprasReembolsadas(p).stream().map(Compra::getReembolso).toList();
    }

    // ===================== Compatibilidad: todo el historial =====================

    public double ventasBrutas() {
        return ventasBrutas(Periodo.todo());
    }

    public double totalReembolsado() {
        return totalReembolsado(Periodo.todo());
    }

    public long cantidadReembolsos() {
        return cantidadCancelaciones(Periodo.todo());
    }

    public long entradasVendidas() {
        return entradasVendidas(Periodo.todo());
    }

    public Map<String, Long> entradasPorPelicula() {
        return entradasPorPelicula(Periodo.todo());
    }

    public Map<String, Long> productosVendidos() {
        return confiteriaPorProducto(Periodo.todo());
    }

    public Map<String, Double> ocupacionPorSala() {
        return ocupacionPorSala(Periodo.todo());
    }

    /** Resumen en texto de un periodo. */
    public String resumen(Periodo p) {
        StringBuilder sb = new StringBuilder("Periodo: " + p + "\n\n");
        indicadores(p).forEach((k, v) -> sb.append(k).append(": ").append(v).append('\n'));
        return sb.toString();
    }

    public String resumen() {
        return resumen(Periodo.todo());
    }

    // ===================== Ayudas =====================

    private Map<LocalDate, Double> diasEnCero(Periodo p) {
        Map<LocalDate, Double> serie = new LinkedHashMap<>();
        p.dias().forEach(d -> serie.put(d, 0.0));
        return serie;
    }

    private static String primero(Map<String, Long> mapa) {
        return mapa.isEmpty() ? "-" : mapa.keySet().iterator().next();
    }

    private static Map<String, Long> ordenarDesc(Map<String, Long> mapa) {
        Map<String, Long> ordenado = new LinkedHashMap<>();
        mapa.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> ordenado.put(e.getKey(), e.getValue()));
        return ordenado;
    }
}
