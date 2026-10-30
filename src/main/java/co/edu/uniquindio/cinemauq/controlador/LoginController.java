package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.app.Navegador;
import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.CredencialesInvalidasException;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.modelo.enums.Rol;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/** RF-002: inicio de sesión. Envía a cada perfil a su pantalla. */
public class LoginController {

    private final CinemaUQ cine = CinemaUQ.getInstance();

    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private Label lblMensaje;

    @FXML
    private void onIngresar() {
        try {
            Usuario usuario = cine.getAutenticacion().iniciarSesion(txtCorreo.getText(), txtContrasena.getText());
            cine.getSesion().iniciar(usuario);
            if (usuario.getRol() == Rol.ADMINISTRADOR) {
                Navegador.ir(Navegador.ADMIN, "Administración");
            } else {
                Navegador.ir(Navegador.CLIENTE, "Cliente");
            }
        } catch (CredencialesInvalidasException e) {
            lblMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void onIrARegistro() {
        Navegador.ir(Navegador.REGISTRO, "Registro");
    }
}
