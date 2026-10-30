package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Combo de confitería: varios productos con un porcentaje de descuento. */
public class Combo implements ItemConfiteria {

    private final String id;
    private String nombre;
    private final List<Producto> productos = new ArrayList<>();
    private double porcentajeDescuento;
    private boolean disponible = true;

    public Combo(String id, String nombre, double porcentajeDescuento) {
        this.id = id;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    @Override
    public double getPrecio() {
        double suma = productos.stream().mapToDouble(Producto::getPrecio).sum();
        return Math.round(suma * (1 - porcentajeDescuento / 100));
    }

    @Override
    public boolean isDisponible() {
        return disponible && productos.stream().allMatch(Producto::isDisponible);
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    @Override
    public String toString() {
        return nombre + " · " + Formato.moneda(getPrecio()) + " (" + productos.size() + " productos, -"
                + (int) porcentajeDescuento + "%)";
    }
}
