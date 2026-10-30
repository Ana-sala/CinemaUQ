package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.ConflictoHorarioException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoFuncion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.GeneradorId;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** Películas, salas y funciones. Implementa las dos interfaces de cartelera. */
public class ServicioCartelera implements ConsultaCartelera, GestionCartelera {

    /** Minutos de limpieza entre dos funciones de la misma sala (RN-12). */
    public static final int MINUTOS_LIMPIEZA = 20;

    private final Repositorio<Pelicula> peliculas;
    private final Repositorio<Sala> salas;
    private final Repositorio<Funcion> funciones;
    private final Reloj reloj;

    public ServicioCartelera(Repositorio<Pelicula> peliculas, Repositorio<Sala> salas, Repositorio<Funcion> funciones,
                             Reloj reloj) {
        this.peliculas = peliculas;
        this.salas = salas;
        this.funciones = funciones;
        this.reloj = reloj;
    }

    // ---------- Consulta (cliente) ----------

    @Override
    public List<Pelicula> peliculasEnCartelera() {
        return peliculas.buscar(Pelicula::isEnCartelera);
    }

    @Override
    public List<Funcion> funcionesDisponibles(Pelicula pelicula) {
        LocalDateTime ahora = reloj.ahora();
        return funciones.buscar(f -> f.getPelicula().equals(pelicula) && f.admiteVentas(ahora)).stream()
                .sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
    }

    // ---------- Gestión (administrador) ----------

    @Override
    public Pelicula registrarPelicula(String titulo, Genero genero, Clasificacion clasificacion, int duracionMinutos,
                                      String sinopsis) throws DatosInvalidosException {
        if (titulo == null || titulo.isBlank()) {
            throw new DatosInvalidosException("El título es obligatorio.");
        }
        if (genero == null || clasificacion == null) {
            throw new DatosInvalidosException("Seleccione género y clasificación.");
        }
        if (duracionMinutos < 30 || duracionMinutos > 300) {
            throw new DatosInvalidosException("La duración debe estar entre 30 y 300 minutos.");
        }
        Pelicula pelicula = new Pelicula(GeneradorId.siguiente("PEL"), titulo.trim(), genero, clasificacion,
                duracionMinutos, sinopsis == null ? "" : sinopsis.trim());
        peliculas.guardar(pelicula);
        return pelicula;
    }

    @Override
    public void cambiarEstadoCartelera(Pelicula pelicula, boolean enCartelera) {
        pelicula.setEnCartelera(enCartelera);
    }

    @Override
    public Sala registrarSala(String nombre, TipoSala tipo, int filas, int asientosPorFila, int filasPreferenciales)
            throws DatosInvalidosException {
        if (filas < 1 || filas > 26 || asientosPorFila < 1 || asientosPorFila > 30) {
            throw new DatosInvalidosException("La sala debe tener entre 1 y 26 filas y entre 1 y 30 sillas por fila.");
        }
        if (filasPreferenciales < 0 || filasPreferenciales > filas) {
            throw new DatosInvalidosException("Filas preferenciales fuera de rango.");
        }
        Sala sala = new Sala(GeneradorId.siguiente("SAL"), nombre, tipo, filas, asientosPorFila, filasPreferenciales);
        salas.guardar(sala);
        return sala;
    }

    @Override
    public Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase)
            throws DatosInvalidosException, ConflictoHorarioException {
        Funcion funcion = new Funcion(GeneradorId.siguiente("FUN"), pelicula, sala, fechaHora, precioBase);
        return guardarFuncion(funcion);
    }

    /** Usa el Prototype de Funcion: copia la configuración y cambia solo la fecha. */
    @Override
    public Funcion duplicarFuncion(Funcion original, LocalDateTime nuevaFechaHora)
            throws DatosInvalidosException, ConflictoHorarioException {
        return guardarFuncion(original.clonarPara(nuevaFechaHora));
    }

    private Funcion guardarFuncion(Funcion nueva) throws DatosInvalidosException, ConflictoHorarioException {
        if (nueva.getPelicula() == null || nueva.getSala() == null || nueva.getFechaHora() == null) {
            throw new DatosInvalidosException("Seleccione película, sala, fecha y hora.");
        }
        if (nueva.getPrecioBase() <= 0) {
            throw new DatosInvalidosException("El precio base debe ser mayor que cero.");
        }
        if (!nueva.getFechaHora().isAfter(reloj.ahora())) {
            throw new DatosInvalidosException("No se pueden programar funciones en el pasado.");
        }
        for (Funcion existente : funciones.buscar(f -> f.getSala().equals(nueva.getSala())
                && f.getEstado() == EstadoFuncion.PROGRAMADA)) {
            if (seCruzan(existente, nueva)) {
                throw new ConflictoHorarioException("La " + nueva.getSala().getNombre() + " está ocupada por "
                        + existente.getPelicula().getTitulo() + " (" + existente + ").");
            }
        }
        funciones.guardar(nueva);
        return nueva;
    }

    private boolean seCruzan(Funcion a, Funcion b) {
        LocalDateTime finA = a.getFin().plusMinutes(MINUTOS_LIMPIEZA);
        LocalDateTime finB = b.getFin().plusMinutes(MINUTOS_LIMPIEZA);
        return a.getFechaHora().isBefore(finB) && b.getFechaHora().isBefore(finA);
    }

    @Override
    public List<Pelicula> listarPeliculas() {
        return peliculas.listar();
    }

    @Override
    public List<Sala> listarSalas() {
        return salas.listar();
    }

    @Override
    public List<Funcion> listarFunciones() {
        return funciones.listar().stream().sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
    }
}
