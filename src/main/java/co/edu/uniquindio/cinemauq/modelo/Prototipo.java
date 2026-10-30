package co.edu.uniquindio.cinemauq.modelo;

/**
 * PATRÓN CREACIONAL · PROTOTYPE.
 * Un objeto que sabe crear una copia de sí mismo. El cliente no necesita
 * conocer todos sus atributos para duplicarlo.
 */
public interface Prototipo<T> {
    T clonar();
}
