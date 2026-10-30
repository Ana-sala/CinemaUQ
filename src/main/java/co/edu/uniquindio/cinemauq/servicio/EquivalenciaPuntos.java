package co.edu.uniquindio.cinemauq.servicio;

/**
 * Equivalencia para redimir (RN-16): 1 punto vale $100.
 * Una entrada de $14.000 cuesta 140 puntos; un combo de $25.000, 250 puntos.
 */
public class EquivalenciaPuntos {

    private final double pesosPorPunto;

    public EquivalenciaPuntos(double pesosPorPunto) {
        this.pesosPorPunto = pesosPorPunto;
    }

    public int puntosPara(double precio) {
        return (int) Math.ceil(precio / pesosPorPunto);
    }

    public double getPesosPorPunto() {
        return pesosPorPunto;
    }
}
