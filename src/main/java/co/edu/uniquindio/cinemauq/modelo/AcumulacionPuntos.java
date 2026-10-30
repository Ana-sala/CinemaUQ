package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDate;

/** Un lote de puntos obtenidos en una fecha. Vence un año después (RN-05). */
public class AcumulacionPuntos {

    private final int puntosObtenidos;
    private int puntosDisponibles;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private final String origen;

    public AcumulacionPuntos(int puntos, LocalDate fechaObtencion, String origen) {
        this.puntosObtenidos = puntos;
        this.puntosDisponibles = puntos;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaObtencion.plusYears(1);
        this.origen = origen;
    }

    /** Vigente hasta el día anterior a la fecha de vencimiento. */
    public boolean estaVigente(LocalDate hoy) {
        return hoy.isBefore(fechaVencimiento);
    }

    /** Descuenta hasta 'cantidad' puntos de este lote y devuelve cuántos tomó. */
    int consumir(int cantidad) {
        int tomados = Math.min(cantidad, puntosDisponibles);
        puntosDisponibles -= tomados;
        return tomados;
    }

    void anular() {
        puntosDisponibles = 0;
    }

    public int getPuntosObtenidos() {
        return puntosObtenidos;
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getOrigen() {
        return origen;
    }

    @Override
    public String toString() {
        return puntosObtenidos + " pts (" + puntosDisponibles + " disponibles) · obtenidos "
                + Formato.fecha(fechaObtencion) + " · vencen " + Formato.fecha(fechaVencimiento) + " · " + origen;
    }
}
