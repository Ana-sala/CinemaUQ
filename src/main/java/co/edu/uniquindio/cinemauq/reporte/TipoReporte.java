package co.edu.uniquindio.cinemauq.reporte;

/** Los reportes que se pueden generar y exportar (RF-025, RF-026). */
public enum TipoReporte {
    RESUMEN("Resumen operativo"),
    VENTAS_POR_DIA("Ventas por día"),
    ENTRADAS_POR_PELICULA("Entradas por película"),
    OCUPACION_POR_FUNCION("Ocupación por función"),
    OCUPACION_POR_SALA("Ocupación por sala"),
    CONFITERIA("Confitería vendida"),
    RECARGAS("Recargas de saldo"),
    CANCELACIONES("Cancelaciones y reembolsos"),
    PUNTOS("Programa de puntos");

    private final String titulo;

    TipoReporte(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
