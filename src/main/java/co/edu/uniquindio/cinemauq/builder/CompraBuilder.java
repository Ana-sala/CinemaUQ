package co.edu.uniquindio.cinemauq.builder;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.fabrica.CatalogoFabricasEntrada;
import co.edu.uniquindio.cinemauq.modelo.Asiento;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.servicio.EquivalenciaPuntos;
import co.edu.uniquindio.cinemauq.util.GeneradorId;

import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN CREACIONAL · BUILDER.
 * Una compra se arma por partes mientras el cliente usa la pantalla: elige sillas,
 * agrega confitería, decide qué paga con puntos... y solo al final se construye.
 * El builder valida cada paso y build() entrega una Compra consistente.
 */
public class CompraBuilder {

    public static final int MAX_ENTRADAS = 10;

    private final Cliente cliente;
    private final Funcion funcion;
    private final CatalogoFabricasEntrada fabricas;
    private final EquivalenciaPuntos equivalencia;
    private final List<Entrada> entradas = new ArrayList<>();
    private final List<LineaConfiteria> lineas = new ArrayList<>();

    public CompraBuilder(Cliente cliente, Funcion funcion, CatalogoFabricasEntrada fabricas,
                         EquivalenciaPuntos equivalencia) {
        this.cliente = cliente;
        this.funcion = funcion;
        this.fabricas = fabricas;
        this.equivalencia = equivalencia;
    }

    public CompraBuilder agregarEntrada(Asiento asiento) throws AsientoNoDisponibleException, DatosInvalidosException {
        return agregarEntrada(asiento, false);
    }

    public CompraBuilder agregarEntradaConPuntos(Asiento asiento) throws AsientoNoDisponibleException, DatosInvalidosException {
        return agregarEntrada(asiento, true);
    }

    private CompraBuilder agregarEntrada(Asiento asiento, boolean conPuntos) throws AsientoNoDisponibleException, DatosInvalidosException {
        if (entradas.size() >= MAX_ENTRADAS) {
            throw new DatosInvalidosException("Máximo " + MAX_ENTRADAS + " entradas por compra.");
        }
        if (entradas.stream().anyMatch(e -> e.getAsiento().equals(asiento))) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo() + " ya está en esta compra.");
        }
        Entrada entrada = fabricas.emitir(funcion, asiento);   // Factory Method decide la clase de entrada
        entrada.setPagadaConPuntos(conPuntos);
        entradas.add(entrada);
        return this;
    }

    public CompraBuilder agregarConfiteria(ItemConfiteria item, int cantidad) throws DatosInvalidosException {
        return agregarConfiteria(item, cantidad, false);
    }

    public CompraBuilder agregarConfiteriaConPuntos(ItemConfiteria item, int cantidad) throws DatosInvalidosException {
        return agregarConfiteria(item, cantidad, true);
    }

    private CompraBuilder agregarConfiteria(ItemConfiteria item, int cantidad, boolean conPuntos) throws DatosInvalidosException {
        if (cantidad <= 0) {
            throw new DatosInvalidosException("La cantidad debe ser mayor que cero.");
        }
        if (!item.isDisponible()) {
            throw new DatosInvalidosException(item.getNombre() + " no está disponible.");
        }
        lineas.add(new LineaConfiteria(item, cantidad, conPuntos));
        return this;
    }

    public CompraBuilder quitarEntrada(Asiento asiento) {
        entradas.removeIf(e -> e.getAsiento().equals(asiento));
        return this;
    }

    /** Puntos que costará lo marcado "con puntos" según la equivalencia (RN-16). */
    public int getPuntosNecesarios() {
        int puntos = entradas.stream().filter(Entrada::isPagadaConPuntos)
                .mapToInt(e -> equivalencia.puntosPara(e.getPrecio())).sum();
        puntos += lineas.stream().filter(LineaConfiteria::isPagadaConPuntos)
                .mapToInt(l -> equivalencia.puntosPara(l.getPrecioUnitario()) * l.getCantidad()).sum();
        return puntos;
    }

    public double getValorAPagar() {
        return entradas.stream().filter(e -> !e.isPagadaConPuntos()).mapToDouble(Entrada::getPrecio).sum()
                + lineas.stream().filter(l -> !l.isPagadaConPuntos()).mapToDouble(LineaConfiteria::getSubtotal).sum();
    }

    public List<Entrada> getEntradas() {
        return List.copyOf(entradas);
    }

    public List<LineaConfiteria> getLineas() {
        return List.copyOf(lineas);
    }

    public Compra build() throws DatosInvalidosException {
        if (entradas.isEmpty() && lineas.isEmpty()) {
            throw new DatosInvalidosException("La compra debe tener al menos una entrada o un producto.");
        }
        return new Compra(GeneradorId.siguiente("CMP"), cliente, funcion, entradas, lineas, getPuntosNecesarios());
    }
}
