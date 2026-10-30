package co.edu.uniquindio.cinemauq.servicio;

/**
 * Regla para calcular cuántos puntos gana una compra (OCP + DIP).
 * Cambiar la regla (por ejemplo, doble puntos los martes) es crear otra
 * implementación, no modificar ServicioCompras.
 */
@FunctionalInterface
public interface ReglaAcumulacionPuntos {
    int calcularPuntos(double valorPagadoEnDinero);
}
