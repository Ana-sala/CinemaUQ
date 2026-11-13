package co.edu.uniquindio.cinemauq.fachada;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.servicio.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.ServicioPuntos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PATRÓN ESTRUCTURAL · FACADE.
 * Una sola puerta para la pantalla del cliente. Detrás hay un subsistema con muchas
 * piezas: ServicioCompras, CompraBuilder, el catálogo de fábricas de entradas, la cadena
 * de decoradores de precio, la tarjeta virtual y la cuenta de puntos. El controlador solo
 * conoce tres operaciones: cotizar, comprar y cancelar.
 */
public class TaquillaFacade {

    private final ServicioCompras compras;
    private final ServicioPuntos puntos;

    public TaquillaFacade(ServicioCompras compras, ServicioPuntos puntos) {
        this.compras = compras;
        this.puntos = puntos;
    }

    /** Arma la compra sin cobrar, para mostrar el resumen en pantalla. */
    public Cotizacion cotizar(Cliente cliente, Funcion funcion, Collection<Asiento> sillas, boolean sillasConPuntos,
                              List<PedidoConfiteria> confiteria) throws CinemaUQException {
        CompraBuilder builder = armar(cliente, funcion, sillas, sillasConPuntos, confiteria);
        List<String> items = new ArrayList<>();
        for (Entrada entrada : builder.getEntradas()) {
            items.add(entrada.toString());
        }
        for (LineaConfiteria linea : builder.getLineas()) {
            items.add(linea.toString());
        }
        return new Cotizacion(items, builder.getCalculadora().getDetalle(), builder.getValorAPagar(),
                builder.getPuntosNecesarios(), puntos.disponibles(cliente));
    }

    /** Arma la compra, la valida, cobra con la tarjeta, redime y acumula puntos. */
    public Compra comprar(Cliente cliente, Funcion funcion, Collection<Asiento> sillas, boolean sillasConPuntos,
                          List<PedidoConfiteria> confiteria) throws CinemaUQException {
        Compra compra = armar(cliente, funcion, sillas, sillasConPuntos, confiteria).build();
        return compras.pagar(compra);
    }

    /** Cancela con la política de tiempo y devuelve el dinero y los puntos. */
    public Reembolso cancelar(Cliente cliente, Compra compra) throws CinemaUQException {
        return compras.cancelarCompra(cliente, compra);
    }

    /** Cuánto se le devolvería al cliente si cancela ahora (lo calcula la política de reembolso). */
    public int porcentajeSiCancela(Compra compra) throws CinemaUQException {
        return compras.porcentajeSiCancela(compra);
    }

    public List<Compra> historial(Cliente cliente) {
        return compras.historialDe(cliente);
    }

    private CompraBuilder armar(Cliente cliente, Funcion funcion, Collection<Asiento> sillas, boolean sillasConPuntos,
                                List<PedidoConfiteria> confiteria) throws CinemaUQException {
        CompraBuilder builder = compras.nuevaCompra(cliente, funcion);
        for (Asiento silla : sillas) {
            if (sillasConPuntos) {
                builder.agregarEntradaConPuntos(silla);
            } else {
                builder.agregarEntrada(silla);
            }
        }
        for (PedidoConfiteria pedido : confiteria) {
            if (pedido.conPuntos()) {
                builder.agregarConfiteriaConPuntos(pedido.item(), pedido.cantidad());
            } else {
                builder.agregarConfiteria(pedido.item(), pedido.cantidad());
            }
        }
        return builder;
    }
}
