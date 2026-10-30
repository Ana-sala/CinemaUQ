package co.edu.uniquindio.cinemauq.util;

import java.time.LocalDateTime;

/** Implementacion real del reloj: la hora del computador. */
public class RelojSistema implements Reloj {
    @Override
    public LocalDateTime ahora() {
        return LocalDateTime.now();
    }
}
