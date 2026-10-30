# CinemaUQ

Sistema integral de gestión de cine · Programación II · Universidad del Quindío · 2026-2.

**Entrega 1 (parcial 1):** análisis, modelo del dominio, principios SOLID y patrones creacionales.


### Usuarios de prueba

| Correo | Contraseña | Perfil |
|---|---|---|
| admin@cinemauq.com | admin123 | Administrador |
| laura@correo.com | 123456 | Cliente con saldo, compras y puntos (incluye un lote vencido y uno por vencer) |
| carlos@correo.com | 123456 | Cliente con poco saldo y una solicitud de recarga pendiente |
| sofia@correo.com | 123456 | Cliente con la tarjeta bloqueada |

Las funciones de ejemplo se calculan desde la fecha actual: siempre hay una que empieza en ~90 min (no se puede cancelar), una en ~5 h (se devuelve el 80 %) y varias en los próximos días (100 %).

## Estructura (MVC)

```
co.edu.uniquindio.cinemauq
├── app            Launcher, CinemaUQApp (JavaFX) y Navegador entre vistas
├── controlador    Controladores JavaFX (Vista ↔ Servicios)
├── contexto       CinemaUQ (Singleton) y Sesion
├── modelo         Entidades del dominio (+ enums)
├── servicio       Reglas de negocio
├── builder        CompraBuilder (Builder)
├── fabrica        FabricaEntrada y subclases (Factory Method)
├── repositorio    Repositorio<T> y su implementación en memoria
├── excepciones    Excepciones propias del dominio
├── datos          Datos iniciales
└── util           Formato, GeneradorId, Reloj
resources/.../vista  Vistas FXML y estilos.css
```

## Patrones creacionales implementados

| Patrón | Dónde | Para qué |
|---|---|---|
| Singleton | `contexto.CinemaUQ` | Una sola instancia del cine compartida por todas las pantallas |
| Factory Method | `fabrica.FabricaEntrada` (+ General, Preferencial) | Crear el tipo de entrada correcto según el asiento |
| Builder | `builder.CompraBuilder` | Armar la compra paso a paso desde la pantalla |
| Prototype | `modelo.Funcion#clonar()` | Duplicar una función ya configurada para otra fecha |

## Reglas propias del grupo (resumen)

- Puntos: 1 punto por cada $1.000 pagados en dinero. Para redimir, 1 punto = $100.
- Asiento preferencial: precio base + 30 %. Máximo 10 entradas por compra.
- Recargas entre $10.000 y $500.000. Funciones de una misma sala separadas por 20 min de limpieza.

El detalle completo (actores, RF, reglas, casos de uso, SOLID y patrones) está en el documento de la entrega.
