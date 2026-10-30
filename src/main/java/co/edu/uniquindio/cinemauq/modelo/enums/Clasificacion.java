package co.edu.uniquindio.cinemauq.modelo.enums;

/** Clasificacion por edad de una pelicula. */
public enum Clasificacion {
    TODO_PUBLICO("Todo publico"), MAYORES_7("+7"), MAYORES_12("+12"), MAYORES_15("+15"), MAYORES_18("+18");

    private final String etiqueta;

    Clasificacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
