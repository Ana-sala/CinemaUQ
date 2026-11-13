package co.edu.uniquindio.cinemauq.precio;

import java.util.List;

/**
 * PATRÓN ESTRUCTURAL · DECORATOR (Component).
 * Calcula lo que se cobra en dinero por una compra, separado en entradas y confitería.
 * Las promociones son decoradores que envuelven esta calculadora y rebajan su parte.
 */
public interface CalculadoraPrecio {

    double getSubtotalEntradas();

    double getSubtotalConfiteria();

    /** Una línea por cada concepto: subtotales y cada descuento aplicado. */
    List<String> getDetalle();

    default double getTotal() {
        return getSubtotalEntradas() + getSubtotalConfiteria();
    }
}
