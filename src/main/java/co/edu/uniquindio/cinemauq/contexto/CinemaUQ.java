package co.edu.uniquindio.cinemauq.contexto;

import co.edu.uniquindio.cinemauq.datos.DatosIniciales;
import co.edu.uniquindio.cinemauq.fachada.TaquillaFacade;
import co.edu.uniquindio.cinemauq.fabrica.CatalogoFabricasEntrada;
import co.edu.uniquindio.cinemauq.modelo.Compra;
import co.edu.uniquindio.cinemauq.modelo.Funcion;
import co.edu.uniquindio.cinemauq.modelo.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.Promocion;
import co.edu.uniquindio.cinemauq.modelo.Sala;
import co.edu.uniquindio.cinemauq.modelo.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.Usuario;
import co.edu.uniquindio.cinemauq.observador.MonitorOcupacion;
import co.edu.uniquindio.cinemauq.observador.NotificadorCliente;
import co.edu.uniquindio.cinemauq.proxy.Auditoria;
import co.edu.uniquindio.cinemauq.reporte.ServicioReportes;
import co.edu.uniquindio.cinemauq.proxy.GestionCarteleraProxy;
import co.edu.uniquindio.cinemauq.repositorio.Repositorio;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioMemoria;
import co.edu.uniquindio.cinemauq.servicio.EquivalenciaPuntos;
import co.edu.uniquindio.cinemauq.servicio.GestionCartelera;
import co.edu.uniquindio.cinemauq.servicio.PoliticaPorAnticipacion;
import co.edu.uniquindio.cinemauq.servicio.PoliticaReembolsoTotal;
import co.edu.uniquindio.cinemauq.servicio.ReglaPorValorGastado;
import co.edu.uniquindio.cinemauq.servicio.ServicioAutenticacion;
import co.edu.uniquindio.cinemauq.servicio.ServicioCartelera;
import co.edu.uniquindio.cinemauq.servicio.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.ServicioConfiteria;
import co.edu.uniquindio.cinemauq.servicio.ServicioEstadisticas;
import co.edu.uniquindio.cinemauq.servicio.ServicioPromociones;
import co.edu.uniquindio.cinemauq.servicio.ServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.ServicioTarjeta;
import co.edu.uniquindio.cinemauq.util.Reloj;
import co.edu.uniquindio.cinemauq.util.RelojSistema;

/**
 * PATRÓN CREACIONAL · SINGLETON.
 * Representa EL cine: una única instancia que crea y conecta los repositorios y los
 * servicios, carga los datos iniciales y guarda la sesión. Todos los controladores de
 * JavaFX piden CinemaUQ.getInstance() y así comparten los mismos datos: si el
 * administrador recarga una tarjeta, el cliente ve el nuevo saldo.
 */
public final class CinemaUQ {

    private static CinemaUQ instancia;

    private final Sesion sesion = new Sesion();
    private final ServicioAutenticacion autenticacion;
    private final ServicioCartelera cartelera;
    private final ServicioConfiteria confiteria;
    private final ServicioTarjeta tarjetas;
    private final ServicioCompras compras;
    private final ServicioPuntos puntos;
    private final ServicioEstadisticas estadisticas;
    private final ServicioPromociones promociones;
    private final ServicioReportes reportes;
    private final TaquillaFacade taquilla;
    private final Auditoria auditoria = new Auditoria();
    private final GestionCartelera gestionCartelera;
    private final NotificadorCliente notificador = new NotificadorCliente();          // Observer
    private final MonitorOcupacion monitorOcupacion = new MonitorOcupacion(80);       // Observer
    private final Reloj reloj;

    /** Constructor privado: nadie fuera de la clase puede hacer new CinemaUQ(). */
    private CinemaUQ() {
        reloj = new RelojSistema();
        Repositorio<Usuario> usuarios = new RepositorioMemoria<>();
        Repositorio<Pelicula> peliculas = new RepositorioMemoria<>();
        Repositorio<Sala> salas = new RepositorioMemoria<>();
        Repositorio<Funcion> funciones = new RepositorioMemoria<>();
        Repositorio<ItemConfiteria> items = new RepositorioMemoria<>();
        Repositorio<Promocion> promociones = new RepositorioMemoria<>();
        Repositorio<SolicitudRecarga> solicitudes = new RepositorioMemoria<>();
        Repositorio<Compra> repoCompras = new RepositorioMemoria<>();

        EquivalenciaPuntos equivalencia = new EquivalenciaPuntos(100);
        autenticacion = new ServicioAutenticacion(usuarios);
        cartelera = new ServicioCartelera(peliculas, salas, funciones, reloj);
        confiteria = new ServicioConfiteria(items);
        this.promociones = new ServicioPromociones(promociones);
        tarjetas = new ServicioTarjeta(solicitudes, reloj);
        compras = new ServicioCompras(repoCompras, new CatalogoFabricasEntrada(), new ReglaPorValorGastado(1000),
                equivalencia, new PoliticaPorAnticipacion(), new PoliticaReembolsoTotal(),   // Strategy
                this.promociones, reloj);
        puntos = new ServicioPuntos(equivalencia, reloj);
        estadisticas = new ServicioEstadisticas(repoCompras::listar, funciones::listar, autenticacion::listarClientes);
        reportes = new ServicioReportes(estadisticas, reloj);
        taquilla = new TaquillaFacade(compras, puntos);                                   // Facade
        gestionCartelera = new GestionCarteleraProxy(cartelera, sesion, auditoria, reloj); // Proxy
        compras.agregarObservador(notificador);                                           // Observer
        compras.agregarObservador(monitorOcupacion);

        DatosIniciales.cargar(this);
    }

    /** Punto de acceso global. Crea la instancia la primera vez (inicialización perezosa). */
    public static synchronized CinemaUQ getInstance() {
        if (instancia == null) {
            instancia = new CinemaUQ();
        }
        return instancia;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public ServicioAutenticacion getAutenticacion() {
        return autenticacion;
    }

    public ServicioCartelera getCartelera() {
        return cartelera;
    }

    public ServicioConfiteria getConfiteria() {
        return confiteria;
    }

    public ServicioTarjeta getTarjetas() {
        return tarjetas;
    }

    public ServicioCompras getCompras() {
        return compras;
    }

    public ServicioPuntos getPuntos() {
        return puntos;
    }

    public ServicioEstadisticas getEstadisticas() {
        return estadisticas;
    }

    public ServicioReportes getReportes() {
        return reportes;
    }

    public ServicioPromociones getPromociones() {
        return promociones;
    }

    /** Facade para la pantalla del cliente. */
    public TaquillaFacade getTaquilla() {
        return taquilla;
    }

    /** Gestión de cartelera protegida por el Proxy (lo que usa la pantalla del administrador). */
    public GestionCartelera getGestionCartelera() {
        return gestionCartelera;
    }

    /** Avisos de compras para cada cliente (Observer). */
    public NotificadorCliente getNotificador() {
        return notificador;
    }

    /** Alertas de funciones con más del 80 % de ocupación (Observer). */
    public MonitorOcupacion getMonitorOcupacion() {
        return monitorOcupacion;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public Reloj getReloj() {
        return reloj;
    }
}
