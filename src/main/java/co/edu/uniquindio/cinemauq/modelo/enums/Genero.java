package co.edu.uniquindio.cinemauq.modelo.enums;

/** Género de una película. */
public enum Genero {
    ACCION("Acción"), ANIMACION("Animación"), COMEDIA("Comedia"), DRAMA("Drama"), TERROR("Terror"),
    CIENCIA_FICCION("Ciencia ficción"), DOCUMENTAL("Documental"), ROMANCE("Romance");

    private final String etiqueta;

    Genero(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
