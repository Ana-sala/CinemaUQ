package co.edu.uniquindio.cinemauq.excepciones;

/** El usuario no tiene permiso para realizar la operacion. */
public class OperacionNoAutorizadaException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public OperacionNoAutorizadaException(String mensaje) {
        super(mensaje);
    }
}
