package co.edu.uniquindio.cinemauq.fabrica;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

/**
 * PATRÓN CREACIONAL · FACTORY METHOD (Creator).
 * emitir() es el algoritmo común: valida el asiento y asigna el id.
 * El paso que cambia, qué clase de Entrada se instancia, se delega al
 * método fábrica crearEntrada(), que implementa cada subclase.
 */
public abstract class FabricaEntrada {

    public final Entrada emitir(Funcion funcion, Asiento asiento) throws AsientoNoDisponibleException {
        if (!funcion.estaDisponible(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo() + " no está disponible para esta función.");
        }
        return crearEntrada(GeneradorId.siguiente("ENT"), funcion, asiento);
    }

    /** Método fábrica. */
    protected abstract Entrada crearEntrada(String id, Funcion funcion, Asiento asiento);
}
