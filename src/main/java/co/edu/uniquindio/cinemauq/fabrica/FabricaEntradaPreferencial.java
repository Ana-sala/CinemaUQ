package co.edu.uniquindio.cinemauq.fabrica;

import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.EntradaPreferencial;
import co.edu.uniquindio.cinemauq.modelo.Funcion;

/** Concrete Creator: fabrica entradas preferenciales. */
public class FabricaEntradaPreferencial extends FabricaEntrada {
    @Override
    protected Entrada crearEntrada(String id, Funcion funcion, Asiento asiento) {
        return new EntradaPreferencial(id, funcion, asiento);
    }
}
