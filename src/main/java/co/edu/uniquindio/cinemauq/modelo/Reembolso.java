package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.time.LocalDateTime;

/** Devolución de dinero (y puntos) de una compra. Una compra tiene como máximo un reembolso. */
public class Reembolso implements Identificable {

    private final String id;
    private final String idCompra;
    private final LocalDateTime fecha;
    private final int porcentaje;
    private final double valor;
    private final int puntosDevueltos;
    private final MotivoReembolso motivo;

    public Reembolso(String idCompra, LocalDateTime fecha, int porcentaje, double valor, int puntosDevueltos,
                     MotivoReembolso motivo) {
        this.id = GeneradorId.siguiente("REE");
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.porcentaje = porcentaje;
        this.valor = valor;
        this.puntosDevueltos = puntosDevueltos;
        this.motivo = motivo;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getIdCompra() {
        return idCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public double getValor() {
        return valor;
    }

    public int getPuntosDevueltos() {
        return puntosDevueltos;
    }

    public MotivoReembolso getMotivo() {
        return motivo;
    }
}
