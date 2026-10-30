package co.edu.uniquindio.cinemauq.datos;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.contexto.CinemaUQ;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.Administrador;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Combo;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Producto;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.enums.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.enums.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.enums.Genero;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoSala;
import co.edu.uniquindio.cinemauq.servicio.ServicioCartelera;
import co.edu.uniquindio.cinemauq.servicio.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.ServicioConfiteria;
import co.edu.uniquindio.cinemauq.servicio.ServicioTarjeta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Datos de demostración. Se crean a través de los servicios (no con new directo),
 * así también pasan por las validaciones de negocio. Las fechas se calculan desde
 * "hoy" para que siempre haya funciones futuras al abrir la aplicación.
 *
 * Usuarios de prueba (contraseña):
 *   admin@cinemauq.com  (admin123)  administrador
 *   laura@correo.com    (123456)    cliente con saldo, compras y puntos
 *   carlos@correo.com   (123456)    cliente con poco saldo y una solicitud de recarga
 *   sofia@correo.com    (123456)    cliente con la tarjeta bloqueada
 */
public final class DatosIniciales {

    private DatosIniciales() {
    }

    public static void cargar(CinemaUQ cine) {
        try {
            cargarDatos(cine);
        } catch (CinemaUQException e) {
            throw new IllegalStateException("No se pudieron cargar los datos iniciales: " + e.getMessage(), e);
        }
    }

    private static void cargarDatos(CinemaUQ cine) throws CinemaUQException {
        ServicioCartelera cartelera = cine.getCartelera();
        ServicioConfiteria confiteria = cine.getConfiteria();
        ServicioTarjeta tarjetas = cine.getTarjetas();
        ServicioCompras compras = cine.getCompras();
        LocalDateTime ahora = cine.getReloj().ahora();
        LocalDate hoy = ahora.toLocalDate();

        // ---------- Usuarios ----------
        Administrador admin = cine.getAutenticacion().registrarAdministrador("Administrador CinemaUQ",
                "admin@cinemauq.com", "admin123");
        Cliente laura = cine.getAutenticacion().registrarCliente("Laura Gómez", "1094000001", "laura@correo.com", "123456");
        Cliente carlos = cine.getAutenticacion().registrarCliente("Carlos Ruiz", "1094000002", "carlos@correo.com", "123456");
        Cliente sofia = cine.getAutenticacion().registrarCliente("Sofía Herrera", "1094000003", "sofia@correo.com", "123456");

        tarjetas.recargar(admin, laura, 300_000);
        tarjetas.recargar(admin, carlos, 30_000);
        tarjetas.recargar(admin, sofia, 40_000);
        tarjetas.bloquearTarjeta(admin, sofia);
        tarjetas.solicitarRecarga(carlos, 50_000);

        // Puntos de compras anteriores: uno vencido, uno por vencer y uno reciente (ejemplo del enunciado)
        laura.getCuentaPuntos().acumular(200, hoy.minusMonths(13), "Compras de hace 13 meses (vencidos)");
        laura.getCuentaPuntos().acumular(300, hoy.minusMonths(11).minusDays(10), "Compras de hace 11 meses");
        laura.getCuentaPuntos().acumular(500, hoy.minusMonths(2), "Compras de hace 2 meses");

        // ---------- Películas ----------
        Pelicula ecos = cartelera.registrarPelicula("Ecos del Quindío", Genero.DRAMA, Clasificacion.MAYORES_12, 118,
                "Una familia cafetera enfrenta la última cosecha antes de vender la finca.");
        Pelicula galaxia = cartelera.registrarPelicula("Galaxia Cafetera", Genero.CIENCIA_FICCION, Clasificacion.MAYORES_7, 132,
                "Dos estudiantes de la UQ descubren una señal extraterrestre en el Parque del Café.");
        Pelicula risas = cartelera.registrarPelicula("Risas en Armenia", Genero.COMEDIA, Clasificacion.TODO_PUBLICO, 98,
                "Un concurso de chistes enreda a todo un barrio durante las fiestas aniversarias.");
        Pelicula bosque = cartelera.registrarPelicula("La Casa del Bosque", Genero.TERROR, Clasificacion.MAYORES_15, 105,
                "Un grupo de amigos pasa la noche en una casa abandonada cerca de Salento.");
        Pelicula gigantes = cartelera.registrarPelicula("Pequeños Gigantes", Genero.ANIMACION, Clasificacion.TODO_PUBLICO, 92,
                "Unas hormigas arrieras organizan la mudanza más grande del bosque.");
        Pelicula clasico = cartelera.registrarPelicula("Memorias de la Montaña", Genero.DOCUMENTAL, Clasificacion.TODO_PUBLICO, 85,
                "Documental sobre el paisaje cultural cafetero.");
        cartelera.cambiarEstadoCartelera(clasico, false);

        // ---------- Salas (los asientos se crean con la sala) ----------
        Sala sala1 = cartelera.registrarSala("Sala 1", TipoSala.DOS_D, 8, 12, 2);
        Sala sala2 = cartelera.registrarSala("Sala 2", TipoSala.TRES_D, 7, 10, 2);
        Sala vip = cartelera.registrarSala("Sala VIP", TipoSala.VIP, 5, 8, 5);

        // ---------- Funciones ----------
        LocalDate d1 = hoy.plusDays(1);
        LocalDate d2 = hoy.plusDays(2);
        LocalDate d3 = hoy.plusDays(3);
        // Funciones cercanas: para probar la política de cancelación (menos de 2 h y entre 2 y 24 h)
        Funcion pronto = cartelera.programarFuncion(risas, sala2, redondear(ahora.plusMinutes(90)), 12_000);
        Funcion hoyNoche = cartelera.programarFuncion(galaxia, vip, redondear(ahora.plusHours(5)), 22_000);

        cartelera.programarFuncion(risas, sala1, d1.atTime(14, 0), 12_000);
        Funcion ecosD1 = cartelera.programarFuncion(ecos, sala1, d1.atTime(16, 30), 14_000);
        Funcion galaxiaD1 = cartelera.programarFuncion(galaxia, sala1, d1.atTime(19, 30), 15_000);
        cartelera.programarFuncion(galaxia, sala2, d1.atTime(15, 0), 17_000);
        Funcion bosqueD1 = cartelera.programarFuncion(bosque, sala2, d1.atTime(18, 30), 16_000);
        cartelera.programarFuncion(ecos, vip, d1.atTime(20, 0), 22_000);
        cartelera.programarFuncion(ecos, sala1, d2.atTime(15, 0), 14_000);
        cartelera.programarFuncion(risas, sala1, d2.atTime(18, 0), 12_000);
        Funcion gigantesD2 = cartelera.programarFuncion(gigantes, sala2, d2.atTime(16, 0), 13_000);
        cartelera.programarFuncion(galaxia, sala2, d2.atTime(19, 0), 17_000);
        cartelera.programarFuncion(bosque, vip, d2.atTime(20, 30), 22_000);
        cartelera.programarFuncion(galaxia, sala1, d3.atTime(17, 0), 15_000);
        // Prototype: la función de las 19:30 se repite igual tres días después
        cartelera.duplicarFuncion(galaxiaD1, d1.plusDays(3).atTime(LocalTime.of(19, 30)));

        // ---------- Confitería ----------
        Producto crispetasM = confiteria.registrarProducto("Crispetas medianas", 12_000, CategoriaProducto.CRISPETAS);
        Producto crispetasG = confiteria.registrarProducto("Crispetas grandes", 16_000, CategoriaProducto.CRISPETAS);
        Producto gaseosa = confiteria.registrarProducto("Gaseosa 500 ml", 7_000, CategoriaProducto.BEBIDA);
        confiteria.registrarProducto("Agua 600 ml", 4_500, CategoriaProducto.BEBIDA);
        confiteria.registrarProducto("Perro caliente", 12_000, CategoriaProducto.SNACK);
        Producto nachos = confiteria.registrarProducto("Nachos con queso", 14_000, CategoriaProducto.SNACK);
        confiteria.registrarProducto("Chocolatina", 4_000, CategoriaProducto.DULCE);
        Combo individual = confiteria.registrarCombo("Combo Individual", 10, List.of(crispetasM, gaseosa));
        Combo pareja = confiteria.registrarCombo("Combo Pareja", 10, List.of(crispetasG, gaseosa, gaseosa));
        confiteria.registrarCombo("Combo Familiar", 15, List.of(crispetasG, crispetasG, gaseosa, gaseosa, gaseosa, gaseosa, nachos));

        confiteria.registrarPromocion("Martes de cine 2x1", 50, hoy, hoy.plusMonths(2));
        confiteria.registrarPromocion("Estudiante UQ -20% en combos", 20, hoy, hoy.plusMonths(6));

        // ---------- Compras de ejemplo (pasan por el Builder y el pago real) ----------
        pagar(compras, compras.nuevaCompra(laura, ecosD1)
                .agregarEntrada(silla(sala1, "D6")).agregarEntrada(silla(sala1, "D7"))
                .agregarConfiteria(pareja, 1));
        pagar(compras, compras.nuevaCompra(laura, pronto)
                .agregarEntrada(silla(sala2, "C5")));
        pagar(compras, compras.nuevaCompra(laura, hoyNoche)
                .agregarEntrada(silla(vip, "B4")).agregarConfiteria(individual, 1));
        pagar(compras, compras.nuevaCompra(carlos, bosqueD1)
                .agregarEntrada(silla(sala2, "E3")));
        pagar(compras, compras.nuevaCompra(laura, gigantesD2)
                .agregarEntrada(silla(sala2, "F1")).agregarEntrada(silla(sala2, "F2")).agregarEntrada(silla(sala2, "F3")));
        pagar(compras, compras.nuevaCompra(laura, galaxiaD1)
                .agregarEntrada(silla(sala1, "H5")).agregarEntrada(silla(sala1, "H6"))
                .agregarConfiteria(nachos, 2));
    }

    private static void pagar(ServicioCompras compras, CompraBuilder builder) throws CinemaUQException {
        compras.pagar(builder.build());
    }

    private static Asiento silla(Sala sala, String codigo) {
        return sala.buscarAsiento(codigo).orElseThrow();
    }

    /** Lleva una hora al siguiente múltiplo de 5 minutos, sin segundos. */
    private static LocalDateTime redondear(LocalDateTime hora) {
        LocalDateTime sinSegundos = hora.truncatedTo(ChronoUnit.MINUTES);
        int resto = sinSegundos.getMinute() % 5;
        return resto == 0 ? sinSegundos : sinSegundos.plusMinutes(5L - resto);
    }
}
