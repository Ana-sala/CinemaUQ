package co.edu.uniquindio.cinemauq.modelo;

/**
 * PATRÓN ESTRUCTURAL · COMPOSITE (Component).
 * Cualquier cosa que se vende en la confitería: un producto suelto (hoja) o un combo,
 * que puede contener productos y también otros combos. La compra, el precio y la
 * pantalla tratan a los dos igual, sin preguntar cuál es (LSP).
 */
public interface ItemConfiteria extends Identificable {

    String getNombre();

    double getPrecio();

    boolean isDisponible();

    /** Cuántos productos sueltos hay en total dentro de este ítem (1 para un producto). */
    int contarProductos();

    /** Texto con la estructura del ítem, con sangría por nivel (para ver el árbol). */
    String describir(String sangria);
}
