package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.contexto.Sesion;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.proxy.Auditoria;
import co.edu.uniquindio.cinemauq.proxy.GestionCarteleraProxy;
import co.edu.uniquindio.cinemauq.servicio.GestionCartelera;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Proxy: protege las operaciones de administración y las deja registradas. */
class GestionCarteleraProxyTest {

    private final Escenario e = new Escenario();
    private final Sesion sesion = new Sesion();
    private final Auditoria auditoria = new Auditoria();
    private final GestionCartelera proxy = new GestionCarteleraProxy(e.cartelera, sesion, auditoria, e.reloj);

    @Test
    void unClienteNoPuedeModificarLaCarteleraYQuedaRegistrado() {
        sesion.iniciar(e.cliente);
        int antes = e.cartelera.listarPeliculas().size();
        assertThrows(OperacionNoAutorizadaException.class,
                () -> proxy.registrarPelicula("Intrusa", Genero.DRAMA, Clasificacion.TODO_PUBLICO, 90, ""));
        assertEquals(antes, e.cartelera.listarPeliculas().size());           // el real ni se enteró
        assertFalse(auditoria.listar().get(0).permitida());
    }

    @Test
    void elAdministradorPasaYSuOperacionQuedaEnLaAuditoria() throws Exception {
        sesion.iniciar(e.admin);
        Pelicula nueva = proxy.registrarPelicula("Nueva", Genero.COMEDIA, Clasificacion.TODO_PUBLICO, 90, "");
        assertTrue(e.cartelera.listarPeliculas().contains(nueva));
        assertTrue(auditoria.listar().get(0).permitida());
        assertEquals("admin@cinemauq.com", auditoria.listar().get(0).usuario());
    }

    @Test
    void lasConsultasPasanSinControlNiRegistro() {
        assertFalse(proxy.listarPeliculas().isEmpty());                       // sin sesión
        assertTrue(auditoria.listar().isEmpty());
    }
}
