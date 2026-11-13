package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.observador.AlertaOcupacion;
import co.edu.uniquindio.cinemauq.reporte.ExportadorReporte;
import co.edu.uniquindio.cinemauq.reporte.ExportadorTxt;
import co.edu.uniquindio.cinemauq.reporte.Reporte;
import co.edu.uniquindio.cinemauq.reporte.ServicioReportes;
import co.edu.uniquindio.cinemauq.reporte.TipoReporte;
import co.edu.uniquindio.cinemauq.servicio.Periodo;
import co.edu.uniquindio.cinemauq.servicio.ServicioEstadisticas;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de la pestaña «Reportes y estadísticas» (MVC: Controller). Lee el periodo,
 * pide los datos a ServicioEstadisticas y ServicioReportes, y los muestra: indicadores,
 * gráficos (Graficos), vista previa y exportación con cualquier ExportadorReporte.
 */
public class ReportesController {

    private final CinemaUQ cine = CinemaUQ.getInstance();
    private final ServicioEstadisticas estadisticas = cine.getEstadisticas();
    private final ServicioReportes reportes = cine.getReportes();

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private Label lblPeriodo;
    @FXML
    private FlowPane fpIndicadores;
    @FXML
    private StackPane spLineas;
    @FXML
    private StackPane spEntradas;
    @FXML
    private StackPane spConfiteria;
    @FXML
    private StackPane spSalas;
    @FXML
    private ComboBox<TipoReporte> cmbReporte;
    @FXML
    private ComboBox<ExportadorReporte> cmbFormato;
    @FXML
    private TextArea txtVistaPrevia;
    @FXML
    private ListView<AlertaOcupacion> lstAlertas;

    @FXML
    private void initialize() {
        cmbReporte.setItems(FXCollections.observableArrayList(TipoReporte.values()));
        cmbReporte.getSelectionModel().selectFirst();
        cmbFormato.setItems(FXCollections.observableArrayList(reportes.getExportadores()));
        cmbFormato.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExportadorReporte e) {
                return e == null ? "" : e.descripcion();
            }

            @Override
            public ExportadorReporte fromString(String s) {
                return null;
            }
        });
        cmbFormato.getSelectionModel().select(1);   // PDF
        cmbReporte.valueProperty().addListener((obs, antes, ahora) -> onVistaPrevia());
        onUltimos30();
    }

    // ===================== Periodo =====================

    private LocalDate hoy() {
        return cine.getReloj().ahora().toLocalDate();
    }

    @FXML
    private void onUltimos7() {
        fijar(hoy().minusDays(6), hoy());
    }

    @FXML
    private void onUltimos30() {
        fijar(hoy().minusDays(29), hoy());
    }

    @FXML
    private void onTodo() {
        LocalDate primero = estadisticas.comprasCobradas(Periodo.todo()).stream()
                .map(c -> c.getFecha().toLocalDate()).findFirst().orElse(hoy());
        fijar(primero, hoy());
    }

    private void fijar(LocalDate desde, LocalDate hasta) {
        dpDesde.setValue(desde);
        dpHasta.setValue(hasta);
        onConsultar();
    }

    private Periodo periodo() throws DatosInvalidosException {
        return Periodo.de(dpDesde.getValue(), dpHasta.getValue());
    }

    @FXML
    private void onConsultar() {
        try {
            Periodo p = periodo();
            lblPeriodo.setText("Periodo: " + p + " · " + p.dias().size() + " días");
            mostrarIndicadores(p);
            mostrarGraficos(p);
            onVistaPrevia();
            lstAlertas.setItems(FXCollections.observableArrayList(alertasDe(p)));
        } catch (DatosInvalidosException e) {
            Dialogos.error(e.getMessage());
        }
    }

    // ===================== Indicadores y gráficos =====================

    private void mostrarIndicadores(Periodo p) {
        fpIndicadores.getChildren().clear();
        estadisticas.indicadores(p).forEach((nombre, valor) -> {
            Label lblValor = new Label(valor);
            lblValor.getStyleClass().add("indicador-valor");
            Label lblNombre = new Label(nombre);
            lblNombre.getStyleClass().add("indicador-nombre");
            VBox tarjeta = new VBox(4, lblValor, lblNombre);
            tarjeta.getStyleClass().add("indicador");
            fpIndicadores.getChildren().add(tarjeta);
        });
    }

    private void mostrarGraficos(Periodo p) {
        Map<String, Map<LocalDate, Double>> series = new LinkedHashMap<>();
        series.put("Ventas", estadisticas.ventasPorDia(p));
        series.put("Recargas", estadisticas.recargasPorDia(p));
        spLineas.getChildren().setAll(Graficos.lineas("Ventas y recargas por día", "Pesos", series));
        spEntradas.getChildren().setAll(Graficos.barras("Entradas vendidas por película", "Entradas", estadisticas.entradasPorPelicula(p)));
        spConfiteria.getChildren().setAll(Graficos.pastel("Confitería vendida (unidades)", estadisticas.confiteriaPorProducto(p), 6));
        spSalas.getChildren().setAll(Graficos.barras("Ocupación promedio por sala", "%", estadisticas.ocupacionPorSala(p)));
    }

    private List<AlertaOcupacion> alertasDe(Periodo p) {
        List<AlertaOcupacion> lista = new ArrayList<>(cine.getMonitorOcupacion().getAlertas().stream()
                .filter(a -> p.contiene(a.fecha())).toList());
        java.util.Collections.reverse(lista);
        return lista;
    }

    // ===================== Reportes =====================

    private Reporte reporteActual() throws DatosInvalidosException {
        return reportes.generar(cmbReporte.getValue(), periodo());
    }

    @FXML
    private void onVistaPrevia() {
        try {
            txtVistaPrevia.setText(new ExportadorTxt().formatear(reporteActual()));
        } catch (DatosInvalidosException e) {
            txtVistaPrevia.setText(e.getMessage());
        }
    }

    /** Exporta con el formato elegido: el controlador no sabe si es TXT o PDF (LSP). */
    @FXML
    private void onExportar() {
        try {
            Reporte reporte = reporteActual();
            ExportadorReporte exportador = cmbFormato.getValue();
            FileChooser selector = new FileChooser();
            selector.setTitle("Guardar reporte");
            selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(exportador.descripcion(), "*." + exportador.extension()));
            selector.setInitialFileName(nombreArchivo(reporte) + "." + exportador.extension());
            File archivo = selector.showSaveDialog(txtVistaPrevia.getScene().getWindow());
            if (archivo == null) {
                return;
            }
            exportador.exportar(reporte, archivo.toPath());
            Dialogos.info("Reporte exportado", "Se guardó " + archivo.getName() + " en " + archivo.getParent() + ".");
        } catch (DatosInvalidosException e) {
            Dialogos.error(e.getMessage());
        } catch (IOException e) {
            Dialogos.error("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    private static String nombreArchivo(Reporte r) {
        String base = java.text.Normalizer.normalize(r.getTitulo(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replaceAll("[^A-Za-z0-9]+", "_");
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMdd");
        return "CinemaUQ_" + base + "_" + f.format(r.getPeriodo().desde()) + "-" + f.format(r.getPeriodo().hasta());
    }
}
