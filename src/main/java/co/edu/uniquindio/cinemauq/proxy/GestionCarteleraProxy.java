package co.edu.uniquindio.cinemauq.proxy;

import co.edu.uniquindio.cinemauq.contexto.Sesion;
import co.edu.uniquindio.cinemauq.excepciones.ConflictoHorarioException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.RegistroAuditoria;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.modelo.enums.Rol;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;
import co.edu.uniquindio.cinemauq.servicio.GestionCartelera;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PATRÓN ESTRUCTURAL · PROXY (de protección y de registro).
 * Se pone delante del servicio real de cartelera con la misma interfaz. Antes de cada
 * operación que modifica la cartelera (1) verifica que quien tiene la sesión sea
 * administrador, (2) deja la operación en la auditoría y (3) delega al servicio real.
 * Las consultas (listar) pasan directo, sin control.
 */
public class GestionCarteleraProxy implements GestionCartelera {

    private final GestionCartelera real;       // TIENE el real (composición), privado
    private final Sesion sesion;
    private final Auditoria auditoria;
    private final Reloj reloj;

    public GestionCarteleraProxy(GestionCartelera real, Sesion sesion, Auditoria auditoria, Reloj reloj) {
        this.real = real;
        this.sesion = sesion;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    /** Protección + registro. Si no es administrador, registra el intento y no deja pasar. */
    private void controlar(String operacion) throws OperacionNoAutorizadaException {
        Usuario usuario = sesion.getUsuario();
        boolean permitida = usuario != null && usuario.getRol() == Rol.ADMINISTRADOR;
        String quien = usuario == null ? "sin sesión" : usuario.getCorreo();
        auditoria.registrar(new RegistroAuditoria(reloj.ahora(), quien, operacion, permitida));
        if (!permitida) {
            throw new OperacionNoAutorizadaException("Solo un administrador puede: " + operacion + ".");
        }
    }

    @Override
    public Pelicula registrarPelicula(String titulo, Genero genero, Clasificacion clasificacion, int duracionMinutos,
                                      String sinopsis) throws DatosInvalidosException, OperacionNoAutorizadaException {
        controlar("registrar película \"" + titulo + "\"");
        return real.registrarPelicula(titulo, genero, clasificacion, duracionMinutos, sinopsis);
    }

    @Override
    public void actualizarPelicula(Pelicula pelicula, String titulo, Genero genero, Clasificacion clasificacion,
                                   int duracionMinutos, String sinopsis) throws DatosInvalidosException, OperacionNoAutorizadaException {
        controlar("editar película \"" + pelicula.getTitulo() + "\"");
        real.actualizarPelicula(pelicula, titulo, genero, clasificacion, duracionMinutos, sinopsis);
    }

    @Override
    public void cambiarEstadoCartelera(Pelicula pelicula, boolean enCartelera) throws OperacionNoAutorizadaException {
        controlar((enCartelera ? "poner en" : "quitar de") + " cartelera \"" + pelicula.getTitulo() + "\"");
        real.cambiarEstadoCartelera(pelicula, enCartelera);
    }

    @Override
    public Sala registrarSala(String nombre, TipoSala tipo, int filas, int asientosPorFila, int filasPreferenciales)
            throws DatosInvalidosException, OperacionNoAutorizadaException {
        controlar("registrar sala \"" + nombre + "\"");
        return real.registrarSala(nombre, tipo, filas, asientosPorFila, filasPreferenciales);
    }

    @Override
    public Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase)
            throws DatosInvalidosException, ConflictoHorarioException, OperacionNoAutorizadaException {
        controlar("programar función de " + (pelicula == null ? "?" : "\"" + pelicula.getTitulo() + "\""));
        return real.programarFuncion(pelicula, sala, fechaHora, precioBase);
    }

    @Override
    public Funcion duplicarFuncion(Funcion original, LocalDateTime nuevaFechaHora)
            throws DatosInvalidosException, ConflictoHorarioException, OperacionNoAutorizadaException {
        controlar("duplicar función " + original.getId());
        return real.duplicarFuncion(original, nuevaFechaHora);
    }

    @Override
    public List<Pelicula> listarPeliculas() {
        return real.listarPeliculas();
    }

    @Override
    public List<Sala> listarSalas() {
        return real.listarSalas();
    }

    @Override
    public List<Funcion> listarFunciones() {
        return real.listarFunciones();
    }
}
