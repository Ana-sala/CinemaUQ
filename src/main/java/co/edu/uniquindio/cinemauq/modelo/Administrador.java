package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.Rol;

/** Administrador del cine: gestiona cartelera, confitería, recargas y funciones. */
public class Administrador extends Usuario {

    public Administrador(String id, String nombre, String correo, String contrasena) {
        super(id, nombre, correo, contrasena);
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }
}
