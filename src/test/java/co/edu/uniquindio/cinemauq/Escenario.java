package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.fabrica.CatalogoFabricasEntrada;
import co.edu.uniquindio.cinemauq.modelo.*;
import co.edu.uniquindio.cinemauq.modelo.enums.*;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioMemoria;
import co.edu.uniquindio.cinemauq.servicio.*;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.time.LocalDateTime;

/** Arma un cine pequeño con un reloj controlable, para que las pruebas no dependan de la hora real. */
class Escenario {

    static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 2, 10, 0);

    LocalDateTime hora = AHORA;
    final Reloj reloj = () -> hora;

    final RepositorioMemoria<Usuario> usuarios = new RepositorioMemoria<>();
    final RepositorioMemoria<Funcion> funciones = new RepositorioMemoria<>();
    final RepositorioMemoria<Compra> compras = new RepositorioMemoria<>();

    final ServicioAutenticacion autenticacion = new ServicioAutenticacion(usuarios);
    final ServicioCartelera cartelera = new ServicioCartelera(new RepositorioMemoria<>(), new RepositorioMemoria<>(),
            funciones, reloj);
    final ServicioConfiteria confiteria = new ServicioConfiteria(new RepositorioMemoria<>(), new RepositorioMemoria<>());
    final ServicioTarjeta tarjetas = new ServicioTarjeta(new RepositorioMemoria<>(), reloj);
    final ServicioCompras servicioCompras = new ServicioCompras(compras, new CatalogoFabricasEntrada(),
            new ReglaPorValorGastado(1000), new EquivalenciaPuntos(100), new PoliticaCancelacion(), reloj);

    final Administrador admin;
    final Cliente cliente;
    final Pelicula pelicula;
    final Sala sala;
    final Funcion funcion;     // dentro de 3 días: cancelación al 100 %
    final Producto gaseosa;

    Escenario() {
        try {
            admin = autenticacion.registrarAdministrador("Admin", "admin@cinemauq.com", "admin123");
            cliente = autenticacion.registrarCliente("Ana", "100", "ana@correo.com", "123456");
            tarjetas.recargar(admin, cliente, 100_000);
            pelicula = cartelera.registrarPelicula("Prueba", Genero.DRAMA, Clasificacion.TODO_PUBLICO, 100, "");
            sala = cartelera.registrarSala("Sala 1", TipoSala.DOS_D, 5, 6, 1);   // fila E preferencial
            funcion = cartelera.programarFuncion(pelicula, sala, AHORA.plusDays(3), 10_000);
            gaseosa = confiteria.registrarProducto("Gaseosa", 7_000, CategoriaProducto.BEBIDA);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    Asiento silla(String codigo) {
        return sala.buscarAsiento(codigo).orElseThrow();
    }
}
