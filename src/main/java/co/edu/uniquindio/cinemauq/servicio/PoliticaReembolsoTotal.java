package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;

import java.time.LocalDateTime;

/**
 * STRATEGY · estrategia concreta para cuando el cine cancela la función (RF-021):
 * se devuelve el 100 % sin importar el tiempo que falte.
 */
public class PoliticaReembolsoTotal implements PoliticaReembolso {

    @Override
    public int porcentajeReembolso(LocalDateTime inicioFuncion, LocalDateTime ahora) {
        return 100;
    }

    @Override
    public MotivoReembolso motivo() {
        return MotivoReembolso.FUNCION_CANCELADA;
    }

    @Override
    public String descripcion() {
        return "Función cancelada por el cine: 100 %";
    }
}
