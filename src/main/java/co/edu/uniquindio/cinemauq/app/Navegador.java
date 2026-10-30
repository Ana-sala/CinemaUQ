package co.edu.uniquindio.cinemauq.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/** Cambia la vista (archivo FXML) que muestra la ventana principal. */
public final class Navegador {

    public static final String LOGIN = "login-view.fxml";
    public static final String REGISTRO = "registro-view.fxml";
    public static final String CLIENTE = "cliente-view.fxml";
    public static final String ADMIN = "admin-view.fxml";

    private static final String RUTA = "/co/edu/uniquindio/cinemauq/vista/";
    private static Stage ventana;

    private Navegador() {
    }

    static void iniciar(Stage stage) {
        ventana = stage;
    }

    public static void ir(String fxml, String titulo) {
        try {
            URL url = Navegador.class.getResource(RUTA + fxml);
            Parent raiz = FXMLLoader.load(url);
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(Navegador.class.getResource(RUTA + "estilos.css").toExternalForm());
            ventana.setScene(escena);
            ventana.setTitle("CinemaUQ · " + titulo);
            ventana.centerOnScreen();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo abrir la vista " + fxml, e);
        }
    }
}
