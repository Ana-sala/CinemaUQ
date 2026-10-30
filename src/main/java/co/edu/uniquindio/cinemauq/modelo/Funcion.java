package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Proyección de una película en una sala, en una fecha y hora.
 * Guarda los asientos vendidos: un asiento no se vende dos veces para la misma función (RN-01).
 * Es un Prototipo: el administrador duplica una función ya configurada para otra fecha.
 */
public class Funcion implements Identificable, Prototipo<Funcion> {

    private final String id;
    private final Pelicula pelicula;
    private final Sala sala;
    private LocalDateTime fechaHora;
    private double precioBase;
    private EstadoFuncion estado = EstadoFuncion.PROGRAMADA;
    private final Set<Asiento> ocupados = new HashSet<>();

    public Funcion(String id, Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase;
    }

    /** Prototype: copia película, sala y precio; la copia nace PROGRAMADA, con id nuevo y sin sillas vendidas. */
    @Override
    public Funcion clonar() {
        return new Funcion(GeneradorId.siguiente("FUN"), pelicula, sala, fechaHora, precioBase);
    }

    /** Atajo: clona y cambia la fecha. */
    public Funcion clonarPara(LocalDateTime nuevaFechaHora) {
        Funcion copia = clonar();
        copia.fechaHora = nuevaFechaHora;
        return copia;
    }

    public boolean estaDisponible(Asiento asiento) {
        return sala.contiene(asiento) && !ocupados.contains(asiento);
    }

    public void ocupar(Asiento asiento) throws AsientoNoDisponibleException {
        if (!sala.contiene(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo() + " no existe en " + sala.getNombre());
        }
        if (!ocupados.add(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo() + " ya fue vendido para esta función.");
        }
    }

    public void liberar(Asiento asiento) {
        ocupados.remove(asiento);
    }

    /** Se vende solo si está programada y aún no ha iniciado (RN-09). */
    public boolean admiteVentas(LocalDateTime ahora) {
        return estado == EstadoFuncion.PROGRAMADA && ahora.isBefore(fechaHora);
    }

    public LocalDateTime getFin() {
        return fechaHora.plusMinutes(pelicula.getDuracionMinutos());
    }

    public void cancelar() {
        estado = EstadoFuncion.CANCELADA;
    }

    public int getDisponibles() {
        return sala.getCapacidad() - ocupados.size();
    }

    public double getPorcentajeOcupacion() {
        return 100.0 * ocupados.size() / sala.getCapacidad();
    }

    @Override
    public String getId() {
        return id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public Set<Asiento> getOcupados() {
        return Collections.unmodifiableSet(ocupados);
    }

    @Override
    public String toString() {
        String base = Formato.fechaHora(fechaHora) + " · " + sala.getNombre() + " " + sala.getTipo()
                + " · " + Formato.moneda(precioBase);
        return estado == EstadoFuncion.CANCELADA ? base + " · CANCELADA" : base + " · " + getDisponibles() + " libres";
    }
}
