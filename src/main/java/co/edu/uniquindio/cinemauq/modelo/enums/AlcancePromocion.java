package co.edu.uniquindio.cinemauq.modelo.enums;

/** Sobre qué parte de la compra se aplica una promoción. */
public enum AlcancePromocion {
    ENTRADAS("Entradas"), CONFITERIA("Confitería");

    private final String etiqueta;

    AlcancePromocion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
