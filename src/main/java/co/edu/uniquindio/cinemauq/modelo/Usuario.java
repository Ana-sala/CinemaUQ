package co.edu.uniquindio.cinemauq.modelo;

import co.edu.uniquindio.cinemauq.modelo.enums.Rol;

/**
 * Usuario del sistema. Es abstracta: no existe un "usuario" a secas, existe un
 * Cliente o un Administrador (herencia + LSP: el login trabaja con Usuario
 * y funciona con cualquiera de los dos).
 */
public abstract class Usuario implements Identificable {

    private final String id;
    private String nombre;
    private String correo;
    private String contrasena;

    protected Usuario(String id, String nombre, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo.trim().toLowerCase();
        this.contrasena = contrasena;
    }

    public abstract Rol getRol();

    public boolean verificarContrasena(String intento) {
        return contrasena.equals(intento);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return nombre + " <" + correo + ">";
    }
}
