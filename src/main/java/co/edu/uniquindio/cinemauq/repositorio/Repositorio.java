package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.Identificable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Contrato de almacenamiento (DIP): los servicios dependen de esta abstracción,
 * no de cómo se guardan los datos. Hoy es memoria; mañana puede ser un archivo
 * o una base de datos sin tocar los servicios.
 */
public interface Repositorio<T extends Identificable> {

    void guardar(T entidad);

    Optional<T> buscarPorId(String id);

    List<T> listar();

    List<T> buscar(Predicate<T> criterio);

    boolean eliminar(String id);
}
