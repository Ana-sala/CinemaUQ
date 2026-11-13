package co.edu.uniquindio.cinemauq.controlador;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Construye los gráficos de la pestaña de reportes a partir de mapas de datos.
 * No conoce compras ni funciones (SRP): solo sabe dibujar.
 */
final class Graficos {

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");

    private Graficos() {
    }

    /** Gráfico de líneas con una serie por cada entrada del mapa (por ejemplo ventas y recargas). */
    static LineChart<String, Number> lineas(String titulo, String ejeY, Map<String, Map<LocalDate, Double>> series) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        y.setLabel(ejeY);
        LineChart<String, Number> grafico = new LineChart<>(x, y);
        grafico.setTitle(titulo);
        grafico.setCreateSymbols(series.values().stream().findFirst().map(m -> m.size() <= 31).orElse(true));
        series.forEach((nombre, datos) -> {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(nombre);
            datos.forEach((dia, valor) -> serie.getData().add(new XYChart.Data<>(DIA.format(dia), valor)));
            grafico.getData().add(serie);
        });
        return grafico;
    }

    /** Gráfico de barras de una sola serie. */
    static BarChart<String, Number> barras(String titulo, String ejeY, Map<String, ? extends Number> datos) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        y.setLabel(ejeY);
        BarChart<String, Number> grafico = new BarChart<>(x, y);
        grafico.setTitle(titulo);
        grafico.setLegendVisible(false);
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        datos.forEach((nombre, valor) -> serie.getData().add(new XYChart.Data<>(nombre, valor)));
        grafico.getData().add(serie);
        return grafico;
    }

    /** Gráfico de pastel con las porciones más grandes y el resto agrupado en «Otros». */
    static PieChart pastel(String titulo, Map<String, Long> datos, int maxPorciones) {
        PieChart grafico = new PieChart();
        grafico.setTitle(titulo);
        grafico.setLabelsVisible(true);
        long otros = 0;
        int i = 0;
        for (Map.Entry<String, Long> e : datos.entrySet()) {
            if (i++ < maxPorciones) {
                grafico.getData().add(new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()));
            } else {
                otros += e.getValue();
            }
        }
        if (otros > 0) {
            grafico.getData().add(new PieChart.Data("Otros (" + otros + ")", otros));
        }
        return grafico;
    }
}
