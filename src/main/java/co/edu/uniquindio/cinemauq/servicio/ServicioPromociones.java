package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Promocion;
import co.edu.uniquindio.cinemauq.modelo.enums.AlcancePromocion;
import co.edu.uniquindio.cinemauq.precio.CalculadoraPrecio;
import co.edu.uniquindio.cinemauq.precio.DescuentoConfiteria;
import co.edu.uniquindio.cinemauq.precio.DescuentoEntradas;
import co.edu.uniquindio.cinemauq.precio.FuentePromociones;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/** Promociones (RF-019): las registra y las aplica al precio apilando decoradores. */
public class ServicioPromociones implements FuentePromociones {

    private final Repositorio<Promocion> promociones;

    public ServicioPromociones(Repositorio<Promocion> promociones) {
        this.promociones = promociones;
    }

    public Promocion registrarPromocion(String nombre, double porcentaje, AlcancePromocion alcance, DayOfWeek dia,
                                        LocalDate inicio, LocalDate fin) throws DatosInvalidosException {
        if (nombre == null || nombre.isBlank() || porcentaje <= 0 || porcentaje > 100 || alcance == null
                || inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new DatosInvalidosException("Promoción inválida: revise nombre, porcentaje (1 a 100), alcance y fechas.");
        }
        Promocion promocion = new Promocion(GeneradorId.siguiente("PRM"), nombre.trim(), porcentaje, alcance, dia, inicio, fin);
        promociones.guardar(promocion);
        return promocion;
    }

    public void cambiarEstado(Promocion promocion) {
        promocion.setActiva(!promocion.isActiva());
    }

    public List<Promocion> listarPromociones() {
        return promociones.listar();
    }

    /**
     * Decorator en acción: por cada promoción que aplica a la fecha de la función se
     * envuelve la calculadora con un decorador más. Las promociones se apilan.
     */
    @Override
    public CalculadoraPrecio aplicar(CalculadoraPrecio base, Funcion funcion) {
        CalculadoraPrecio precio = base;
        LocalDate fecha = funcion.getFechaHora().toLocalDate();
        for (Promocion p : promociones.buscar(p -> p.aplicaPara(fecha))) {
            precio = p.getAlcance() == AlcancePromocion.ENTRADAS
                    ? new DescuentoEntradas(precio, p.getNombre(), p.getPorcentajeDescuento())
                    : new DescuentoConfiteria(precio, p.getNombre(), p.getPorcentajeDescuento());
        }
        return precio;
    }
}
