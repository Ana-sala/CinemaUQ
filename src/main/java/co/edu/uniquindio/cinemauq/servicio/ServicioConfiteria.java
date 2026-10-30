package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Combo;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Producto;
import co.edu.uniquindio.cinemauq.modelo.Promocion;
import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.time.LocalDate;
import java.util.List;

/** Productos, combos y promociones (RF-006, RF-018, RF-019). */
public class ServicioConfiteria {

    private final Repositorio<ItemConfiteria> items;
    private final Repositorio<Promocion> promociones;

    public ServicioConfiteria(Repositorio<ItemConfiteria> items, Repositorio<Promocion> promociones) {
        this.items = items;
        this.promociones = promociones;
    }

    public Producto registrarProducto(String nombre, double precio, CategoriaProducto categoria) throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank() || precio <= 0 || categoria == null) {
            throw new DatosInvalidosException("Producto inválido: nombre, precio positivo y categoría son obligatorios.");
        }
        Producto producto = new Producto(GeneradorId.siguiente("PRO"), nombre.trim(), precio, categoria);
        items.guardar(producto);
        return producto;
    }

    public Combo registrarCombo(String nombre, double porcentajeDescuento, List<Producto> productos) throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank() || productos == null || productos.size() < 2) {
            throw new DatosInvalidosException("Un combo necesita nombre y al menos dos productos.");
        }
        if (porcentajeDescuento < 0 || porcentajeDescuento > 50) {
            throw new DatosInvalidosException("El descuento de un combo va de 0 % a 50 %.");
        }
        Combo combo = new Combo(GeneradorId.siguiente("COM"), nombre.trim(), porcentajeDescuento);
        productos.forEach(combo::agregarProducto);
        items.guardar(combo);
        return combo;
    }

    public List<ItemConfiteria> listarDisponibles() {
        return items.buscar(ItemConfiteria::isDisponible);
    }

    public List<Producto> listarProductos() {
        return items.listar().stream().filter(Producto.class::isInstance).map(Producto.class::cast).toList();
    }

    public Promocion registrarPromocion(String nombre, double porcentaje, LocalDate inicio, LocalDate fin)
            throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank() || porcentaje <= 0 || porcentaje > 100 || inicio == null
                || fin == null || fin.isBefore(inicio)) {
            throw new DatosInvalidosException("Promoción inválida: revise nombre, porcentaje y fechas.");
        }
        Promocion promocion = new Promocion(GeneradorId.siguiente("PRM"), nombre.trim(), porcentaje, inicio, fin);
        promociones.guardar(promocion);
        return promocion;
    }

    public List<Promocion> listarPromociones() {
        return promociones.listar();
    }
}
