package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;

/**
 * Promoción que el administrador registra (p. ej. "Martes 2x1" o "-20 % en combos").
 * En esta entrega solo se gestiona; su aplicación sobre el precio se implementará
 * en la entrega de patrones estructurales/comportamiento.
 */
public class Promocion implements Identificable {

    private final String id;
    private String nombre;
    private double porcentajeDescuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa = true;

    public Promocion(String id, String nombre, double porcentajeDescuento, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.porcentajeDescuento = porcentajeDescuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public boolean estaVigente(LocalDate hoy) {
        return activa && !hoy.isBefore(fechaInicio) && !hoy.isAfter(fechaFin);
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
        return nombre + " · -" + (int) porcentajeDescuento + "% · " + Formato.fecha(fechaInicio) + " a "
                + Formato.fecha(fechaFin) + (activa ? "" : " (inactiva)");
    }
}
