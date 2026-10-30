package co.edu.uniquindio.cinemauq.excepciones;

/** Dos funciones se cruzan en la misma sala. */
public class ConflictoHorarioException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public ConflictoHorarioException(String mensaje) {
        super(mensaje);
    }
}
