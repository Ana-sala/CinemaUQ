package co.edu.uniquindio.cinemauq.observador;

import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * OBSERVER · observador concreto: después de cada pago revisa la ocupación de la función
 * y genera una alerta para el administrador la primera vez que supera el umbral (80 %).
 */
public class MonitorOcupacion implements ObservadorCompras {

    private final double umbral;
    private final List<AlertaOcupacion> alertas = new ArrayList<>();
    private final Set<String> funcionesAlertadas = new HashSet<>();

    public MonitorOcupacion(double umbral) {
        this.umbral = umbral;
    }

    @Override
    public void compraPagada(Compra compra) {
        Funcion funcion = compra.getFuncion();
        double ocupacion = funcion.getPorcentajeOcupacion();
        if (ocupacion >= umbral && funcionesAlertadas.add(funcion.getId())) {   // una sola alerta por función
            alertas.add(new AlertaOcupacion(compra.getFecha(), funcion, ocupacion));
        }
    }

    @Override
    public void compraCancelada(Compra compra, Reembolso reembolso) {
        // una cancelación libera sillas: no genera alerta
    }

    public List<AlertaOcupacion> getAlertas() {
        return Collections.unmodifiableList(alertas);
    }

    public double getUmbral() {
        return umbral;
    }
}
