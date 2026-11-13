package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.estado.EstadoDeCompra;
import co.edu.uniquindio.cinemauq.estado.EstadoPendiente;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
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
 * PATRÓN STATE · contexto: delega en su EstadoDeCompra qué operaciones se permiten.
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
    private EstadoDeCompra estado = EstadoPendiente.INSTANCIA;   // State
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

    /** Pregunta al estado si se puede pagar, sin cambiar nada (se usa antes de cobrar). */
    public void verificarPago() throws CinemaUQException {
        estado.pagar(this);
    }

    /** Pregunta al estado si se puede cancelar, sin cambiar nada (se usa antes de reembolsar). */
    public void verificarCancelacion() throws CinemaUQException {
        estado.cancelar(this);
    }

    /** Congela lo que realmente se cobró: los reembolsos se calculan sobre este valor. */
    public void marcarPagada(LocalDateTime fechaPago, double valorPagado, int puntosGanados) throws CinemaUQException {
        this.estado = estado.pagar(this);          // el estado decide si se puede y cuál sigue
        this.fecha = fechaPago;
        this.valorPagado = valorPagado;
        this.puntosGanados = puntosGanados;
    }

    /** Un reembolso no puede ejecutarse dos veces sobre la misma operación (RN-04): lo impide el estado. */
    public void registrarReembolso(Reembolso reembolso) throws CinemaUQException {
        this.estado = estado.cancelar(this);
        this.reembolso = reembolso;
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

    /** Estado para mostrar (enum). El comportamiento lo da getEstadoActual(). */
    public EstadoCompra getEstado() {
        return estado.tipo();
    }

    public EstadoDeCompra getEstadoActual() {
        return estado;
    }

    public Reembolso getReembolso() {
        return reembolso;
    }

    @Override
    public String toString() {
        String sillas = String.join(", ", entradas.stream().map(e -> e.getAsiento().getCodigo()).toList());
        String texto = id + " · " + funcion.getPelicula().getTitulo() + " · " + Formato.fechaHora(funcion.getFechaHora())
                + " · sillas " + (sillas.isEmpty() ? "-" : sillas) + " · " + Formato.moneda(getEstado() == EstadoCompra.PENDIENTE ? getValorAPagar() : valorPagado);
        if (puntosRedimidos > 0) {
            texto += " + " + puntosRedimidos + " pts";
        }
        return texto + " · " + getEstado() + (reembolso != null ? " (reembolso " + reembolso.getPorcentaje() + "%)" : "");
    }
}
