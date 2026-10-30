package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.util.Formato;

/** Producto suelto de confitería. */
public class Producto implements ItemConfiteria {

    private final String id;
    private String nombre;
    private double precio;
    private CategoriaProducto categoria;
    private boolean disponible = true;

    public Producto(String id, String nombre, double precio, CategoriaProducto categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    @Override
    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return nombre + " · " + Formato.moneda(precio);
    }
}
