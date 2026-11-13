package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.observador.MonitorOcupacion;
import co.edu.uniquindio.cinemauq.observador.NotificadorCliente;
import co.edu.uniquindio.cinemauq.observador.ObservadorCompras;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Observer: ServicioCompras avisa a todos los suscritos sin conocerlos. */
class ObservadorComprasTest {

    private final Escenario e = new Escenario();
    private final NotificadorCliente notificador = new NotificadorCliente();

    @Test
    void elClienteRecibeAvisoAlPagarYAlCancelar() throws Exception {
        e.servicioCompras.agregarObservador(notificador);
        Compra compra = e.servicioCompras.pagar(
                e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("A1")).build());
        assertEquals(1, notificador.notificacionesDe(e.cliente).size());
        assertTrue(notificador.notificacionesDe(e.cliente).get(0).mensaje().contains("confirmada"));

        e.hora = e.hora.plusMinutes(5);
        e.servicioCompras.cancelarCompra(e.cliente, compra);
        assertEquals(2, notificador.notificacionesDe(e.cliente).size());
        assertTrue(notificador.notificacionesDe(e.cliente).get(0).mensaje().contains("100 %"));   // la más reciente primero
    }

    @Test
    void todosLosSuscritosRecibenElAvisoYUnoQuitadoYaNo() throws Exception {
        List<String> registro = new ArrayList<>();
        ObservadorCompras contador = new ObservadorCompras() {
            public void compraPagada(Compra c) { registro.add("pago " + c.getId()); }
            public void compraCancelada(Compra c, Reembolso r) { registro.add("cancelación " + c.getId()); }
        };
        e.servicioCompras.agregarObservador(notificador);
        e.servicioCompras.agregarObservador(contador);
        e.servicioCompras.pagar(e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("A1")).build());
        assertEquals(1, registro.size());
        assertEquals(1, notificador.notificacionesDe(e.cliente).size());

        e.servicioCompras.quitarObservador(contador);
        e.servicioCompras.pagar(e.servicioCompras.nuevaCompra(e.cliente, e.funcion).agregarEntrada(e.silla("A2")).build());
        assertEquals(1, registro.size());                                     // ya no está suscrito
        assertEquals(2, notificador.notificacionesDe(e.cliente).size());
    }

    @Test
    void laAlertaDeOcupacionSaleUnaSolaVezAlLlegarAl80() throws Exception {
        MonitorOcupacion monitor = new MonitorOcupacion(80);
        e.servicioCompras.agregarObservador(monitor);
        e.tarjetas.recargar(e.admin, e.cliente, 400_000);
        List<Asiento> sillas = e.sala.getAsientos();                         // 30 sillas: el 80 % son 24
        venderDesde(sillas, 0, 10);
        venderDesde(sillas, 10, 20);
        venderDesde(sillas, 20, 23);
        assertTrue(monitor.getAlertas().isEmpty());                          // 23 sillas: 76,7 %
        venderDesde(sillas, 23, 24);
        assertEquals(1, monitor.getAlertas().size());                        // 24 sillas: 80 %
        venderDesde(sillas, 24, 26);
        assertEquals(1, monitor.getAlertas().size());                        // no se repite
        assertEquals(80.0, monitor.getAlertas().get(0).porcentaje(), 0.01);
    }

    private void venderDesde(List<Asiento> sillas, int desde, int hasta) throws Exception {
        CompraBuilder b = e.servicioCompras.nuevaCompra(e.cliente, e.funcion);
        for (int i = desde; i < hasta; i++) {
            b.agregarEntrada(sillas.get(i));
        }
        e.servicioCompras.pagar(b.build());
    }
}
