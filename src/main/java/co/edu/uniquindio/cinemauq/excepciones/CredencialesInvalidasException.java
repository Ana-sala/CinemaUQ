package co.edu.uniquindio.cinemauq.excepciones;

/** Correo o contrasena incorrectos al iniciar sesion. */
public class CredencialesInvalidasException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
