package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import javafx.application.Application;
import javafx.stage.Stage;

/** Aplicación JavaFX. Arranca en la pantalla de inicio de sesión. */
public class CinemaUQApp extends Application {

    @Override
    public void start(Stage stage) {
        CinemaUQ.getInstance();          // crea el cine y carga los datos iniciales
        Navegador.iniciar(stage);
        Navegador.ir(Navegador.LOGIN, "Iniciar sesión");
        stage.show();
    }
}
