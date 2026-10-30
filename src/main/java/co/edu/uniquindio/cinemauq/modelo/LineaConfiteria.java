package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.util.Formato;

/** Un ítem de confitería con su cantidad dentro de una compra. */
public class LineaConfiteria {

    private final ItemConfiteria item;
    private final int cantidad;
    private final double precioUnitario;
    private final boolean pagadaConPuntos;

    public LineaConfiteria(ItemConfiteria item, int cantidad, boolean pagadaConPuntos) {
        this.item = item;
        this.cantidad = cantidad;
        this.precioUnitario = item.getPrecio();
        this.pagadaConPuntos = pagadaConPuntos;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    public ItemConfiteria getItem() {
        return item;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public boolean isPagadaConPuntos() {
        return pagadaConPuntos;
    }

    @Override
    public String toString() {
        return cantidad + " x " + item.getNombre() + " · "
                + (pagadaConPuntos ? "con puntos" : Formato.moneda(getSubtotal()));
    }
}
