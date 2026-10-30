package co.edu.uniquindio.cinemauq.excepciones;

/** La funcion esta cancelada o ya inicio: no admite ventas. */
public class FuncionNoDisponibleException extends CinemaUQException {

    private static final long serialVersionUID = 1L;

    public FuncionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
