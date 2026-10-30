package co.edu.uniquindio.cinemauq.excepciones;

/** El cliente no tiene puntos vigentes suficientes para la redencion. */
public class PuntosInsuficientesException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public PuntosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}
