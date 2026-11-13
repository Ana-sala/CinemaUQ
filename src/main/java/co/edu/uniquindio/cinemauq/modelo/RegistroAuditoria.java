package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.time.LocalDateTime;

/** Una operación de administración que alguien intentó: quién, qué, cuándo y si se permitió. */
public record RegistroAuditoria(LocalDateTime fecha, String usuario, String operacion, boolean permitida) {

    @Override
    public String toString() {
        return Formato.fechaHora(fecha) + " · " + usuario + " · " + operacion + " · "
                + (permitida ? "permitida" : "DENEGADA");
    }
}
