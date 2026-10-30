package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.builder.CompraBuilder;
import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.FuncionNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoAutorizadaException;
import co.edu.uniquindio.cinemauq.excepciones.ReembolsoDuplicadoException;
import co.edu.uniquindio.cinemauq.fabrica.CatalogoFabricasEntrada;
import co.edu.uniquindio.cinemauq.modelo.Cliente;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Entrada;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.LineaConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Reembolso;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.modelo.enums.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.enums.MotivoReembolso;
import co.edu.uniquindio.cinemauq.modelo.enums.Rol;
import co.edu.uniquindio.cinemauq.modelo.enums.TipoMovimiento;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.util.Reloj;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Orquesta la compra, el pago, la cancelación y el reembolso (RF-007, RF-008, RF-012, RF-014, RF-021).
 * No sabe calcular puntos ni porcentajes: se los pide a sus colaboradores, que recibe
 * por el constructor como abstracciones (DIP).
 */
public class ServicioCompras {

    private final Repositorio<Compra> compras;
    private final CatalogoFabricasEntrada fabricas;
    private final ReglaAcumulacionPuntos reglaPuntos;
    private final EquivalenciaPuntos equivalencia;
    private final PoliticaCancelacion politica;
    private final Reloj reloj;

    public ServicioCompras(Repositorio<Compra> compras, CatalogoFabricasEntrada fabricas,
                           ReglaAcumulacionPuntos reglaPuntos, EquivalenciaPuntos equivalencia,
                           PoliticaCancelacion politica, Reloj reloj) {
        this.compras = compras;
        this.fabricas = fabricas;
        this.reglaPuntos = reglaPuntos;
        this.equivalencia = equivalencia;
        this.politica = politica;
        this.reloj = reloj;
    }

    /** Punto de partida de una compra: entrega el Builder para que la pantalla la arme. */
    public CompraBuilder nuevaCompra(Cliente cliente, Funcion funcion) throws FuncionNoDisponibleException {
        if (!funcion.admiteVentas(reloj.ahora())) {
            throw new FuncionNoDisponibleException("La función ya inició o fue cancelada.");
        }
        return new CompraBuilder(cliente, funcion, fabricas, equivalencia);
    }

    /**
     * RF-008: primero valida todo (función, sillas, tarjeta, puntos) y solo si todo
     * está bien modifica algo. Así una compra rechazada no deja cambios a medias.
     */
    public Compra pagar(Compra compra) throws CinemaUQException {
        LocalDateTime ahora = reloj.ahora();
        LocalDate hoy = ahora.toLocalDate();
        Cliente cliente = compra.getCliente();
        Funcion funcion = compra.getFuncion();

        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new DatosInvalidosException("La compra " + compra.getId() + " ya fue procesada.");
        }
        if (!funcion.admiteVentas(ahora)) {
            throw new FuncionNoDisponibleException("La función ya inició o fue cancelada.");
        }
        for (Entrada entrada : compra.getEntradas()) {
            if (!funcion.estaDisponible(entrada.getAsiento())) {
                throw new AsientoNoDisponibleException("El asiento " + entrada.getAsiento().getCodigo()
                        + " acaba de ser vendido. Elija otro.");
            }
        }
        for (LineaConfiteria linea : compra.getLineas()) {
            if (!linea.getItem().isDisponible()) {
                throw new DatosInvalidosException(linea.getItem().getNombre() + " ya no está disponible.");
            }
        }
        double valor = compra.getValorAPagar();
        if (valor > 0) {
            cliente.getTarjeta().validarPago(valor);
        }
        cliente.getCuentaPuntos().verificarDisponibles(compra.getPuntosRedimidos(), hoy);

        // --- todo validado: ahora sí se aplican los cambios ---
        for (Entrada entrada : compra.getEntradas()) {
            funcion.ocupar(entrada.getAsiento());
        }
        if (valor > 0) {
            cliente.getTarjeta().debitar(valor, "Compra " + compra.getId() + " · " + funcion.getPelicula().getTitulo(), ahora);
        }
        if (compra.getPuntosRedimidos() > 0) {
            cliente.getCuentaPuntos().redimir(compra.getPuntosRedimidos(), hoy);
        }
        int ganados = reglaPuntos.calcularPuntos(valor);
        if (ganados > 0) {
            cliente.getCuentaPuntos().acumular(ganados, hoy, compra.getId());
        }
        compra.marcarPagada(ahora, valor, ganados);
        compras.guardar(compra);
        cliente.agregarCompra(compra);
        return compra;
    }

    /** RF-012: el cliente cancela según la política de tiempo (RN-04). */
    public Reembolso cancelarCompra(Cliente solicitante, Compra compra) throws CinemaUQException {
        if (!compra.getCliente().equals(solicitante)) {
            throw new OperacionNoAutorizadaException("Solo el dueño de la compra puede cancelarla.");
        }
        if (compra.getEstado() != EstadoCompra.PAGADA) {
            throw new CancelacionNoPermitidaException("Solo se cancelan compras pagadas; esta está " + compra.getEstado() + ".");
        }
        int porcentaje = politica.porcentajeReembolso(compra.getFuncion().getFechaHora(), reloj.ahora());
        return reembolsar(compra, porcentaje, MotivoReembolso.CANCELACION_CLIENTE);
    }

    /** RF-021: el cine cancela la función y devuelve el 100 % a cada compra pagada. */
    public List<Reembolso> cancelarFuncion(Usuario operador, Funcion funcion) throws CinemaUQException {
        if (operador == null || operador.getRol() != Rol.ADMINISTRADOR) {
            throw new OperacionNoAutorizadaException("Solo un administrador puede cancelar funciones.");
        }
        funcion.cancelar();
        List<Reembolso> reembolsos = new ArrayList<>();
        for (Compra compra : comprasDe(funcion)) {
            if (compra.getEstado() == EstadoCompra.PAGADA) {
                reembolsos.add(reembolsar(compra, 100, MotivoReembolso.FUNCION_CANCELADA));
            }
        }
        return reembolsos;
    }

    private Reembolso reembolsar(Compra compra, int porcentaje, MotivoReembolso motivo) throws CinemaUQException {
        if (compra.isReembolsada()) {
            throw new ReembolsoDuplicadoException("La compra " + compra.getId() + " ya fue reembolsada.");
        }
        LocalDateTime ahora = reloj.ahora();
        Cliente cliente = compra.getCliente();
        double valor = Math.round(compra.getValorPagado() * porcentaje / 100.0);
        Reembolso reembolso = new Reembolso(compra.getId(), ahora, porcentaje, valor, compra.getPuntosRedimidos(), motivo);
        compra.registrarReembolso(reembolso);            // marca la operación: no se puede repetir
        if (valor > 0) {
            cliente.getTarjeta().acreditar(TipoMovimiento.REEMBOLSO, valor,
                    "Reembolso " + porcentaje + "% compra " + compra.getId(), ahora);
        }
        for (Entrada entrada : compra.getEntradas()) {
            compra.getFuncion().liberar(entrada.getAsiento());
        }
        cliente.getCuentaPuntos().anularAcumulacion(compra.getId());
        if (compra.getPuntosRedimidos() > 0) {
            cliente.getCuentaPuntos().acumular(compra.getPuntosRedimidos(), ahora.toLocalDate(),
                    "Devolución de puntos " + compra.getId());
        }
        return reembolso;
    }

    public List<Compra> historialDe(Cliente cliente) {
        return compras.buscar(c -> c.getCliente().equals(cliente)).stream()
                .sorted(Comparator.comparing(Compra::getFecha).reversed()).toList();
    }

    public List<Compra> comprasDe(Funcion funcion) {
        return compras.buscar(c -> c.getFuncion().equals(funcion));
    }

    public List<Compra> listarCompras() {
        return compras.listar();
    }

    public EquivalenciaPuntos getEquivalencia() {
        return equivalencia;
    }
}
