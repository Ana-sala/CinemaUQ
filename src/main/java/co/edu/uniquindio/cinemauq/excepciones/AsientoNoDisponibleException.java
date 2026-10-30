package co.edu.uniquindio.cinemauq.excepciones;

/** El asiento ya esta vendido o no pertenece a la sala de la funcion. */
public class AsientoNoDisponibleException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public AsientoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
