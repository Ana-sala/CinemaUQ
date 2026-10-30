package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Cuenta de puntos CinemaUQ de un cliente: una lista de lotes, cada uno con su fecha.
 * Redime primero los lotes que vencen antes (RN-05) y nunca usa puntos vencidos.
 */
public class CuentaPuntos {

    private final List<AcumulacionPuntos> acumulaciones = new ArrayList<>();

    public void acumular(int puntos, LocalDate fecha, String origen) throws DatosInvalidosException {
        if (puntos <= 0) {
            throw new DatosInvalidosException("Solo se acumulan cantidades positivas de puntos.");
        }
        acumulaciones.add(new AcumulacionPuntos(puntos, fecha, origen));
    }

    public int getDisponibles(LocalDate hoy) {
        return acumulaciones.stream().filter(a -> a.estaVigente(hoy)).mapToInt(AcumulacionPuntos::getPuntosDisponibles).sum();
    }

    /** Puntos vigentes que vencen en los próximos 'dias' días. */
    public int getPorVencer(LocalDate hoy, int dias) {
        return acumulaciones.stream()
                .filter(a -> a.estaVigente(hoy) && a.getFechaVencimiento().isBefore(hoy.plusDays(dias + 1L)))
                .mapToInt(AcumulacionPuntos::getPuntosDisponibles).sum();
    }

    public void verificarDisponibles(int puntos, LocalDate hoy) throws PuntosInsuficientesException {
        int disponibles = getDisponibles(hoy);
        if (puntos > disponibles) {
            throw new PuntosInsuficientesException("Se necesitan " + puntos + " puntos y tiene " + disponibles + " vigentes.");
        }
    }

    /** Redime 'puntos' empezando por los lotes vigentes más próximos a vencer. */
    public void redimir(int puntos, LocalDate hoy) throws PuntosInsuficientesException {
        verificarDisponibles(puntos, hoy);
        int faltan = puntos;
        List<AcumulacionPuntos> vigentes = acumulaciones.stream()
                .filter(a -> a.estaVigente(hoy) && a.getPuntosDisponibles() > 0)
                .sorted(Comparator.comparing(AcumulacionPuntos::getFechaVencimiento))
                .toList();
        for (AcumulacionPuntos lote : vigentes) {
            if (faltan == 0) {
                break;
            }
            faltan -= lote.consumir(faltan);
        }
    }

    /** Al cancelar una compra se anulan los puntos que esa compra generó y aún no se han usado (RN-14). */
    public int anularAcumulacion(String origen) {
        int anulados = 0;
        for (AcumulacionPuntos lote : acumulaciones) {
            if (lote.getOrigen().equals(origen)) {
                anulados += lote.getPuntosDisponibles();
                lote.anular();
            }
        }
        return anulados;
    }

    public List<AcumulacionPuntos> getAcumulaciones() {
        return Collections.unmodifiableList(acumulaciones);
    }
}
