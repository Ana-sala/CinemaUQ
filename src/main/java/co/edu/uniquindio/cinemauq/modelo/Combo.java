package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PATRÓN ESTRUCTURAL · COMPOSITE (Composite).
 * Un combo agrupa ítems de confitería: productos sueltos y también otros combos
 * (por ejemplo, "Combo Mega Pareja" = "Combo Pareja" + nachos). Su precio es la
 * suma de lo que contiene menos su descuento; si adentro hay otro combo, ese combo
 * aplica primero su propio descuento (cada nivel del árbol aplica el suyo).
 */
public class Combo implements ItemConfiteria {

    private final String id;
    private String nombre;
    private final List<ItemConfiteria> elementos = new ArrayList<>();   // del tipo de la interfaz: caben combos
    private double porcentajeDescuento;
    private boolean disponible = true;

    public Combo(String id, String nombre, double porcentajeDescuento) {
        this.id = id;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    /** Agrega un producto o un combo. Impide ciclos: un combo no puede contenerse a sí mismo. */
    public void agregar(ItemConfiteria item) throws DatosInvalidosException {
        if (item == this || (item instanceof Combo otro && otro.contiene(this))) {
            throw new DatosInvalidosException("Un combo no puede contenerse a sí mismo.");
        }
        elementos.add(item);
    }

    /** true si 'item' está en este combo, a cualquier profundidad. */
    public boolean contiene(ItemConfiteria item) {
        for (ItemConfiteria e : elementos) {
            if (e == item || (e instanceof Combo c && c.contiene(item))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getPrecio() {
        double suma = 0;
        for (ItemConfiteria e : elementos) {
            suma += e.getPrecio();                      // recursivo si e es otro combo
        }
        return Math.round(suma * (1 - porcentajeDescuento / 100));
    }

    @Override
    public boolean isDisponible() {
        return disponible && elementos.stream().allMatch(ItemConfiteria::isDisponible);
    }

    @Override
    public int contarProductos() {
        return elementos.stream().mapToInt(ItemConfiteria::contarProductos).sum();
    }

    @Override
    public String describir(String sangria) {
        StringBuilder sb = new StringBuilder(sangria + "+ " + nombre + " " + Formato.moneda(getPrecio())
                + " (-" + (int) porcentajeDescuento + "%)\n");
        for (ItemConfiteria e : elementos) {
            sb.append(e.describir(sangria + "   "));   // cada hijo se describe con más sangría
        }
        return sb.toString();
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

    public List<ItemConfiteria> getElementos() {
        return Collections.unmodifiableList(elementos);
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    @Override
    public String toString() {
        return nombre + " · " + Formato.moneda(getPrecio()) + " (combo de " + contarProductos() + " productos, -"
                + (int) porcentajeDescuento + "%)";
    }
}
