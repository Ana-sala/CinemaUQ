package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.app.Navegador;
import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** RF-001: registro de clientes. Las validaciones viven en el servicio, no aquí. */
public class RegistroController {

    private final CinemaUQ cine = CinemaUQ.getInstance();

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDocumento;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private PasswordField txtConfirmar;
    @FXML
    private Label lblMensaje;

    @FXML
    private void onRegistrar() {
        if (!txtContrasena.getText().equals(txtConfirmar.getText())) {
            lblMensaje.setText("Las contraseñas no coinciden.");
            return;
        }
        try {
            Cliente cliente = cine.getAutenticacion().registrarCliente(txtNombre.getText(), txtDocumento.getText(),
                    txtCorreo.getText(), txtContrasena.getText());
            Dialogos.info("Cuenta creada", "Bienvenido(a), " + cliente.getNombre() + ".\nSu tarjeta virtual "
                    + cliente.getTarjeta().getNumero() + " está activa con saldo $0. Ya puede iniciar sesión.");
            Navegador.ir(Navegador.LOGIN, "Iniciar sesión");
        } catch (CinemaUQException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        Navegador.ir(Navegador.LOGIN, "Iniciar sesión");
    }
}
