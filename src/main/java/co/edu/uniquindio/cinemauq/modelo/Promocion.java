package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.AlcancePromocion;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Promoción del cine: un porcentaje de descuento sobre las entradas o sobre la confitería,
 * vigente entre dos fechas y, opcionalmente, solo un día de la semana ("Martes de cine").
 * Se aplica al precio con el patrón Decorator (paquete precio).
 */
public class Promocion implements Identificable {

    private final String id;
    private final String nombre;
    private final double porcentajeDescuento;
    private final AlcancePromocion alcance;
    private final DayOfWeek dia;          // null = todos los días
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private boolean activa = true;

    public Promocion(String id, String nombre, double porcentajeDescuento, AlcancePromocion alcance, DayOfWeek dia,
                     LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
        this.alcance = alcance;
        this.dia = dia;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    /** ¿Aplica para una función de esa fecha? Se mira la fecha de la función, no la de la compra. */
    public boolean aplicaPara(LocalDate fechaFuncion) {
        return activa && !fechaFuncion.isBefore(fechaInicio) && !fechaFuncion.isAfter(fechaFin)
                && (dia == null || fechaFuncion.getDayOfWeek() == dia);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public AlcancePromocion getAlcance() {
        return alcance;
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        String cuando = dia == null ? "todos los días" : "solo " + dia.getDisplayName(TextStyle.FULL, Locale.of("es", "CO"));
        return nombre + " · -" + (int) porcentajeDescuento + "% en " + alcance.toString().toLowerCase() + " · " + cuando
                + " · " + Formato.fecha(fechaInicio) + " a " + Formato.fecha(fechaFin) + (activa ? "" : " (inactiva)");
    }
}
