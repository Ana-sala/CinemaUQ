package co.edu.uniquindio.cinemauq.contexto;

import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Usuario;

/** Usuario que tiene la sesión abierta en la aplicación. */
public class Sesion {

    private Usuario usuario;

    public void iniciar(Usuario usuario) {
        this.usuario = usuario;
    }

    public void cerrar() {
        usuario = null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Cliente getCliente() {
        return usuario instanceof Cliente cliente ? cliente : null;
    }
}
