package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

/** Entrada de asiento general: paga el precio base de la función (RN-11). */
public class EntradaGeneral extends Entrada {

    public EntradaGeneral(String id, Funcion funcion, Asiento asiento) {
        super(id, funcion, asiento);
    }

    @Override
    public double getPrecio() {
        return getFuncion().getPrecioBase();
    }

    @Override
    public TipoAsiento getTipo() {
        return TipoAsiento.GENERAL;
    }
}
