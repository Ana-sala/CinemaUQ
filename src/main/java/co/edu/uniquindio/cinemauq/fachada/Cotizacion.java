package co.edu.uniquindio.cinemauq.fachada;

import java.util.List;

/** Lo que la taquilla le muestra al cliente antes de pagar. */
public record Cotizacion(List<String> items, List<String> detallePrecio, double total, int puntosNecesarios,
                         int puntosDisponibles) {
}
