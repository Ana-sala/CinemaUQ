package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Estadísticas básicas (RF-024). Lee compras y funciones, no las modifica.
 * Los reportes exportables y los gráficos llegan en las siguientes entregas.
 */
public class ServicioEstadisticas {

    private final Supplier<List<Compra>> compras;
    private final Supplier<List<Funcion>> funciones;

    public ServicioEstadisticas(Supplier<List<Compra>> compras, Supplier<List<Funcion>> funciones) {
        this.compras = compras;
        this.funciones = funciones;
    }

    public double ventasBrutas() {
        return compras.get().stream().filter(c -> c.getEstado() != EstadoCompra.PENDIENTE)
                .mapToDouble(Compra::getValorPagado).sum();
    }

    public double totalReembolsado() {
        return compras.get().stream().filter(Compra::isReembolsada).mapToDouble(c -> c.getReembolso().getValor()).sum();
    }

    public long cantidadReembolsos() {
        return compras.get().stream().filter(Compra::isReembolsada).count();
    }

    public Map<String, Long> entradasPorPelicula() {
        Map<String, Long> conteo = new TreeMap<>();
        for (Compra compra : pagadas()) {
            conteo.merge(compra.getFuncion().getPelicula().getTitulo(), (long) compra.getEntradas().size(), Long::sum);
        }
        return ordenarDesc(conteo);
    }

    public Map<String, Long> productosVendidos() {
        Map<String, Long> conteo = new TreeMap<>();
        for (Compra compra : pagadas()) {
            for (LineaConfiteria linea : compra.getLineas()) {
                conteo.merge(linea.getItem().getNombre(), (long) linea.getCantidad(), Long::sum);
            }
        }
        return ordenarDesc(conteo);
    }

    /** Ocupación promedio de cada sala sobre sus funciones no canceladas, en %. */
    public Map<String, Double> ocupacionPorSala() {
        Map<String, double[]> acumulado = new TreeMap<>();
        for (Funcion f : funciones.get()) {
            if (f.getEstado() == EstadoFuncion.PROGRAMADA) {
                double[] a = acumulado.computeIfAbsent(f.getSala().getNombre(), k -> new double[2]);
                a[0] += f.getPorcentajeOcupacion();
                a[1]++;
            }
        }
        Map<String, Double> resultado = new LinkedHashMap<>();
        acumulado.forEach((sala, a) -> resultado.put(sala, a[0] / a[1]));
        return resultado;
    }

    public long entradasVendidas() {
        return pagadas().stream().mapToLong(c -> c.getEntradas().size()).sum();
    }

    private List<Compra> pagadas() {
        return compras.get().stream().filter(c -> c.getEstado() == EstadoCompra.PAGADA).toList();
    }

    private Map<String, Long> ordenarDesc(Map<String, Long> mapa) {
        Map<String, Long> ordenado = new LinkedHashMap<>();
        mapa.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> ordenado.put(e.getKey(), e.getValue()));
        return ordenado;
    }

    /** Resumen en texto para la pestaña de estadísticas del administrador. */
    public String resumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ventas brutas: ").append(Formato.moneda(ventasBrutas())).append('\n');
        sb.append("Reembolsos: ").append(cantidadReembolsos()).append(" por ")
                .append(Formato.moneda(totalReembolsado())).append('\n');
        sb.append("Ventas netas: ").append(Formato.moneda(ventasBrutas() - totalReembolsado())).append('\n');
        sb.append("Entradas vendidas (compras vigentes): ").append(entradasVendidas()).append("\n\n");
        sb.append("Películas con más entradas vendidas:\n");
        entradasPorPelicula().forEach((p, n) -> sb.append("  ").append(p).append(": ").append(n).append('\n'));
        sb.append("\nProductos y combos más vendidos:\n");
        productosVendidos().forEach((p, n) -> sb.append("  ").append(p).append(": ").append(n).append('\n'));
        sb.append("\nOcupación promedio por sala:\n");
        ocupacionPorSala().forEach((s, o) -> sb.append("  ").append(s).append(": ")
                .append(String.format("%.1f %%", o)).append('\n'));
        return sb.toString();
    }
}
