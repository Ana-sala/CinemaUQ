package co.edu.uniquindio.cinemauq.reporte;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Exporta el reporte como texto plano UTF-8 con columnas alineadas. */
public class ExportadorTxt implements ExportadorReporte {

    @Override
    public String extension() {
        return "txt";
    }

    @Override
    public String descripcion() {
        return "Texto (.txt)";
    }

    @Override
    public void exportar(Reporte reporte, Path destino) throws IOException {
        Files.writeString(destino, formatear(reporte), StandardCharsets.UTF_8);
    }

    /** Texto del reporte; también se usa para la vista previa en pantalla. */
    public String formatear(Reporte r) {
        List<String> cols = r.getColumnas();
        int[] ancho = new int[cols.size()];
        for (int i = 0; i < cols.size(); i++) {
            ancho[i] = cols.get(i).length();
            for (List<String> fila : r.getFilas()) {
                ancho[i] = Math.max(ancho[i], fila.get(i).length());
            }
        }
        int total = 0;
        for (int a : ancho) {
            total += a + 2;
        }
        String linea = "-".repeat(Math.max(total, 40));
        StringBuilder sb = new StringBuilder();
        sb.append("CINEMAUQ · ").append(r.getTitulo().toUpperCase()).append('\n');
        sb.append("Periodo: ").append(r.getPeriodo()).append('\n');
        sb.append("Generado: ").append(Formato.fechaHora(r.getGenerado())).append('\n');
        sb.append(linea).append('\n');
        sb.append(fila(cols, ancho, r)).append('\n').append(linea).append('\n');
        if (r.getFilas().isEmpty()) {
            sb.append("(Sin datos en el periodo)\n");
        }
        for (List<String> f : r.getFilas()) {
            sb.append(fila(f, ancho, r)).append('\n');
        }
        sb.append(linea).append('\n');
        int etiqueta = r.getTotales().stream().mapToInt(t -> t[0].length()).max().orElse(0);
        for (String[] t : r.getTotales()) {
            sb.append(String.format("%-" + (etiqueta + 1) + "s %s", t[0] + ":", t[1])).append('\n');
        }
        return sb.toString();
    }

    private String fila(List<String> valores, int[] ancho, Reporte r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            String formato = r.esNumerica(i) ? "%" + ancho[i] + "s" : "%-" + ancho[i] + "s";
            sb.append(String.format(formato, valores.get(i)));
            if (i < valores.size() - 1) {
                sb.append("  ");
            }
        }
        return sb.toString().stripTrailing();
    }
}
