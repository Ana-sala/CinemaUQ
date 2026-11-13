package co.edu.uniquindio.cinemauq.observador;

import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;

/**
 * PATRÓN DE COMPORTAMIENTO · OBSERVER (observador).
 * Quien quiera enterarse de lo que pasa con las compras implementa esta interfaz y se
 * suscribe en ServicioCompras (el sujeto). El servicio avisa a todos los suscritos sin
 * saber qué hace cada uno: agregar un aviso nuevo (correo, contabilidad) es otra clase.
 */
public interface ObservadorCompras {

    /** Se llama cuando una compra quedó pagada. */
    void compraPagada(Compra compra);

    /** Se llama cuando una compra fue cancelada y reembolsada. */
    void compraCancelada(Compra compra, Reembolso reembolso);
}
