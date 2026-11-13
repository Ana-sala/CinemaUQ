package co.edu.uniquindio.cinemauq.datos;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.cinemauq.modelo.Administrador;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.servicio.ServicioCartelera;
import co.edu.uniquindio.cinemauq.servicio.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.ServicioTarjeta;
import co.edu.uniquindio.cinemauq.util.RelojAjustable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Simula días de operación del cine para que las estadísticas, los reportes y los
 * gráficos tengan datos al abrir la aplicación. Mueve el RelojAjustable al pasado día por
 * día y en cada día programa funciones, recarga tarjetas, vende entradas y confitería y
 * cancela algunas compras, siempre a través de los servicios reales (con sus validaciones,
 * promociones, puntos y observadores). El Random tiene semilla fija: las cifras son las
 * mismas en cada ejecución; solo cambian las fechas.
 */
final class HistorialSimulado {

    /** Una acción del día en un momento dado; se ejecutan en orden de hora. */
    private interface Accion {
        void ejecutar() throws CinemaUQException;
    }

    private record Evento(LocalDateTime hora, int orden, Accion accion) {
    }

    // Horarios fijos por sala: respetan la duración de las películas y los 20 minutos de limpieza
    private static final LocalTime[][] HORARIOS = {
            {LocalTime.of(14, 0), LocalTime.of(18, 30)},
            {LocalTime.of(15, 0), LocalTime.of(19, 30)},
            {LocalTime.of(16, 0), LocalTime.of(20, 30)}};
    private static final double[][] PRECIOS = {{12_000, 15_000}, {17_000, 17_000}, {22_000, 22_000}};

    private final Random azar = new Random(2026);
    private final RelojAjustable reloj;
    private final ServicioCartelera cartelera;
    private final ServicioCompras compras;
    private final ServicioTarjeta tarjetas;
    private final Administrador admin;
    private final List<Cliente> clientes;
    private final List<Pelicula> peliculas;
    private final List<Sala> salas;
    private final List<ItemConfiteria> confiteria;
    private PriorityQueue<Evento> agenda;
    private int secuencia;

    HistorialSimulado(RelojAjustable reloj, ServicioCartelera cartelera, ServicioCompras compras, ServicioTarjeta tarjetas,
                      Administrador admin, List<Cliente> clientes, List<Pelicula> peliculas, List<Sala> salas,
                      List<ItemConfiteria> confiteria) {
        this.reloj = reloj;
        this.cartelera = cartelera;
        this.compras = compras;
        this.tarjetas = tarjetas;
        this.admin = admin;
        this.clientes = clientes;
        this.peliculas = peliculas;
        this.salas = salas;
        this.confiteria = confiteria;
    }

    /** Genera los días [hoy - dias, hoy - 1] y deja el reloj otra vez en la hora real. */
    void generar(LocalDate hoy, int dias) throws CinemaUQException {
        try {
            LocalDate inicio = hoy.minusDays(dias);
            reloj.fijar(inicio.atTime(8, 0));
            for (Cliente c : clientes) {
                tarjetas.recargar(admin, c, 150_000 + 10_000 * azar.nextInt(16));
            }
            for (int k = dias; k >= 1; k--) {
                simularDia(hoy.minusDays(k), k);
            }
        } finally {
            reloj.liberar();
        }
    }

    private void simularDia(LocalDate dia, int diasAtras) throws CinemaUQException {
        agenda = new PriorityQueue<>(Comparator.comparing(Evento::hora).thenComparing(Evento::orden));
        reloj.fijar(dia.atTime(8, 0));
        List<Funcion> delDia = new ArrayList<>();
        for (int s = 0; s < salas.size(); s++) {
            for (int t = 0; t < HORARIOS[s].length; t++) {
                Pelicula pelicula = peliculas.get(azar.nextInt(peliculas.size()));
                if (diasAtras == 3 && s == 2 && t == 1) {
                    pelicula = peliculas.get(1);                    // estreno en la Sala VIP: se llena
                }
                Funcion f = cartelera.programarFuncion(pelicula, salas.get(s), dia.atTime(HORARIOS[s][t]), PRECIOS[s][t]);
                delDia.add(f);
                boolean estreno = diasAtras == 3 && s == 2 && t == 1;
                int ventas = estreno ? 12 : s == 0 ? 4 + azar.nextInt(7) : s == 1 ? 3 + azar.nextInt(6) : 2 + azar.nextInt(5);
                for (int v = 0; v < ventas; v++) {
                    LocalDateTime hora = horaDeCompra(dia, f.getFechaHora());
                    agendar(hora, () -> vender(f, estreno));
                }
            }
        }
        if (diasAtras == 9) {
            Funcion cancelada = delDia.get(3);                       // Sala 2, 19:30: el cine la cancela
            agendar(dia.atTime(17, 0), () -> compras.cancelarFuncion(admin, cancelada));
        }
        while (!agenda.isEmpty()) {
            Evento e = agenda.poll();
            reloj.fijar(e.hora());
            e.accion().ejecutar();
        }
    }

    private void agendar(LocalDateTime hora, Accion accion) {
        agenda.add(new Evento(hora, secuencia++, accion));
    }

    private LocalDateTime horaDeCompra(LocalDate dia, LocalDateTime inicioFuncion) {
        long margen = java.time.Duration.between(dia.atTime(9, 0), inicioFuncion.minusMinutes(15)).toMinutes();
        return dia.atTime(9, 0).plusMinutes(azar.nextInt((int) margen));
    }

    /** Un cliente compra sillas libres (a veces con confitería o pagando una entrada con puntos). */
    private void vender(Funcion funcion, boolean estreno) throws CinemaUQException {
        if (!funcion.admiteVentas(reloj.ahora())) {
            return;                                                  // el cine la canceló antes
        }
        Cliente cliente = clientes.get(azar.nextInt(clientes.size()));
        List<Asiento> libres = new ArrayList<>(funcion.getSala().getAsientos().stream().filter(funcion::estaDisponible).toList());
        int cantidad = Math.min(libres.size(), estreno ? 3 : cantidadSillas());
        if (cantidad == 0) {
            return;
        }
        int inicio = azar.nextInt(libres.size() - cantidad + 1);     // sillas juntas cuando se puede
        List<Asiento> sillas = libres.subList(inicio, inicio + cantidad);
        ItemConfiteria item = azar.nextDouble() < 0.55 ? confiteria.get(azar.nextInt(confiteria.size())) : null;
        int unidades = 1 + (azar.nextDouble() < 0.3 ? 1 : 0);
        boolean conPuntos = azar.nextDouble() < 0.10;

        Compra compra = construir(cliente, funcion, sillas, item, unidades, conPuntos);
        Compra pagada;
        try {
            pagada = pagarRecargandoSiFalta(cliente, compra);
        } catch (PuntosInsuficientesException e) {                    // no le alcanzan los puntos: paga en dinero
            pagada = pagarRecargandoSiFalta(cliente, construir(cliente, funcion, sillas, item, unidades, false));
        }
        Compra hecha = pagada;
        if (!estreno && azar.nextDouble() < 0.07) {                  // algunos clientes cancelan después
            LocalDateTime cancela = reloj.ahora().plusMinutes(20 + azar.nextInt(120));
            if (cancela.plusHours(2).isBefore(funcion.getFechaHora())) {
                agendar(cancela, () -> compras.cancelarCompra(cliente, hecha));
            }
        }
    }

    private Compra pagarRecargandoSiFalta(Cliente cliente, Compra compra) throws CinemaUQException {
        double falta = compra.getValorAPagar() - cliente.getTarjeta().getSaldo();
        if (falta > 0) {                                             // el cliente recarga antes de comprar
            double minimo = Math.ceil((falta + 30_000) / 10_000) * 10_000;
            tarjetas.recargar(admin, cliente, Math.max(minimo, 100_000 + 20_000 * azar.nextInt(6)));
        }
        return compras.pagar(compra);
    }

    private Compra construir(Cliente cliente, Funcion funcion, List<Asiento> sillas, ItemConfiteria item, int unidades,
                             boolean primeraConPuntos) throws CinemaUQException {
        CompraBuilder b = compras.nuevaCompra(cliente, funcion);
        for (int i = 0; i < sillas.size(); i++) {
            if (i == 0 && primeraConPuntos) {
                b.agregarEntradaConPuntos(sillas.get(i));
            } else {
                b.agregarEntrada(sillas.get(i));
            }
        }
        if (item != null) {
            b.agregarConfiteria(item, unidades);
        }
        return b.build();
    }

    private int cantidadSillas() {
        double r = azar.nextDouble();
        return r < 0.20 ? 1 : r < 0.65 ? 2 : r < 0.85 ? 3 : 4;
    }
}
