package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.ConflictoHorarioException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;

import java.time.LocalDateTime;
import java.util.List;

/** Lo que el ADMINISTRADOR hace con la cartelera (ISP: separado de la consulta). */
public interface GestionCartelera {

    Pelicula registrarPelicula(String titulo, Genero genero, Clasificacion clasificacion, int duracionMinutos,
                               String sinopsis) throws DatosInvalidosException;

    void cambiarEstadoCartelera(Pelicula pelicula, boolean enCartelera);

    Sala registrarSala(String nombre, TipoSala tipo, int filas, int asientosPorFila, int filasPreferenciales)
            throws DatosInvalidosException;

    Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase)
            throws DatosInvalidosException, ConflictoHorarioException;

    Funcion duplicarFuncion(Funcion original, LocalDateTime nuevaFechaHora)
            throws DatosInvalidosException, ConflictoHorarioException;

    List<Pelicula> listarPeliculas();

    List<Sala> listarSalas();

    List<Funcion> listarFunciones();
}
