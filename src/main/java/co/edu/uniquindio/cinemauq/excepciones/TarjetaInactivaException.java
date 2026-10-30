package co.edu.uniquindio.cinemauq.excepciones;

/** La tarjeta virtual esta bloqueada y no puede pagar. */
public class TarjetaInactivaException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public TarjetaInactivaException(String mensaje) {
        super(mensaje);
    }
}
