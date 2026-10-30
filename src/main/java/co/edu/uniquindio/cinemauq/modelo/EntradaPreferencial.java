package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

/** Entrada de asiento preferencial: precio base + 30 % (RN-11). */
public class EntradaPreferencial extends Entrada {

    public static final double RECARGO = 0.30;

    public EntradaPreferencial(String id, Funcion funcion, Asiento asiento) {
        super(id, funcion, asiento);
    }

    @Override
    public double getPrecio() {
        return Math.round(getFuncion().getPrecioBase() * (1 + RECARGO));
    }

    @Override
    public TipoAsiento getTipo() {
        return TipoAsiento.PREFERENCIAL;
    }
}
