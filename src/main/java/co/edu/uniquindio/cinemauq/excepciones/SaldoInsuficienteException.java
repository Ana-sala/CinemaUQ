package co.edu.uniquindio.cinemauq.excepciones;

/** La tarjeta virtual no tiene saldo suficiente para el pago. */
public class SaldoInsuficienteException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
