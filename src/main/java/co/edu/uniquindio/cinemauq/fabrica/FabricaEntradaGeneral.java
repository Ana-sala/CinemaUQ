package co.edu.uniquindio.cinemauq.fabrica;

import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.EntradaGeneral;
import co.edu.uniquindio.cinemauq.modelo.Funcion;

/** Concrete Creator: fabrica entradas generales. */
public class FabricaEntradaGeneral extends FabricaEntrada {
    @Override
    protected Entrada crearEntrada(String id, Funcion funcion, Asiento asiento) {
        return new EntradaGeneral(id, funcion, asiento);
    }
}
