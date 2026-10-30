package co.edu.uniquindio.cinemauq.excepciones;

/** La compra no se puede cancelar segun la politica de cancelacion. */
public class CancelacionNoPermitidaException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public CancelacionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
