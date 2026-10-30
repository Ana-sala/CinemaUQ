package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.time.LocalDateTime;

/** Registro inmutable de un pago, recarga o reembolso sobre una tarjeta virtual. */
public class Movimiento implements Identificable {

    private final String id;
    private final LocalDateTime fecha;
    private final TipoMovimiento tipo;
    private final double valor;
    private final String descripcion;
    private final double saldoResultante;

    public Movimiento(LocalDateTime fecha, TipoMovimiento tipo, double valor, String descripcion,
                      double saldoResultante) {
        this.id = GeneradorId.siguiente("MOV");
        this.fecha = fecha;
        this.tipo = tipo;
        this.valor = valor;
        this.descripcion = descripcion;
        this.saldoResultante = saldoResultante;
    }

    @Override
    public String getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getSaldoResultante() {
        return saldoResultante;
    }

    @Override
    public String toString() {
        String signo = tipo == TipoMovimiento.PAGO ? "-" : "+";
        return Formato.fechaHora(fecha) + " · " + tipo + " " + signo + Formato.moneda(valor)
                + " · " + descripcion + " · saldo " + Formato.moneda(saldoResultante);
    }
}
