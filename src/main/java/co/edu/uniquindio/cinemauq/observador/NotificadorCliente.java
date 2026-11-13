package co.edu.uniquindio.cinemauq.observador;

import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OBSERVER · observador concreto: guarda un aviso para el cliente cada vez que una de sus
 * compras se paga o se cancela. El cliente los ve en la pestaña «Notificaciones».
 */
public class NotificadorCliente implements ObservadorCompras {

    private final Map<String, List<Notificacion>> porCliente = new HashMap<>();

    @Override
    public void compraPagada(Compra compra) {
        String sillas = String.join(", ", compra.getEntradas().stream().map(e -> e.getAsiento().getCodigo()).toList());
        agregar(compra, compra.getFecha(), "Compra " + compra.getId() + " confirmada: " + compra.getFuncion().getPelicula().getTitulo()
                + ", " + Formato.fechaHora(compra.getFuncion().getFechaHora())
                + (sillas.isEmpty() ? "" : ", sillas " + sillas) + ". Pagó " + Formato.moneda(compra.getValorPagado())
                + (compra.getPuntosGanados() > 0 ? " y ganó " + compra.getPuntosGanados() + " puntos." : "."));
    }

    @Override
    public void compraCancelada(Compra compra, Reembolso reembolso) {
        String quien = switch (reembolso.getMotivo()) {
            case FUNCION_CANCELADA -> "El cine canceló la función de " + compra.getFuncion().getPelicula().getTitulo();
            case CANCELACION_CLIENTE -> "Usted canceló la compra " + compra.getId();
        };
        agregar(compra, reembolso.getFecha(), quien + ". Se devolvieron " + Formato.moneda(reembolso.getValor())
                + " (" + reembolso.getPorcentaje() + " %) a su tarjeta"
                + (reembolso.getPuntosDevueltos() > 0 ? " y " + reembolso.getPuntosDevueltos() + " puntos." : "."));
    }

    private void agregar(Compra compra, java.time.LocalDateTime fecha, String mensaje) {
        String correo = compra.getCliente().getCorreo();
        porCliente.computeIfAbsent(correo, k -> new ArrayList<>()).add(new Notificacion(fecha, correo, mensaje));
    }

    /** Avisos del cliente, del más reciente al más antiguo. */
    public List<Notificacion> notificacionesDe(Cliente cliente) {
        return porCliente.getOrDefault(cliente.getCorreo(), List.of()).stream()
                .sorted(Comparator.comparing(Notificacion::fecha).reversed()).toList();
    }
}
