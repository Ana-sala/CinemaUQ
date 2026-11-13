package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.ConflictoHorarioException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Lo que el ADMINISTRADOR hace con la cartelera (ISP: separado de la consulta).
 * Es también el Subject del patrón Proxy: GestionCarteleraProxy y ServicioCartelera
 * la implementan, y la pantalla del administrador no sabe con cuál habla.
 */
public interface GestionCartelera {

    Pelicula registrarPelicula(String titulo, Genero genero, Clasificacion clasificacion, int duracionMinutos,
                               String sinopsis) throws DatosInvalidosException, OperacionNoAutorizadaException;

    void actualizarPelicula(Pelicula pelicula, String titulo, Genero genero, Clasificacion clasificacion,
                            int duracionMinutos, String sinopsis) throws DatosInvalidosException, OperacionNoAutorizadaException;

    void cambiarEstadoCartelera(Pelicula pelicula, boolean enCartelera) throws OperacionNoAutorizadaException;

    Sala registrarSala(String nombre, TipoSala tipo, int filas, int asientosPorFila, int filasPreferenciales)
            throws DatosInvalidosException, OperacionNoAutorizadaException;

    Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase)
            throws DatosInvalidosException, ConflictoHorarioException, OperacionNoAutorizadaException;

    Funcion duplicarFuncion(Funcion original, LocalDateTime nuevaFechaHora)
            throws DatosInvalidosException, ConflictoHorarioException, OperacionNoAutorizadaException;

    List<Pelicula> listarPeliculas();

    List<Sala> listarSalas();

    List<Funcion> listarFunciones();
}
