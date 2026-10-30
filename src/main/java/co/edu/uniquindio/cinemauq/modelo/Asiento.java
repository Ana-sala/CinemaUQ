package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;

import java.util.Objects;

/** Asiento físico de una sala. Su código es fila + número: "C7". */
public class Asiento {

    private final char fila;
    private final int numero;
    private final TipoAsiento tipo;

    public Asiento(char fila, int numero, TipoAsiento tipo) {
        this.fila = fila;
        this.numero = numero;
        this.tipo = tipo;
    }

    public String getCodigo() {
        return "" + fila + numero;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public TipoAsiento getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asiento otro)) {
            return false;
        }
        return fila == otro.fila && numero == otro.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, numero);
    }

    @Override
    public String toString() {
        return getCodigo() + (tipo == TipoAsiento.PREFERENCIAL ? " (preferencial)" : "");
    }
}
