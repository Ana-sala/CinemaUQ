package co.edu.uniquindio.cinemauq.servicio;

/** Regla del grupo (RN-15): 1 punto por cada $1.000 pagados en dinero, redondeando hacia abajo. */
public class ReglaPorValorGastado implements ReglaAcumulacionPuntos {

    private final double pesosPorPunto;

    public ReglaPorValorGastado(double pesosPorPunto) {
        this.pesosPorPunto = pesosPorPunto;
    }

    @Override
    public int calcularPuntos(double valorPagadoEnDinero) {
        return (int) Math.floor(valorPagadoEnDinero / pesosPorPunto);
    }
}
