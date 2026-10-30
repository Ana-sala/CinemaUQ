package co.edu.uniquindio.cinemauq.modelo.enums;

/** Formato de proyeccion de una sala. */
public enum TipoSala {
    DOS_D("2D"), TRES_D("3D"), VIP("VIP");

    private final String etiqueta;

    TipoSala(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
