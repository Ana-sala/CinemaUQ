package co.edu.uniquindio.cinemauq.util;

import java.time.LocalDateTime;

/**
 * Reloj que normalmente da la hora real, pero se puede fijar en otro momento.
 * DatosIniciales lo mueve al pasado para simular varios días de operación a través de
 * los servicios reales, y al terminar lo libera. Los servicios no notan la diferencia:
 * solo conocen la interfaz Reloj (DIP).
 */
public class RelojAjustable implements Reloj {

    private final Reloj real;
    private LocalDateTime fijado;

    public RelojAjustable(Reloj real) {
        this.real = real;
    }

    @Override
    public LocalDateTime ahora() {
        return fijado != null ? fijado : real.ahora();
    }

    /** Desde ahora el reloj marca esta hora, hasta que se llame a liberar(). */
    public void fijar(LocalDateTime momento) {
        this.fijado = momento;
    }

    /** Vuelve a la hora real. */
    public void liberar() {
        this.fijado = null;
    }

    public boolean estaFijado() {
        return fijado != null;
    }
}
