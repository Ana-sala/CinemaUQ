package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.AcumulacionPuntos;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.util.Comparator;
import java.util.List;

/** Consulta de puntos CinemaUQ (RF-013). La redención ocurre dentro de la compra. */
public class ServicioPuntos {

    public static final int DIAS_AVISO_VENCIMIENTO = 30;

    private final EquivalenciaPuntos equivalencia;
    private final Reloj reloj;

    public ServicioPuntos(EquivalenciaPuntos equivalencia, Reloj reloj) {
        this.equivalencia = equivalencia;
        this.reloj = reloj;
    }

    public int disponibles(Cliente cliente) {
        return cliente.getCuentaPuntos().getDisponibles(reloj.ahora().toLocalDate());
    }

    public int porVencer(Cliente cliente) {
        return cliente.getCuentaPuntos().getPorVencer(reloj.ahora().toLocalDate(), DIAS_AVISO_VENCIMIENTO);
    }

    /** Lotes ordenados por vencimiento: el primero es el que se redime primero. */
    public List<AcumulacionPuntos> historial(Cliente cliente) {
        return cliente.getCuentaPuntos().getAcumulaciones().stream()
                .sorted(Comparator.comparing(AcumulacionPuntos::getFechaVencimiento)).toList();
    }

    public int puntosPara(double precio) {
        return equivalencia.puntosPara(precio);
    }
}
