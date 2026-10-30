package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.excepciones.TarjetaInactivaException;
import co.edu.uniquindio.cinemauq.modelo.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaVirtualTest {

    private final LocalDateTime ahora = LocalDateTime.of(2026, 11, 1, 12, 0);

    @Test
    void pagoDescuentaSaldoYRegistraMovimiento() throws Exception {
        TarjetaVirtual tarjeta = new TarjetaVirtual("TV-1");
        tarjeta.acreditar(TipoMovimiento.RECARGA, 50_000, "Recarga", ahora);
        tarjeta.debitar(20_000, "Compra", ahora);
        assertEquals(30_000, tarjeta.getSaldo());
        assertEquals(2, tarjeta.getMovimientos().size());
        assertEquals(TipoMovimiento.PAGO, tarjeta.getMovimientos().get(1).getTipo());
    }

    @Test
    void sinSaldoSuficienteNoPagaNiCambiaElSaldo() throws Exception {
        TarjetaVirtual tarjeta = new TarjetaVirtual("TV-1");
        tarjeta.acreditar(TipoMovimiento.RECARGA, 10_000, "Recarga", ahora);
        assertThrows(SaldoInsuficienteException.class, () -> tarjeta.debitar(15_000, "Compra", ahora));
        assertEquals(10_000, tarjeta.getSaldo());
    }

    @Test
    void tarjetaBloqueadaNoPagaPeroSiRecibeReembolsos() throws Exception {
        TarjetaVirtual tarjeta = new TarjetaVirtual("TV-1");
        tarjeta.acreditar(TipoMovimiento.RECARGA, 10_000, "Recarga", ahora);
        tarjeta.bloquear();
        assertThrows(TarjetaInactivaException.class, () -> tarjeta.debitar(1_000, "Compra", ahora));
        tarjeta.acreditar(TipoMovimiento.REEMBOLSO, 5_000, "Reembolso", ahora);
        assertEquals(15_000, tarjeta.getSaldo());
    }
}
