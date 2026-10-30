package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.excepciones.CredencialesInvalidasException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.UsuarioYaExisteException;
import co.edu.uniquindio.cinemauq.modelo.Administrador;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.util.List;

/** Registro e inicio de sesión (RF-001, RF-002). */
public class ServicioAutenticacion {

    private final Repositorio<Usuario> usuarios;

    public ServicioAutenticacion(Repositorio<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    /** RN-06 y RN-07: correo y documento únicos; la tarjeta nace activa con saldo 0. */
    public Cliente registrarCliente(String nombre, String documento, String correo, String contrasena)
            throws DatosInvalidosException, UsuarioYaExisteException {
        validarTexto(nombre, "nombre");
        validarTexto(documento, "documento");
        if (correo == null || !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new DatosInvalidosException("El correo no es válido.");
        }
        if (contrasena == null || contrasena.length() < 6) {
            throw new DatosInvalidosException("La contraseña debe tener al menos 6 caracteres.");
        }
        String correoNormalizado = correo.trim().toLowerCase();
        if (!usuarios.buscar(u -> u.getCorreo().equals(correoNormalizado)).isEmpty()) {
            throw new UsuarioYaExisteException("Ya existe una cuenta con el correo " + correoNormalizado);
        }
        if (!usuarios.buscar(u -> u instanceof Cliente c && c.getDocumento().equals(documento.trim())).isEmpty()) {
            throw new UsuarioYaExisteException("Ya existe una cuenta con el documento " + documento);
        }
        Cliente cliente = new Cliente(GeneradorId.siguiente("CLI"), nombre.trim(), documento.trim(), correoNormalizado,
                contrasena, new TarjetaVirtual(GeneradorId.siguiente("TV")));
        usuarios.guardar(cliente);
        return cliente;
    }

    public Administrador registrarAdministrador(String nombre, String correo, String contrasena) {
        Administrador admin = new Administrador(GeneradorId.siguiente("ADM"), nombre, correo, contrasena);
        usuarios.guardar(admin);
        return admin;
    }

    public Usuario iniciarSesion(String correo, String contrasena) throws CredencialesInvalidasException {
        String correoNormalizado = correo == null ? "" : correo.trim().toLowerCase();
        return usuarios.buscar(u -> u.getCorreo().equals(correoNormalizado) && u.verificarContrasena(contrasena))
                .stream().findFirst()
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));
    }

    public List<Cliente> listarClientes() {
        return usuarios.listar().stream().filter(Cliente.class::isInstance).map(Cliente.class::cast).toList();
    }

    private void validarTexto(String valor, String campo) throws DatosInvalidosException {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException("El campo " + campo + " es obligatorio.");
        }
    }
}
