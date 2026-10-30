package co.edu.uniquindio.cinemauq.controlador;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/** Ventanas emergentes reutilizadas por todos los controladores. */
final class Dialogos {

    private Dialogos() {
    }

    static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "No se pudo completar la operación", mensaje);
    }

    static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(titulo);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(titulo);
        alerta.showAndWait();
    }
}
