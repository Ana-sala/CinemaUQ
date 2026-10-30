package co.edu.uniquindio.cinemauq.excepciones;

/** Se intento reembolsar dos veces la misma operacion. */
public class ReembolsoDuplicadoException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public ReembolsoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
