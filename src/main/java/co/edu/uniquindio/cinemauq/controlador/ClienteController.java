package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.app.Navegador;
import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoAsiento;
import co.edu.uniquindio.cinemauq.servicio.ConsultaCartelera;
import co.edu.uniquindio.cinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Pantalla del cliente: cartelera, selección gráfica de sillas, confitería, compra,
 * tarjeta, historial y puntos. El controlador solo traduce clics a llamadas de servicio (MVC).
 */
public class ClienteController {

    /** Lo que el cliente va agregando de confitería antes de comprar. */
    private record SeleccionConfiteria(ItemConfiteria item, int cantidad, boolean conPuntos) {
    }

    private final CinemaUQ cine = CinemaUQ.getInstance();
    private final ConsultaCartelera cartelera = cine.getCartelera();   // ISP: solo consulta
    private Cliente cliente;
    private Funcion funcionActual;
    private final Set<Asiento> seleccionados = new LinkedHashSet<>();
    private final List<SeleccionConfiteria> confiteria = new ArrayList<>();

    @FXML
    private Label lblBienvenida;
    // --- Cartelera y compra ---
    @FXML
    private ListView<Pelicula> lstPeliculas;
    @FXML
    private Label lblSinopsis;
    @FXML
    private ListView<Funcion> lstFunciones;
    @FXML
    private GridPane gridAsientos;
    @FXML
    private CheckBox chkEntradasConPuntos;
    @FXML
    private ComboBox<ItemConfiteria> cmbConfiteria;
    @FXML
    private Spinner<Integer> spnCantidad;
    @FXML
    private CheckBox chkConfiteriaConPuntos;
    @FXML
    private ListView<String> lstResumen;
    @FXML
    private Label lblTotal;
    // --- Tarjeta ---
    @FXML
    private Label lblTarjeta;
    @FXML
    private Label lblSaldo;
    @FXML
    private ListView<Movimiento> lstMovimientos;
    @FXML
    private TextField txtValorRecarga;
    @FXML
    private ListView<SolicitudRecarga> lstSolicitudes;
    // --- Compras ---
    @FXML
    private ListView<Compra> lstCompras;
    @FXML
    private TextArea txtDetalleCompra;
    // --- Puntos ---
    @FXML
    private Label lblPuntos;
    @FXML
    private Label lblPorVencer;
    @FXML
    private ListView<AcumulacionPuntos> lstAcumulaciones;
    @FXML
    private Label lblEquivalencia;

    @FXML
    private void initialize() {
        cliente = cine.getSesion().getCliente();
        lblBienvenida.setText("Hola, " + cliente.getNombre());

        lstPeliculas.setItems(FXCollections.observableArrayList(cartelera.peliculasEnCartelera()));
        lstPeliculas.getSelectionModel().selectedItemProperty().addListener((obs, antes, pelicula) -> mostrarFunciones(pelicula));
        lstFunciones.getSelectionModel().selectedItemProperty().addListener((obs, antes, funcion) -> seleccionarFuncion(funcion));

        cmbConfiteria.setItems(FXCollections.observableArrayList(cine.getConfiteria().listarDisponibles()));
        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        chkEntradasConPuntos.selectedProperty().addListener((obs, antes, ahora) -> actualizarResumen());

        lstCompras.getSelectionModel().selectedItemProperty().addListener((obs, antes, compra) -> mostrarDetalle(compra));
        lblEquivalencia.setText("Gana 1 punto por cada $1.000 pagados. Al redimir, 1 punto vale $100 "
                + "(ej.: una entrada de $14.000 = 140 puntos). Los puntos vencen al año.");

        actualizarResumen();
        refrescarTarjeta();
        refrescarCompras();
        refrescarPuntos();
    }

    // ===================== Cartelera y selección de sillas =====================

    private void mostrarFunciones(Pelicula pelicula) {
        if (pelicula == null) {
            return;
        }
        lblSinopsis.setText(pelicula.getSinopsis());
        lstFunciones.setItems(FXCollections.observableArrayList(cartelera.funcionesDisponibles(pelicula)));
        seleccionarFuncion(null);
    }

    private void seleccionarFuncion(Funcion funcion) {
        funcionActual = funcion;
        seleccionados.clear();
        dibujarAsientos();
        actualizarResumen();
    }

    /** RF-005: mapa de la sala. Gris = libre, dorado = preferencial, verde = elegida, rojo = vendida. */
    private void dibujarAsientos() {
        gridAsientos.getChildren().clear();
        if (funcionActual == null) {
            return;
        }
        Sala sala = funcionActual.getSala();
        for (Asiento asiento : sala.getAsientos()) {
            int fila = asiento.getFila() - 'A';
            if (asiento.getNumero() == 1) {
                Label letra = new Label(String.valueOf(asiento.getFila()));
                letra.getStyleClass().add("fila-asientos");
                gridAsientos.add(letra, 0, fila);
            }
            Button boton = new Button(asiento.getCodigo());
            boton.getStyleClass().add("asiento");
            if (!funcionActual.estaDisponible(asiento)) {
                boton.getStyleClass().add("asiento-ocupado");
                boton.setDisable(true);
            } else if (seleccionados.contains(asiento)) {
                boton.getStyleClass().add("asiento-seleccionado");
            } else if (asiento.getTipo() == TipoAsiento.PREFERENCIAL) {
                boton.getStyleClass().add("asiento-preferencial");
            }
            boton.setTooltip(new Tooltip(asiento.getTipo() == TipoAsiento.PREFERENCIAL ? "Preferencial (+30 %)" : "General"));
            boton.setOnAction(evento -> alternarAsiento(asiento));
            gridAsientos.add(boton, asiento.getNumero(), fila);
        }
    }

    private void alternarAsiento(Asiento asiento) {
        if (!seleccionados.remove(asiento)) {
            seleccionados.add(asiento);
        }
        dibujarAsientos();
        actualizarResumen();
    }

    // ===================== Confitería y resumen =====================

    @FXML
    private void onAgregarConfiteria() {
        ItemConfiteria item = cmbConfiteria.getValue();
        if (item == null) {
            Dialogos.error("Seleccione un producto o combo.");
            return;
        }
        confiteria.add(new SeleccionConfiteria(item, spnCantidad.getValue(), chkConfiteriaConPuntos.isSelected()));
        actualizarResumen();
    }

    @FXML
    private void onLimpiarCompra() {
        seleccionados.clear();
        confiteria.clear();
        chkEntradasConPuntos.setSelected(false);
        dibujarAsientos();
        actualizarResumen();
    }

    /** Arma la compra con el Builder a partir de lo elegido en pantalla. */
    private CompraBuilder construirCompra() throws CinemaUQException {
        CompraBuilder builder = cine.getCompras().nuevaCompra(cliente, funcionActual);
        for (Asiento asiento : seleccionados) {
            if (chkEntradasConPuntos.isSelected()) {
                builder.agregarEntradaConPuntos(asiento);
            } else {
                builder.agregarEntrada(asiento);
            }
        }
        for (SeleccionConfiteria s : confiteria) {
            if (s.conPuntos()) {
                builder.agregarConfiteriaConPuntos(s.item(), s.cantidad());
            } else {
                builder.agregarConfiteria(s.item(), s.cantidad());
            }
        }
        return builder;
    }

    private void actualizarResumen() {
        lstResumen.getItems().clear();
        if (funcionActual == null) {
            lblTotal.setText("Elija una película y una función.");
            return;
        }
        try {
            CompraBuilder builder = construirCompra();
            for (Entrada entrada : builder.getEntradas()) {
                lstResumen.getItems().add(entrada.toString());
            }
            for (LineaConfiteria linea : builder.getLineas()) {
                lstResumen.getItems().add(linea.toString());
            }
            lblTotal.setText("A pagar: " + Formato.moneda(builder.getValorAPagar()) + "   ·   Puntos a usar: "
                    + builder.getPuntosNecesarios() + " de " + cine.getPuntos().disponibles(cliente) + " disponibles");
        } catch (CinemaUQException e) {
            lblTotal.setText(e.getMessage());
        }
    }

    /** RF-007, RF-008 y RF-014: confirma, paga con la tarjeta y redime puntos si se marcaron. */
    @FXML
    private void onComprar() {
        if (funcionActual == null) {
            Dialogos.error("Elija primero una función.");
            return;
        }
        try {
            Compra compra = construirCompra().build();
            String mensaje = "Película: " + funcionActual.getPelicula().getTitulo() + "\nFunción: " + funcionActual
                    + "\nTotal a pagar con la tarjeta: " + Formato.moneda(compra.getValorAPagar())
                    + (compra.getPuntosRedimidos() > 0 ? "\nPuntos a redimir: " + compra.getPuntosRedimidos() : "");
            if (!Dialogos.confirmar("¿Confirmar la compra?", mensaje)) {
                return;
            }
            cine.getCompras().pagar(compra);
            Dialogos.info("Compra exitosa", "Compra " + compra.getId() + " pagada.\nGanó " + compra.getPuntosGanados()
                    + " puntos. Saldo actual: " + Formato.moneda(cliente.getTarjeta().getSaldo()));
            confiteria.clear();
            seleccionados.clear();
            lstFunciones.refresh();
            dibujarAsientos();
            actualizarResumen();
            refrescarTarjeta();
            refrescarCompras();
            refrescarPuntos();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    // ===================== Tarjeta =====================

    private void refrescarTarjeta() {
        lblTarjeta.setText("Tarjeta " + cliente.getTarjeta().getNumero() + " · " + cliente.getTarjeta().getEstado());
        lblSaldo.setText("Saldo: " + Formato.moneda(cliente.getTarjeta().getSaldo()));
        List<Movimiento> movimientos = new ArrayList<>(cliente.getTarjeta().getMovimientos());
        Collections.reverse(movimientos);
        lstMovimientos.setItems(FXCollections.observableArrayList(movimientos));
        lstSolicitudes.setItems(FXCollections.observableArrayList(cine.getTarjetas().solicitudesDe(cliente)));
    }

    @FXML
    private void onSolicitarRecarga() {
        try {
            double valor = leerValor(txtValorRecarga.getText());
            cine.getTarjetas().solicitarRecarga(cliente, valor);
            txtValorRecarga.clear();
            Dialogos.info("Solicitud enviada", "Un administrador revisará su solicitud de recarga por " + Formato.moneda(valor) + ".");
            refrescarTarjeta();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    // ===================== Compras =====================

    private void refrescarCompras() {
        lstCompras.setItems(FXCollections.observableArrayList(cine.getCompras().historialDe(cliente)));
        txtDetalleCompra.clear();
    }

    private void mostrarDetalle(Compra compra) {
        if (compra == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(compra.getFuncion().getPelicula().getTitulo()).append('\n').append(compra.getFuncion()).append("\n\n");
        compra.getEntradas().forEach(e -> sb.append("  ").append(e).append('\n'));
        compra.getLineas().forEach(l -> sb.append("  ").append(l).append('\n'));
        sb.append("\nPagado: ").append(Formato.moneda(compra.getValorPagado()))
                .append("\nPuntos redimidos: ").append(compra.getPuntosRedimidos())
                .append("\nPuntos ganados: ").append(compra.getPuntosGanados())
                .append("\nEstado: ").append(compra.getEstado());
        if (compra.getReembolso() != null) {
            Reembolso r = compra.getReembolso();
            sb.append("\nReembolso: ").append(r.getPorcentaje()).append("% = ").append(Formato.moneda(r.getValor()))
                    .append(" (").append(r.getMotivo()).append(')');
        }
        txtDetalleCompra.setText(sb.toString());
    }

    /** RF-012: cancelación con la política de tiempo. */
    @FXML
    private void onCancelarCompra() {
        Compra compra = lstCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            Dialogos.error("Seleccione la compra que desea cancelar.");
            return;
        }
        if (!Dialogos.confirmar("¿Cancelar la compra " + compra.getId() + "?",
                "Más de 24 h antes: se devuelve el 100 %. Entre 2 y 24 h: el 80 %. Con menos de 2 h no se puede cancelar.")) {
            return;
        }
        try {
            Reembolso reembolso = cine.getCompras().cancelarCompra(cliente, compra);
            Dialogos.info("Compra cancelada", "Se devolvieron " + Formato.moneda(reembolso.getValor()) + " ("
                    + reembolso.getPorcentaje() + "%) a su tarjeta virtual.");
            refrescarTarjeta();
            refrescarCompras();
            refrescarPuntos();
            lstFunciones.refresh();
            dibujarAsientos();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    // ===================== Puntos =====================

    private void refrescarPuntos() {
        lblPuntos.setText("Puntos disponibles: " + cine.getPuntos().disponibles(cliente));
        lblPorVencer.setText("Vencen en los próximos 30 días: " + cine.getPuntos().porVencer(cliente));
        lstAcumulaciones.setItems(FXCollections.observableArrayList(cine.getPuntos().historial(cliente)));
    }

    @FXML
    private void onCerrarSesion() {
        cine.getSesion().cerrar();
        Navegador.ir(Navegador.LOGIN, "Iniciar sesión");
    }

    static double leerValor(String texto) throws DatosInvalidosException {
        String digitos = texto == null ? "" : texto.replaceAll("[^0-9]", "");
        if (digitos.isEmpty()) {
            throw new DatosInvalidosException("Escriba un valor numérico, por ejemplo 50000.");
        }
        return Double.parseDouble(digitos);
    }
}
