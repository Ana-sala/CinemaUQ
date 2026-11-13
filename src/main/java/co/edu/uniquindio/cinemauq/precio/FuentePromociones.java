package co.edu.uniquindio.cinemauq.precio;

import co.edu.uniquindio.cinemauq.modelo.Funcion;

/**
 * Quién decide qué promociones aplican a una función (DIP: el builder y el servicio
 * de compras dependen de esta interfaz, no de ServicioPromociones).
 */
@FunctionalInterface
public interface FuentePromociones {

    /** Envuelve la calculadora con los decoradores de las promociones vigentes para esa función. */
    CalculadoraPrecio aplicar(CalculadoraPrecio base, Funcion funcion);

    /** Sin promociones: devuelve la misma calculadora. */
    static FuentePromociones ninguna() {
        return (base, funcion) -> base;
    }
}
