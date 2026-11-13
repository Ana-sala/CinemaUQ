package co.edu.uniquindio.cinemauq.precio;

import java.util.List;

/**
 * DECORATOR · Decorator: implementa la misma interfaz y TIENE una calculadora adentro.
 * Por defecto delega todo; cada promoción concreta cambia solo lo que le corresponde.
 */
public abstract class DecoradorPrecio implements CalculadoraPrecio {

    protected final CalculadoraPrecio envuelta;

    protected DecoradorPrecio(CalculadoraPrecio envuelta) {
        this.envuelta = envuelta;
    }

    @Override
    public double getSubtotalEntradas() {
        return envuelta.getSubtotalEntradas();
    }

    @Override
    public double getSubtotalConfiteria() {
        return envuelta.getSubtotalConfiteria();
    }

    @Override
    public List<String> getDetalle() {
        return envuelta.getDetalle();
    }
}
