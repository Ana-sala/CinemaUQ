from uml import *

PAT = "#e8f0fe"   # color de clases que participan en patrones
d = Diagrama("CinemaUQ · Diagrama de clases del dominio (paquete modelo)", nodesep=0.35, ranksep=0.6)

d.nodo("Identificable", clase("Identificable", (), ("+getId(): String {abstract}",), "interface"))
d.nodo("Usuario", clase("Usuario", ("-id: String", "-nombre: String", "-correo: String", "-contrasena: String"),
       ("+getRol(): Rol {abstract}", "+verificarContrasena(intento: String): boolean"), abstracta=True))
d.nodo("Cliente", clase("Cliente", ("-documento: String",), ("+agregarCompra(c: Compra): void", "+getTarjeta(): TarjetaVirtual", "+getCuentaPuntos(): CuentaPuntos")))
d.nodo("Administrador", clase("Administrador", (), ("+getRol(): Rol",)))
d.nodo("TarjetaVirtual", clase("TarjetaVirtual", ("-numero: String", "-saldo: double", "-estado: EstadoTarjeta"),
       ("+validarPago(valor: double): void", "+debitar(valor, desc, fecha): Movimiento", "+acreditar(tipo, valor, desc, fecha): Movimiento",
        "+bloquear(): void", "+activar(): void")))
d.nodo("Movimiento", clase("Movimiento", ("-id: String", "-fecha: LocalDateTime", "-tipo: TipoMovimiento", "-valor: double",
       "-descripcion: String", "-saldoResultante: double")))
d.nodo("CuentaPuntos", clase("CuentaPuntos", (), ("+acumular(puntos, fecha, origen): void", "+getDisponibles(hoy): int",
       "+getPorVencer(hoy, dias): int", "+redimir(puntos, hoy): void", "+anularAcumulacion(origen): int")))
d.nodo("AcumulacionPuntos", clase("AcumulacionPuntos", ("-puntosObtenidos: int", "-puntosDisponibles: int", "-fechaObtencion: LocalDate",
       "-fechaVencimiento: LocalDate", "-origen: String"), ("+estaVigente(hoy: LocalDate): boolean",)))
d.nodo("SolicitudRecarga", clase("SolicitudRecarga", ("-id: String", "-valor: double", "-fecha: LocalDateTime", "-estado: EstadoSolicitud"),
       ("+aprobar(): void", "+rechazar(): void")))
d.nodo("Pelicula", clase("Pelicula", ("-id: String", "-titulo: String", "-genero: Genero", "-clasificacion: Clasificacion",
       "-duracionMinutos: int", "-sinopsis: String", "-enCartelera: boolean")))
d.nodo("Sala", clase("Sala", ("-id: String", "-nombre: String", "-tipo: TipoSala", "-filas: int", "-asientosPorFila: int"),
       ("+buscarAsiento(codigo): Optional<Asiento>", "+getCapacidad(): int")))
d.nodo("Asiento", clase("Asiento", ("-fila: char", "-numero: int", "-tipo: TipoAsiento"), ("+getCodigo(): String",)))
d.nodo("Prototipo", clase("Prototipo<T>", (), ("+clonar(): T {abstract}",), "interface", color=PAT))
d.nodo("Funcion", clase("Funcion", ("-id: String", "-fechaHora: LocalDateTime", "-precioBase: double", "-estado: EstadoFuncion"),
       ("+clonar(): Funcion", "+clonarPara(fecha): Funcion", "+estaDisponible(a: Asiento): boolean", "+ocupar(a: Asiento): void",
        "+liberar(a: Asiento): void", "+admiteVentas(ahora): boolean", "+cancelar(): void"), color=PAT))
d.nodo("Entrada", clase("Entrada", ("-id: String", "-pagadaConPuntos: boolean"), ("+getPrecio(): double {abstract}", "+getTipo(): TipoAsiento {abstract}"),
       abstracta=True, color=PAT))
d.nodo("EntradaGeneral", clase("EntradaGeneral", (), ("+getPrecio(): double",), color=PAT))
d.nodo("EntradaPreferencial", clase("EntradaPreferencial", ("+RECARGO: double = 0.30 {static}",), ("+getPrecio(): double",), color=PAT))
d.nodo("ItemConfiteria", clase("ItemConfiteria", (), ("+getNombre(): String {abstract}", "+getPrecio(): double {abstract}", "+isDisponible(): boolean {abstract}"), "interface"))
d.nodo("Producto", clase("Producto", ("-id: String", "-nombre: String", "-precio: double", "-categoria: CategoriaProducto", "-disponible: boolean")))
d.nodo("Combo", clase("Combo", ("-id: String", "-nombre: String", "-porcentajeDescuento: double"), ("+agregarProducto(p: Producto): void", "+getPrecio(): double")))
d.nodo("LineaConfiteria", clase("LineaConfiteria", ("-cantidad: int", "-precioUnitario: double", "-pagadaConPuntos: boolean"), ("+getSubtotal(): double",)))
d.nodo("Compra", clase("Compra", ("-id: String", "-fecha: LocalDateTime", "-estado: EstadoCompra", "-valorPagado: double",
       "-puntosRedimidos: int", "-puntosGanados: int"),
       ("+getTotalBruto(): double", "+getValorAPagar(): double", "+marcarPagada(fecha, valor, puntos): void",
        "+registrarReembolso(r: Reembolso): void"), color=PAT))
d.nodo("Reembolso", clase("Reembolso", ("-id: String", "-fecha: LocalDateTime", "-porcentaje: int", "-valor: double",
       "-puntosDevueltos: int", "-motivo: MotivoReembolso")))
d.nodo("Promocion", clase("Promocion", ("-id: String", "-nombre: String", "-porcentajeDescuento: double", "-fechaInicio: LocalDate",
       "-fechaFin: LocalDate", "-activa: boolean"), ("+estaVigente(hoy): boolean",)))

# enumeraciones
enums = {
    "Rol": "CLIENTE, ADMINISTRADOR", "EstadoTarjeta": "ACTIVA, BLOQUEADA", "TipoMovimiento": "PAGO, RECARGA, REEMBOLSO",
    "EstadoSolicitud": "PENDIENTE, APROBADA, RECHAZADA", "Genero": "ACCION, ANIMACION, COMEDIA, DRAMA,\nTERROR, CIENCIA_FICCION, ...",
    "Clasificacion": "TODO_PUBLICO, MAYORES_7, ..., MAYORES_18", "TipoSala": "DOS_D, TRES_D, VIP", "TipoAsiento": "GENERAL, PREFERENCIAL",
    "EstadoFuncion": "PROGRAMADA, CANCELADA", "EstadoCompra": "PENDIENTE, PAGADA, CANCELADA",
    "MotivoReembolso": "CANCELACION_CLIENTE,\nFUNCION_CANCELADA", "CategoriaProducto": "CRISPETAS, BEBIDA, SNACK, DULCE"}
import html as _h
celdas=[]
items=list(enums.items())
filas=""
for i in range(0,len(items),3):
    filas+="<TR>"+"".join(f'<TD ALIGN="LEFT" BALIGN="LEFT"><FONT POINT-SIZE="8">«enumeration»</FONT><BR/><B>{_h.escape(n)}</B><BR/>{_h.escape(v).replace(chr(10),"<BR/>")}</TD>' for n,v in items[i:i+3])+"</TR>"
d.nodo("Enums", f'<<TABLE BORDER="1" CELLBORDER="1" CELLSPACING="0" CELLPADDING="5" COLOR="#8a93a6"><TR><TD COLSPAN="3" BGCOLOR="#f3f4f6"><B>Enumeraciones (modelo.enums)</B></TD></TR>{filas}</TABLE>>')

# herencia y realización
d.hereda("Cliente", "Usuario"); d.hereda("Administrador", "Usuario")
d.hereda("EntradaGeneral", "Entrada"); d.hereda("EntradaPreferencial", "Entrada")
d.implementa("Producto", "ItemConfiteria"); d.implementa("Combo", "ItemConfiteria")
d.implementa("Funcion", "Prototipo")
d.implementa("Usuario", "Identificable")
# composiciones
d.compone("Cliente", "TarjetaVirtual", "1")
d.compone("Cliente", "CuentaPuntos", "1")
d.compone("TarjetaVirtual", "Movimiento", "0..*")
d.compone("CuentaPuntos", "AcumulacionPuntos", "0..*")
d.compone("Sala", "Asiento", "1..*")
d.compone("Compra", "Entrada", "0..10")
d.compone("Compra", "LineaConfiteria", "0..*")
d.compone("Compra", "Reembolso", "0..1")
# agregaciones y asociaciones
d.agrega("Cliente", "Compra", "0..*", "1", "historial")
d.agrega("Combo", "Producto", "2..*", "0..*")
d.asocia("SolicitudRecarga", "Cliente", "1", "0..*", "solicita")
d.asocia("Funcion", "Pelicula", "1", "0..*")
d.asocia("Funcion", "Sala", "1", "0..*")
d.asocia("Funcion", "Asiento", "0..*", "", "ocupados")
d.asocia("Compra", "Funcion", "1", "0..*")
d.asocia("Entrada", "Asiento", "1", "0..*")
d.asocia("Entrada", "Funcion", "1", "0..*")
d.asocia("LineaConfiteria", "ItemConfiteria", "1", "0..*")
d.nota("n1", "Identificable también la implementan:\\nPelicula, Sala, Funcion, Entrada, Compra,\\nReembolso, Movimiento, SolicitudRecarga,\\nPromocion e ItemConfiteria.", "Identificable")
d.nota("n2", "Azul: clases que participan en patrones\\ncreacionales (Prototype en Funcion,\\nFactory Method crea Entrada,\\nBuilder construye Compra).", None)
d.guardar("1_diagrama_dominio")
