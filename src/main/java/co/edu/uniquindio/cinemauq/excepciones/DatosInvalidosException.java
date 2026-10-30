package co.edu.uniquindio.cinemauq.excepciones;

/** Datos de entrada que no cumplen las validaciones del dominio. */
public class DatosInvalidosException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
