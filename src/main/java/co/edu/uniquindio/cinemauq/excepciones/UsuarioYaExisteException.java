package co.edu.uniquindio.cinemauq.excepciones;

/** Ya existe un usuario registrado con ese correo o documento. */
public class UsuarioYaExisteException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
}
