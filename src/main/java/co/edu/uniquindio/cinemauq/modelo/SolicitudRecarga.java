package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.time.LocalDateTime;

/** Pedido de recarga que hace un cliente. Solo un administrador puede aprobarlo (RN-03). */
public class SolicitudRecarga implements Identificable {

    private final String id;
    private final Cliente cliente;
    private final double valor;
    private final LocalDateTime fecha;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    public SolicitudRecarga(Cliente cliente, double valor, LocalDateTime fecha) {
        this.id = GeneradorId.siguiente("SOL");
        this.cliente = cliente;
        this.valor = valor;
        this.fecha = fecha;
    }

    public void aprobar() {
        estado = EstadoSolicitud.APROBADA;
    }

    public void rechazar() {
        estado = EstadoSolicitud.RECHAZADA;
    }

    @Override
    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return id + " · " + cliente.getNombre() + " · " + Formato.moneda(valor) + " · " + Formato.fechaHora(fecha)
                + " · " + estado;
    }
}
