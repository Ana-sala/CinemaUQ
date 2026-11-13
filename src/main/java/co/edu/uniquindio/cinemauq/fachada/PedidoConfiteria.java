package co.edu.uniquindio.cinemauq.fachada;

import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;

/** Un producto o combo que el cliente quiere, con su cantidad y si lo paga con puntos. */
public record PedidoConfiteria(ItemConfiteria item, int cantidad, boolean conPuntos) {
}
