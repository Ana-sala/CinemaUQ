package co.edu.uniquindio.cinemauq.precio;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.ArrayList;
import java.util.List;

/** DECORATOR · Concrete Decorator: rebaja un porcentaje del subtotal de entradas. */
public class DescuentoEntradas extends DecoradorPrecio {

    private final String nombre;
    private final double porcentaje;

    public DescuentoEntradas(CalculadoraPrecio envuelta, String nombre, double porcentaje) {
        super(envuelta);
        this.nombre = nombre;
        this.porcentaje = porcentaje;
    }

    @Override
    public double getSubtotalEntradas() {
        return Math.round(envuelta.getSubtotalEntradas() * (1 - porcentaje / 100));   // delega y agrega lo suyo
    }

    @Override
    public List<String> getDetalle() {
        List<String> detalle = new ArrayList<>(envuelta.getDetalle());
        double ahorro = envuelta.getSubtotalEntradas() - getSubtotalEntradas();
        if (ahorro > 0) {
            detalle.add(nombre + " (-" + (int) porcentaje + "% entradas): -" + Formato.moneda(ahorro));
        }
        return detalle;
    }
}
