package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.CredencialesInvalidasException;
import co.edu.uniquindio.cinemauq.excepciones.UsuarioYaExisteException;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.enums.Rol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutenticacionYSingletonTest {

    private final Escenario e = new Escenario();

    @Test
    void elRegistroCreaTarjetaActivaConSaldoCero() throws Exception {
        Cliente nuevo = e.autenticacion.registrarCliente("Luis", "200", "luis@correo.com", "abcdef");
        assertEquals(EstadoTarjeta.ACTIVA, nuevo.getTarjeta().getEstado());
        assertEquals(0, nuevo.getTarjeta().getSaldo());
    }

    @Test
    void noSeRepiteElCorreo() {
        assertThrows(UsuarioYaExisteException.class,
                () -> e.autenticacion.registrarCliente("Otra", "999", "ANA@correo.com", "123456"));
    }

    @Test
    void iniciarSesionDevuelveElRolCorrecto() throws Exception {
        assertEquals(Rol.ADMINISTRADOR, e.autenticacion.iniciarSesion("admin@cinemauq.com", "admin123").getRol());
        assertEquals(Rol.CLIENTE, e.autenticacion.iniciarSesion("ana@correo.com", "123456").getRol());
        assertThrows(CredencialesInvalidasException.class, () -> e.autenticacion.iniciarSesion("ana@correo.com", "mala"));
    }

    @Test
    void singletonDevuelveSiempreLaMismaInstanciaConDatosIniciales() {
        CinemaUQ a = CinemaUQ.getInstance();
        CinemaUQ b = CinemaUQ.getInstance();
        assertSame(a, b);
        assertFalse(a.getCartelera().peliculasEnCartelera().isEmpty());
        assertFalse(a.getAutenticacion().listarClientes().isEmpty());
    }
}
