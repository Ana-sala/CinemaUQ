# CinemaUQ

Sistema integral de gestión de cine · Programación II · Universidad del Quindío · 2026-2.
Proyecto individual de Ana María Salazar Meza.

**Entrega 1 (parcial 1):** análisis, modelo del dominio, principios SOLID y patrones creacionales.

**Entrega 2 (parcial 2):** interfaz JavaFX con MVC, tarjeta virtual, excepciones del dominio y patrones estructurales.

**Entrega 3 (final):** patrones de comportamiento, políticas de cancelación y reembolso, programa de puntos con vigencia,
estadísticas por periodo, reportes exportables (.txt y .pdf), gráficos JavaFX y datos iniciales con 21 días de historial.

Cada entrega tiene su etiqueta en el repositorio: `entrega-1`, `entrega-2` y `entrega-3`.



### Usuarios de prueba

| Correo | Contraseña | Perfil |
|---|---|---|
| admin@cinemauq.com | admin123 | Administrador (ver **Reportes y estadísticas**) |
| laura@correo.com | 123456 | Cliente con saldo, compras y puntos (incluye un lote vencido y uno por vencer) |
| carlos@correo.com | 123456 | Cliente con poco saldo y una solicitud de recarga pendiente |
| sofia@correo.com | 123456 | Cliente con la tarjeta bloqueada |
| andres@, valentina@, juanpablo@, mariana@, santiago@, daniela@, felipe@, isabela@, tomas@, camila@, miguel@correo.com | 123456 | Clientes que participan en el historial de ventas |

Las funciones de ejemplo se calculan desde la fecha actual: siempre hay una que empieza en ~90 min (no se puede cancelar),
una en ~5 h (se devuelve el 80 %) y varias en los próximos días (100 %). Antes de hoy hay 21 días de operación simulados.

## Estructura (MVC)

```
co.edu.uniquindio.cinemauq
├── app            Launcher, CinemaUQApp (JavaFX) y Navegador entre vistas
├── controlador    Controladores JavaFX (Vista ↔ Servicios), ReportesController y Graficos
├── contexto       CinemaUQ (Singleton) y Sesion
├── modelo         Entidades del dominio (+ enums)
├── servicio       Reglas de negocio, políticas de reembolso (Strategy), Periodo y estadísticas
├── builder        CompraBuilder (Builder)
├── fabrica        FabricaEntrada y subclases (Factory Method)
├── fachada        TaquillaFacade (Facade)
├── precio         CalculadoraPrecio y decoradores de promociones (Decorator)
├── proxy          GestionCarteleraProxy y Auditoria (Proxy)
├── estado         EstadoDeCompra y estados concretos (State)
├── observador     ObservadorCompras, NotificadorCliente, MonitorOcupacion (Observer)
├── reporte        ServicioReportes, Reporte y exportadores TXT/PDF
├── repositorio    Repositorio<T> y su implementación en memoria
├── excepciones    Excepciones propias del dominio
├── datos          DatosIniciales e HistorialSimulado
└── util           Formato, GeneradorId, Reloj, RelojAjustable
resources/.../vista  Vistas FXML y estilos.css
```

## Patrones implementados (11)

| # | Patrón | Tipo | Dónde | Para qué |
|---|---|---|---|---|
| 1 | Singleton | Creacional | `contexto.CinemaUQ` | Una sola instancia del cine compartida por todas las pantallas |
| 2 | Factory Method | Creacional | `fabrica.FabricaEntrada` (+ General, Preferencial) | Crear el tipo de entrada correcto según el asiento |
| 3 | Builder | Creacional | `builder.CompraBuilder` | Armar la compra paso a paso desde la pantalla |
| 4 | Prototype | Creacional | `modelo.Funcion#clonar()` | Duplicar una función ya configurada para otra fecha |
| 5 | Composite | Estructural | `modelo.ItemConfiteria`, `Producto`, `Combo` | Combos que contienen productos y otros combos |
| 6 | Decorator | Estructural | paquete `precio` | Promociones que se apilan sobre el precio |
| 7 | Facade | Estructural | `fachada.TaquillaFacade` | Una sola puerta para cotizar, comprar y cancelar |
| 8 | Proxy | Estructural | `proxy.GestionCarteleraProxy` | Proteger y auditar los cambios de cartelera |
| 9 | Strategy | Comportamiento | `servicio.PoliticaReembolso` (+ `PoliticaPorAnticipacion`, `PoliticaReembolsoTotal`) | Reglas de reembolso intercambiables |
| 10 | State | Comportamiento | paquete `estado` | Ciclo de vida de la compra: impide pagos y reembolsos duplicados |
| 11 | Observer | Comportamiento | paquete `observador` | Avisar al cliente y alertar al administrador cuando una función supera el 80 % |

## Reportes y estadísticas

En la pestaña **Reportes y estadísticas** del administrador se elige un periodo y se ven 14 indicadores,
gráficos de líneas (ventas y recargas por día), barras (entradas por película y ocupación por sala) y pastel
(confitería). Hay 9 reportes que se exportan a `.txt` o `.pdf` (el PDF se genera sin librerías externas).

## Reglas propias (resumen)

- Puntos: 1 punto por cada $1.000 pagados en dinero. Para redimir, 1 punto = $100. Vencen al año.
- Cancelación del cliente: más de 24 h → 100 %; entre 2 y 24 h → 80 %; menos de 2 h → no se permite. Si el cine cancela la función → 100 %.
- Asiento preferencial: precio base + 30 %. Máximo 10 entradas por compra.
- Recargas entre $10.000 y $500.000. Funciones de una misma sala separadas por 20 min de limpieza.

El detalle completo (actores, RF, reglas, casos de uso, SOLID y patrones) está en el documento de cada entrega.
