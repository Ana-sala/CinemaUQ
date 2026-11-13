package co.edu.uniquindio.cinemauq.reporte;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Formato de exportación de un reporte. La pantalla y el servicio solo conocen esta
 * interfaz: agregar otro formato (por ejemplo CSV) es una clase nueva (OCP, LSP).
 */
public interface ExportadorReporte {

    /** Extensión del archivo, sin punto ("txt", "pdf"). */
    String extension();

    /** Nombre del formato para mostrarlo en la pantalla. */
    String descripcion();

    /** Escribe el reporte en el archivo indicado. */
    void exportar(Reporte reporte, Path destino) throws IOException;
}
