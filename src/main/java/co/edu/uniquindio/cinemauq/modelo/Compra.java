package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.excepciones.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Compra de un cliente para una función: entradas + confitería.
 * Se arma paso a paso con CompraBuilder (patrón Builder) y nace PENDIENTE;
 * ServicioCompras la cobra (PAGADA) o la cancela (CANCELADA).
 */
public class Compra implements Identificable {

    private final String id;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Entrada> entradas;
    private final List<LineaConfiteria> lineas;
    private final int puntosRedimidos;
    private final double valorAPagar;
    private final List<String> detallePrecio;
    private LocalDateTime fecha;
    private EstadoCompra estado = EstadoCompra.PENDIENTE;
    private int puntosGanados;
    private double valorPagado;
    private Reembolso reembolso;

    public Compra(String id, Cliente cliente, Funcion funcion, List<Entrada> entradas,
                  List<LineaConfiteria> lineas, int puntosRedimidos, double valorAPagar, List<String> detallePrecio) {
        this.id = id;
        this.cliente = cliente;
        this.funcion = funcion;
        this.entradas = new ArrayList<>(entradas);
        this.lineas = new ArrayList<>(lineas);
        this.puntosRedimidos = puntosRedimidos;
        this.valorAPagar = valorAPagar;
        this.detallePrecio = List.copyOf(detallePrecio);
    }

    /** Valor de todo lo que se lleva el cliente, sin importar cómo lo paga. */
    public double getTotalBruto() {
        return entradas.stream().mapToDouble(Entrada::getPrecio).sum()
                + lineas.stream().mapToDouble(LineaConfiteria::getSubtotal).sum();
    }

    /**
     * Lo que se cobra en dinero: sin los ítems pagados con puntos y ya con las promociones
     * aplicadas (lo calcula la cadena de decoradores de precio al construir la compra).
     */
    public double getValorAPagar() {
        return valorAPagar;
    }

    /** Subtotales y descuentos aplicados, tal como se mostraron al cliente. */
    public List<String> getDetallePrecio() {
        return detallePrecio;
    }

    /** Congela lo que realmente se cobró: los reembolsos se calculan sobre este valor. */
    public void marcarPagada(LocalDateTime fechaPago, double valorPagado, int puntosGanados) {
        this.fecha = fechaPago;
        this.valorPagado = valorPagado;
        this.puntosGanados = puntosGanados;
        this.estado = EstadoCompra.PAGADA;
    }

    /** Un reembolso no puede ejecutarse dos veces sobre la misma operación (RN-04). */
    public void registrarReembolso(Reembolso reembolso) throws ReembolsoDuplicadoException {
        if (this.reembolso != null) {
            throw new ReembolsoDuplicadoException("La compra " + id + " ya fue reembolsada.");
        }
        this.reembolso = reembolso;
        this.estado = EstadoCompra.CANCELADA;
    }

    public boolean isReembolsada() {
        return reembolso != null;
    }

    @Override
    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public List<LineaConfiteria> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public int getPuntosRedimidos() {
        return puntosRedimidos;
    }

    public int getPuntosGanados() {
        return puntosGanados;
    }

    public double getValorPagado() {
        return valorPagado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public Reembolso getReembolso() {
        return reembolso;
    }

    @Override
    public String toString() {
        String sillas = String.join(", ", entradas.stream().map(e -> e.getAsiento().getCodigo()).toList());
        String texto = id + " · " + funcion.getPelicula().getTitulo() + " · " + Formato.fechaHora(funcion.getFechaHora())
                + " · sillas " + (sillas.isEmpty() ? "-" : sillas) + " · " + Formato.moneda(estado == EstadoCompra.PENDIENTE ? getValorAPagar() : valorPagado);
        if (puntosRedimidos > 0) {
            texto += " + " + puntosRedimidos + " pts";
        }
        return texto + " · " + estado + (reembolso != null ? " (reembolso " + reembolso.getPorcentaje() + "%)" : "");
    }
}
