package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;

/** Película que el cine puede proyectar. */
public class Pelicula implements Identificable {

    private final String id;
    private String titulo;
    private Genero genero;
    private Clasificacion clasificacion;
    private int duracionMinutos;
    private String sinopsis;
    private boolean enCartelera = true;

    public Pelicula(String id, String titulo, Genero genero, Clasificacion clasificacion,
                    int duracionMinutos, String sinopsis) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacion = clasificacion;
        this.duracionMinutos = duracionMinutos;
        this.sinopsis = sinopsis;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }

    @Override
    public String toString() {
        return titulo + " · " + genero + " · " + clasificacion + " · " + duracionMinutos + " min"
                + (enCartelera ? "" : " (fuera de cartelera)");
    }
}
