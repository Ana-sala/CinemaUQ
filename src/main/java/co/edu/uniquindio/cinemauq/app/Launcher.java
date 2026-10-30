package co.edu.uniquindio.cinemauq.app;

import javafx.application.Application;

/**
 * Clase para ejecutar desde IntelliJ (Run 'Launcher.main()').
 * Separar el main de la clase que extiende Application evita el error
 * "JavaFX runtime components are missing" cuando JavaFX está en el classpath.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(CinemaUQApp.class, args);
    }
}
