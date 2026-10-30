package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;

import java.util.List;

/**
 * Lo que el CLIENTE necesita de la cartelera: solo consultar (ISP).
 * La pantalla del cliente depende de esta interfaz y no ve métodos de gestión.
 */
public interface ConsultaCartelera {

    List<Pelicula> peliculasEnCartelera();

    List<Funcion> funcionesDisponibles(Pelicula pelicula);
}
