package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Combo;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Producto;
import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.util.List;

/** Productos y combos de confitería (RF-006, RF-018). */
public class ServicioConfiteria {

    private final Repositorio<ItemConfiteria> items;

    public ServicioConfiteria(Repositorio<ItemConfiteria> items) {
        this.items = items;
    }

    public Producto registrarProducto(String nombre, double precio, CategoriaProducto categoria) throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank() || precio <= 0 || categoria == null) {
            throw new DatosInvalidosException("Producto inválido: nombre, precio positivo y categoría son obligatorios.");
        }
        Producto producto = new Producto(GeneradorId.siguiente("PRO"), nombre.trim(), precio, categoria);
        items.guardar(producto);
        return producto;
    }

    /** Composite: los elementos pueden ser productos o combos ya existentes (RN-18). */
    public Combo registrarCombo(String nombre, double porcentajeDescuento, List<? extends ItemConfiteria> elementos)
            throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank() || elementos == null || elementos.size() < 2) {
            throw new DatosInvalidosException("Un combo necesita nombre y al menos dos elementos.");
        }
        if (porcentajeDescuento < 0 || porcentajeDescuento > 50) {
            throw new DatosInvalidosException("El descuento de un combo va de 0 % a 50 %.");
        }
        Combo combo = new Combo(GeneradorId.siguiente("COM"), nombre.trim(), porcentajeDescuento);
        for (ItemConfiteria elemento : elementos) {
            combo.agregar(elemento);
        }
        items.guardar(combo);
        return combo;
    }

    public List<ItemConfiteria> listarDisponibles() {
        return items.buscar(ItemConfiteria::isDisponible);
    }

    public List<ItemConfiteria> listarTodos() {
        return items.listar();
    }

    public List<Producto> listarProductos() {
        return items.listar().stream().filter(Producto.class::isInstance).map(Producto.class::cast).toList();
    }
}
