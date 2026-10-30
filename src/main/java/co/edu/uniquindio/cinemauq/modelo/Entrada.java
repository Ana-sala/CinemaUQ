package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.util.Formato;

/**
 * Entrada para un asiento de una función. Es abstracta: cada tipo de asiento
 * define su precio. Las crea una FabricaEntrada (patrón Factory Method).
 */
public abstract class Entrada implements Identificable {

    private final String id;
    private final Funcion funcion;
    private final Asiento asiento;
    private boolean pagadaConPuntos;

    protected Entrada(String id, Funcion funcion, Asiento asiento) {
        this.id = id;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    /** Cada subclase decide su precio a partir del precio base de la función. */
    public abstract double getPrecio();

    public abstract TipoAsiento getTipo();

    @Override
    public String getId() {
        return id;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public boolean isPagadaConPuntos() {
        return pagadaConPuntos;
    }

    public void setPagadaConPuntos(boolean pagadaConPuntos) {
        this.pagadaConPuntos = pagadaConPuntos;
    }

    @Override
    public String toString() {
        return "Entrada " + getTipo().name().toLowerCase() + " " + asiento.getCodigo() + " · "
                + (pagadaConPuntos ? "con puntos" : Formato.moneda(getPrecio()));
    }
}
