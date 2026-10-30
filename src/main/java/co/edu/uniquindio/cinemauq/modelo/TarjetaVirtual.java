package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.excepciones.TarjetaInactivaException;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tarjeta virtual del cliente (SRP: solo maneja su saldo y su historial de movimientos).
 * Todo cambio de saldo pasa por debitar/acreditar y deja un Movimiento.
 */
public class TarjetaVirtual {

    private final String numero;
    private double saldo;
    private EstadoTarjeta estado = EstadoTarjeta.ACTIVA;
    private final List<Movimiento> movimientos = new ArrayList<>();

    public TarjetaVirtual(String numero) {
        this.numero = numero;
    }

    /** Verifica que la tarjeta pueda pagar el valor, sin modificar nada. */
    public void validarPago(double valor) throws TarjetaInactivaException, SaldoInsuficienteException {
        if (estado != EstadoTarjeta.ACTIVA) {
            throw new TarjetaInactivaException("La tarjeta " + numero + " está bloqueada.");
        }
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente: tiene " + Formato.moneda(saldo)
                    + " y el pago es de " + Formato.moneda(valor) + ". Solicite una recarga.");
        }
    }

    public Movimiento debitar(double valor, String descripcion, LocalDateTime fecha)
            throws TarjetaInactivaException, SaldoInsuficienteException {
        validarPago(valor);
        saldo -= valor;
        return registrar(fecha, TipoMovimiento.PAGO, valor, descripcion);
    }

    /** Recargas y reembolsos. Un reembolso entra aunque la tarjeta esté bloqueada (RN-13). */
    public Movimiento acreditar(TipoMovimiento tipo, double valor, String descripcion, LocalDateTime fecha)
            throws DatosInvalidosException {
        if (tipo == TipoMovimiento.PAGO) {
            throw new DatosInvalidosException("Un pago no se acredita: use debitar.");
        }
        if (valor <= 0) {
            throw new DatosInvalidosException("El valor a acreditar debe ser mayor que cero.");
        }
        saldo += valor;
        return registrar(fecha, tipo, valor, descripcion);
    }

    private Movimiento registrar(LocalDateTime fecha, TipoMovimiento tipo, double valor, String descripcion) {
        Movimiento movimiento = new Movimiento(fecha, tipo, valor, descripcion, saldo);
        movimientos.add(movimiento);
        return movimiento;
    }

    public boolean estaActiva() {
        return estado == EstadoTarjeta.ACTIVA;
    }

    public void bloquear() {
        estado = EstadoTarjeta.BLOQUEADA;
    }

    public void activar() {
        estado = EstadoTarjeta.ACTIVA;
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public EstadoTarjeta getEstado() {
        return estado;
    }

    public List<Movimiento> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }
}
