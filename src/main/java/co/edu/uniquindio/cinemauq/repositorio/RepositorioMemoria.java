package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.Identificable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/** Implementación en memoria del repositorio, conserva el orden de inserción. */
public class RepositorioMemoria<T extends Identificable> implements Repositorio<T> {

    private final Map<String, T> datos = new LinkedHashMap<>();

    @Override
    public void guardar(T entidad) {
        datos.put(entidad.getId(), entidad);
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public List<T> buscar(Predicate<T> criterio) {
        return datos.values().stream().filter(criterio).toList();
    }

    @Override
    public boolean eliminar(String id) {
        return datos.remove(id) != null;
    }
}
