package co.edu.uniquindio.cinemauq.precio;

import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.util.ArrayList;
import java.util.List;

/**
 * DECORATOR · Concrete Component: el precio sin promociones.
 * Suma solo lo que se paga en dinero (lo marcado "con puntos" no suma).
 */
public class PrecioBase implements CalculadoraPrecio {

    private final double entradas;
    private final double confiteria;

    public PrecioBase(List<Entrada> entradas, List<LineaConfiteria> lineas) {
        this.entradas = entradas.stream().filter(e -> !e.isPagadaConPuntos()).mapToDouble(Entrada::getPrecio).sum();
        this.confiteria = lineas.stream().filter(l -> !l.isPagadaConPuntos()).mapToDouble(LineaConfiteria::getSubtotal).sum();
    }

    @Override
    public double getSubtotalEntradas() {
        return entradas;
    }

    @Override
    public double getSubtotalConfiteria() {
        return confiteria;
    }

    @Override
    public List<String> getDetalle() {
        List<String> detalle = new ArrayList<>();
        detalle.add("Entradas: " + Formato.moneda(entradas));
        detalle.add("Confitería: " + Formato.moneda(confiteria));
        return detalle;
    }
}
