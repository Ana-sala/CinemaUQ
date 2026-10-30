package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Sala del cine. Sus asientos se crean con la sala y no existen sin ella (composición).
 * Las últimas filas pueden ser preferenciales.
 */
public class Sala implements Identificable {

    private final String id;
    private final String nombre;
    private final TipoSala tipo;
    private final int filas;
    private final int asientosPorFila;
    private final List<Asiento> asientos = new ArrayList<>();

    public Sala(String id, String nombre, TipoSala tipo, int filas, int asientosPorFila, int filasPreferenciales) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.filas = filas;
        this.asientosPorFila = asientosPorFila;
        for (int f = 0; f < filas; f++) {
            char letra = (char) ('A' + f);
            TipoAsiento tipoAsiento = f >= filas - filasPreferenciales ? TipoAsiento.PREFERENCIAL : TipoAsiento.GENERAL;
            for (int n = 1; n <= asientosPorFila; n++) {
                asientos.add(new Asiento(letra, n, tipoAsiento));
            }
        }
    }

    public Optional<Asiento> buscarAsiento(String codigo) {
        return asientos.stream().filter(a -> a.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public boolean contiene(Asiento asiento) {
        return asientos.contains(asiento);
    }

    public int getCapacidad() {
        return asientos.size();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public int getFilas() {
        return filas;
    }

    public int getAsientosPorFila() {
        return asientosPorFila;
    }

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ", " + getCapacidad() + " sillas)";
    }
}
