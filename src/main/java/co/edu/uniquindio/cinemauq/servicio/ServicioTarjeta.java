package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.modelo.enums.Rol;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.Formato;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.util.List;

/** Recargas y control de la tarjeta virtual (RF-010, RF-022, RF-023). */
public class ServicioTarjeta {

    public static final double RECARGA_MINIMA = 10_000;
    public static final double RECARGA_MAXIMA = 500_000;

    private final Repositorio<SolicitudRecarga> solicitudes;
    private final Reloj reloj;

    public ServicioTarjeta(Repositorio<SolicitudRecarga> solicitudes, Reloj reloj) {
        this.solicitudes = solicitudes;
        this.reloj = reloj;
    }

    /** RF-010: el cliente pide una recarga; queda pendiente hasta que un administrador la apruebe. */
    public SolicitudRecarga solicitarRecarga(Cliente cliente, double valor) throws DatosInvalidosException {
        validarValor(valor);
        SolicitudRecarga solicitud = new SolicitudRecarga(cliente, valor, reloj.ahora());
        solicitudes.guardar(solicitud);
        return solicitud;
    }

    /** RN-03: solo un administrador recarga; la recarga queda en el historial de movimientos. */
    public Movimiento recargar(Usuario operador, Cliente cliente, double valor)
            throws OperacionNoAutorizadaException, DatosInvalidosException {
        exigirAdministrador(operador);
        validarValor(valor);
        return cliente.getTarjeta().acreditar(TipoMovimiento.RECARGA, valor,
                "Recarga realizada por " + operador.getNombre(), reloj.ahora());
    }

    public Movimiento aprobarSolicitud(Usuario operador, SolicitudRecarga solicitud)
            throws OperacionNoAutorizadaException, DatosInvalidosException {
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new DatosInvalidosException("La solicitud " + solicitud.getId() + " ya fue atendida.");
        }
        Movimiento movimiento = recargar(operador, solicitud.getCliente(), solicitud.getValor());
        solicitud.aprobar();
        return movimiento;
    }

    public void rechazarSolicitud(Usuario operador, SolicitudRecarga solicitud) throws OperacionNoAutorizadaException {
        exigirAdministrador(operador);
        solicitud.rechazar();
    }

    public void bloquearTarjeta(Usuario operador, Cliente cliente) throws OperacionNoAutorizadaException {
        exigirAdministrador(operador);
        cliente.getTarjeta().bloquear();
    }

    public void activarTarjeta(Usuario operador, Cliente cliente) throws OperacionNoAutorizadaException {
        exigirAdministrador(operador);
        cliente.getTarjeta().activar();
    }

    public List<SolicitudRecarga> solicitudesPendientes() {
        return solicitudes.buscar(s -> s.getEstado() == EstadoSolicitud.PENDIENTE);
    }

    public List<SolicitudRecarga> solicitudesDe(Cliente cliente) {
        return solicitudes.buscar(s -> s.getCliente().equals(cliente));
    }

    private void validarValor(double valor) throws DatosInvalidosException {
        if (valor < RECARGA_MINIMA || valor > RECARGA_MAXIMA) {
            throw new DatosInvalidosException("La recarga debe estar entre " + Formato.moneda(RECARGA_MINIMA) + " y "
                    + Formato.moneda(RECARGA_MAXIMA) + ".");
        }
    }

    private void exigirAdministrador(Usuario operador) throws OperacionNoAutorizadaException {
        if (operador == null || operador.getRol() != Rol.ADMINISTRADOR) {
            throw new OperacionNoAutorizadaException("Solo un administrador puede realizar esta operación.");
        }
    }
}
