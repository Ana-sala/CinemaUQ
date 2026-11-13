package co.edu.uniquindio.cinemauq.reporte;

import co.edu.uniquindio.cinemauq.servicio.Periodo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contenido de un reporte, independiente del formato: título, periodo, fecha de
 * generación, una tabla y unos totales. Los exportadores deciden cómo se escribe.
 */
public class Reporte {

    private final String titulo;
    private final Periodo periodo;
    private final LocalDateTime generado;
    private final List<String> columnas = new ArrayList<>();
    private final List<Boolean> numericas = new ArrayList<>();
    private final List<List<String>> filas = new ArrayList<>();
    private final List<String[]> totales = new ArrayList<>();

    public Reporte(String titulo, Periodo periodo, LocalDateTime generado) {
        this.titulo = titulo;
        this.periodo = periodo;
        this.generado = generado;
    }

    /** Agrega una columna; las numéricas se alinean a la derecha. */
    public Reporte columna(String nombre, boolean numerica) {
        columnas.add(nombre);
        numericas.add(numerica);
        return this;
    }

    public Reporte fila(String... valores) {
        if (valores.length != columnas.size()) {
            throw new IllegalArgumentException("La fila tiene " + valores.length + " valores y hay " + columnas.size() + " columnas.");
        }
        filas.add(List.of(valores));
        return this;
    }

    public Reporte total(String nombre, String valor) {
        totales.add(new String[]{nombre, valor});
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public LocalDateTime getGenerado() {
        return generado;
    }

    public List<String> getColumnas() {
        return Collections.unmodifiableList(columnas);
    }

    public boolean esNumerica(int columna) {
        return numericas.get(columna);
    }

    public List<List<String>> getFilas() {
        return Collections.unmodifiableList(filas);
    }

    public List<String[]> getTotales() {
        return Collections.unmodifiableList(totales);
    }
}
