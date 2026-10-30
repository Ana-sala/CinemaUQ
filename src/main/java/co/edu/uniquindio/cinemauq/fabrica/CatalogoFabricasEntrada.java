package co.edu.uniquindio.cinemauq.fabrica;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

import java.util.EnumMap;
import java.util.Map;

/**
 * Elige la fábrica según el tipo de asiento. Para un tipo nuevo (por ejemplo VIP)
 * se crea una clase de entrada y su fábrica y se registra aquí: el resto del
 * sistema no cambia (OCP).
 */
public class CatalogoFabricasEntrada {

    private final Map<TipoAsiento, FabricaEntrada> fabricas = new EnumMap<>(TipoAsiento.class);

    public CatalogoFabricasEntrada() {
        registrar(TipoAsiento.GENERAL, new FabricaEntradaGeneral());
        registrar(TipoAsiento.PREFERENCIAL, new FabricaEntradaPreferencial());
    }

    public final void registrar(TipoAsiento tipo, FabricaEntrada fabrica) {
        fabricas.put(tipo, fabrica);
    }

    public FabricaEntrada fabricaPara(TipoAsiento tipo) throws DatosInvalidosException {
        FabricaEntrada fabrica = fabricas.get(tipo);
        if (fabrica == null) {
            throw new DatosInvalidosException("No hay fábrica registrada para asientos " + tipo);
        }
        return fabrica;
    }

    public Entrada emitir(Funcion funcion, Asiento asiento) throws DatosInvalidosException, AsientoNoDisponibleException {
        return fabricaPara(asiento.getTipo()).emitir(funcion, asiento);
    }
}
