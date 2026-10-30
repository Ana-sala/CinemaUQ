package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.app.Navegador;
import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Promocion;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.servicio.GestionCartelera;
import co.edu.uniquindio.cinemauq.util.Formato;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pantalla del administrador: clientes y recargas, funciones, películas, confitería y estadísticas. */
public class AdminController {

    private final CinemaUQ cine = CinemaUQ.getInstance();
    private final GestionCartelera cartelera = cine.getCartelera();     // ISP: interfaz de gestión
    private Usuario admin;

    @FXML
    private Label lblAdmin;
    // --- Clientes y recargas ---
    @FXML
    private ListView<Cliente> lstClientes;
    @FXML
    private Label lblDetalleCliente;
    @FXML
    private ListView<Movimiento> lstMovimientosCliente;
    @FXML
    private ListView<Compra> lstComprasCliente;
    @FXML
    private TextField txtValorRecarga;
    @FXML
    private ListView<SolicitudRecarga> lstSolicitudes;
    // --- Funciones ---
    @FXML
    private ListView<Funcion> lstFuncionesAdmin;
    @FXML
    private ComboBox<Pelicula> cmbPelicula;
    @FXML
    private ComboBox<Sala> cmbSala;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtHora;
    @FXML
    private TextField txtPrecio;
    // --- Películas ---
    @FXML
    private ListView<Pelicula> lstPeliculasAdmin;
    @FXML
    private TextField txtTitulo;
    @FXML
    private ComboBox<Genero> cmbGenero;
    @FXML
    private ComboBox<Clasificacion> cmbClasificacion;
    @FXML
    private TextField txtDuracion;
    @FXML
    private TextArea txtSinopsis;
    // --- Confitería ---
    @FXML
    private ListView<ItemConfiteria> lstItems;
    @FXML
    private ListView<Promocion> lstPromociones;
    @FXML
    private TextField txtNombreProducto;
    @FXML
    private TextField txtPrecioProducto;
    @FXML
    private ComboBox<CategoriaProducto> cmbCategoria;
    // --- Estadísticas ---
    @FXML
    private TextArea txtEstadisticas;

    @FXML
    private void initialize() {
        admin = cine.getSesion().getUsuario();
        lblAdmin.setText("Administrador: " + admin.getNombre());

        lstClientes.getSelectionModel().selectedItemProperty().addListener((obs, antes, cliente) -> mostrarCliente(cliente));
        cmbGenero.setItems(FXCollections.observableArrayList(Genero.values()));
        cmbClasificacion.setItems(FXCollections.observableArrayList(Clasificacion.values()));
        cmbCategoria.setItems(FXCollections.observableArrayList(CategoriaProducto.values()));
        dpFecha.setValue(LocalDate.now().plusDays(1));

        refrescarClientes();
        refrescarFunciones();
        refrescarPeliculas();
        refrescarConfiteria();
        onActualizarEstadisticas();
    }

    // ===================== Clientes, tarjetas y recargas =====================

    private void refrescarClientes() {
        Cliente seleccionado = lstClientes.getSelectionModel().getSelectedItem();
        lstClientes.setItems(FXCollections.observableArrayList(cine.getAutenticacion().listarClientes()));
        lstSolicitudes.setItems(FXCollections.observableArrayList(cine.getTarjetas().solicitudesPendientes()));
        if (seleccionado != null) {
            lstClientes.getSelectionModel().select(seleccionado);
        }
        mostrarCliente(lstClientes.getSelectionModel().getSelectedItem());
    }

    /** RF-020: consultar un cliente, su tarjeta y sus compras. */
    private void mostrarCliente(Cliente cliente) {
        if (cliente == null) {
            lblDetalleCliente.setText("Seleccione un cliente.");
            lstMovimientosCliente.getItems().clear();
            lstComprasCliente.getItems().clear();
            return;
        }
        lblDetalleCliente.setText("Tarjeta " + cliente.getTarjeta().getNumero() + " · " + cliente.getTarjeta().getEstado()
                + " · saldo " + Formato.moneda(cliente.getTarjeta().getSaldo()) + " · puntos "
                + cine.getPuntos().disponibles(cliente));
        List<Movimiento> movimientos = new ArrayList<>(cliente.getTarjeta().getMovimientos());
        Collections.reverse(movimientos);
        lstMovimientosCliente.setItems(FXCollections.observableArrayList(movimientos));
        lstComprasCliente.setItems(FXCollections.observableArrayList(cine.getCompras().historialDe(cliente)));
    }

    /** RF-022: recarga directa (solo administrador, queda en el historial). */
    @FXML
    private void onRecargar() {
        Cliente cliente = lstClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            Dialogos.error("Seleccione un cliente.");
            return;
        }
        try {
            double valor = ClienteController.leerValor(txtValorRecarga.getText());
            cine.getTarjetas().recargar(admin, cliente, valor);
            txtValorRecarga.clear();
            refrescarClientes();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void onAprobarSolicitud() {
        SolicitudRecarga solicitud = lstSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            Dialogos.error("Seleccione una solicitud pendiente.");
            return;
        }
        try {
            cine.getTarjetas().aprobarSolicitud(admin, solicitud);
            refrescarClientes();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void onRechazarSolicitud() {
        SolicitudRecarga solicitud = lstSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            Dialogos.error("Seleccione una solicitud pendiente.");
            return;
        }
        try {
            cine.getTarjetas().rechazarSolicitud(admin, solicitud);
            refrescarClientes();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** RF-023: bloquear o activar la tarjeta del cliente seleccionado. */
    @FXML
    private void onBloquearTarjeta() {
        cambiarEstadoTarjeta(true);
    }

    @FXML
    private void onActivarTarjeta() {
        cambiarEstadoTarjeta(false);
    }

    private void cambiarEstadoTarjeta(boolean bloquear) {
        Cliente cliente = lstClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            Dialogos.error("Seleccione un cliente.");
            return;
        }
        try {
            if (bloquear) {
                cine.getTarjetas().bloquearTarjeta(admin, cliente);
            } else {
                cine.getTarjetas().activarTarjeta(admin, cliente);
            }
            refrescarClientes();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    // ===================== Funciones =====================

    private void refrescarFunciones() {
        lstFuncionesAdmin.setItems(FXCollections.observableArrayList(cartelera.listarFunciones()));
        cmbPelicula.setItems(FXCollections.observableArrayList(cartelera.listarPeliculas()));
        cmbSala.setItems(FXCollections.observableArrayList(cartelera.listarSalas()));
    }

    /** RF-017: programar una función nueva (valida cruces de horario). */
    @FXML
    private void onProgramarFuncion() {
        try {
            double precio = ClienteController.leerValor(txtPrecio.getText());
            cartelera.programarFuncion(cmbPelicula.getValue(), cmbSala.getValue(), leerFechaHora(), precio);
            refrescarFunciones();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** RF-017 con Prototype: copia la función seleccionada a la fecha y hora del formulario. */
    @FXML
    private void onDuplicarFuncion() {
        Funcion original = lstFuncionesAdmin.getSelectionModel().getSelectedItem();
        if (original == null) {
            Dialogos.error("Seleccione la función que desea duplicar.");
            return;
        }
        try {
            Funcion copia = cartelera.duplicarFuncion(original, leerFechaHora());
            Dialogos.info("Función duplicada", "Se creó " + copia.getId() + ": " + copia.getPelicula().getTitulo() + " · " + copia);
            refrescarFunciones();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** RF-021: cancelar la función y devolver el 100 % a todos sus compradores. */
    @FXML
    private void onCancelarFuncion() {
        Funcion funcion = lstFuncionesAdmin.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.error("Seleccione la función que desea cancelar.");
            return;
        }
        if (!Dialogos.confirmar("¿Cancelar la función?", funcion.getPelicula().getTitulo() + " · " + funcion
                + "\nSe devolverá el 100 % a todas las compras.")) {
            return;
        }
        try {
            List<Reembolso> reembolsos = cine.getCompras().cancelarFuncion(admin, funcion);
            double total = reembolsos.stream().mapToDouble(Reembolso::getValor).sum();
            Dialogos.info("Función cancelada", reembolsos.size() + " compras reembolsadas por " + Formato.moneda(total) + ".");
            refrescarFunciones();
            refrescarClientes();
            onActualizarEstadisticas();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    private LocalDateTime leerFechaHora() throws DatosInvalidosException {
        if (dpFecha.getValue() == null) {
            throw new DatosInvalidosException("Seleccione la fecha.");
        }
        try {
            return dpFecha.getValue().atTime(LocalTime.parse(txtHora.getText().trim()));
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("Escriba la hora en formato HH:mm, por ejemplo 19:30.");
        }
    }

    // ===================== Películas =====================

    private void refrescarPeliculas() {
        lstPeliculasAdmin.setItems(FXCollections.observableArrayList(cartelera.listarPeliculas()));
    }

    /** RF-015: registrar película. */
    @FXML
    private void onRegistrarPelicula() {
        try {
            int duracion = (int) ClienteController.leerValor(txtDuracion.getText());
            cartelera.registrarPelicula(txtTitulo.getText(), cmbGenero.getValue(), cmbClasificacion.getValue(), duracion,
                    txtSinopsis.getText());
            txtTitulo.clear();
            txtDuracion.clear();
            txtSinopsis.clear();
            refrescarPeliculas();
            refrescarFunciones();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void onCambiarCartelera() {
        Pelicula pelicula = lstPeliculasAdmin.getSelectionModel().getSelectedItem();
        if (pelicula == null) {
            Dialogos.error("Seleccione una película.");
            return;
        }
        cartelera.cambiarEstadoCartelera(pelicula, !pelicula.isEnCartelera());
        lstPeliculasAdmin.refresh();
    }

    // ===================== Confitería =====================

    private void refrescarConfiteria() {
        lstItems.setItems(FXCollections.observableArrayList(cine.getConfiteria().listarDisponibles()));
        lstPromociones.setItems(FXCollections.observableArrayList(cine.getConfiteria().listarPromociones()));
    }

    /** RF-018: registrar producto de confitería. */
    @FXML
    private void onRegistrarProducto() {
        try {
            double precio = ClienteController.leerValor(txtPrecioProducto.getText());
            cine.getConfiteria().registrarProducto(txtNombreProducto.getText(), precio, cmbCategoria.getValue());
            txtNombreProducto.clear();
            txtPrecioProducto.clear();
            refrescarConfiteria();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    // ===================== Estadísticas =====================

    /** RF-024: estadísticas básicas. */
    @FXML
    private void onActualizarEstadisticas() {
        txtEstadisticas.setText(cine.getEstadisticas().resumen());
    }

    @FXML
    private void onCerrarSesion() {
        cine.getSesion().cerrar();
        Navegador.ir(Navegador.LOGIN, "Iniciar sesión");
    }
}
