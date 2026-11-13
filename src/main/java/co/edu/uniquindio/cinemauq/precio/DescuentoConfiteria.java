package co.edu.uniquindio.cinemauq.precio;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.ArrayList;
import java.util.List;

/** DECORATOR · Concrete Decorator: rebaja un porcentaje del subtotal de confitería. */
public class DescuentoConfiteria extends DecoradorPrecio {

    private final String nombre;
    private final double porcentaje;

    public DescuentoConfiteria(CalculadoraPrecio envuelta, String nombre, double porcentaje) {
        super(envuelta);
        this.nombre = nombre;
        this.porcentaje = porcentaje;
    }

    @Override
    public double getSubtotalConfiteria() {
        return Math.round(envuelta.getSubtotalConfiteria() * (1 - porcentaje / 100));
    }

    @Override
    public List<String> getDetalle() {
        List<String> detalle = new ArrayList<>(envuelta.getDetalle());
        double ahorro = envuelta.getSubtotalConfiteria() - getSubtotalConfiteria();
        if (ahorro > 0) {
            detalle.add(nombre + " (-" + (int) porcentaje + "% confitería): -" + Formato.moneda(ahorro));
        }
        return detalle;
    }
}
