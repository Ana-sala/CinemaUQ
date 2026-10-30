package co.edu.uniquindio.cinemauq.modelo;

/**
 * Cualquier cosa que se vende en la confitería: un producto suelto o un combo.
 * La compra trabaja con esta interfaz y no le importa cuál de los dos es (LSP).
 */
public interface ItemConfiteria extends Identificable {
    String getNombre();

    double getPrecio();

    boolean isDisponible();
}
