package co.edu.uniquindio.cinemauq.excepciones;

/** Excepcion base del dominio CinemaUQ. Todas las reglas de negocio violadas la extienden. */
public class CinemaUQException extends Exception {

    private static final long serialVersionUID = 1L;

    public CinemaUQException(String mensaje) {
        super(mensaje);
    }
}
