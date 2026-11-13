package co.edu.uniquindio.cinemauq.reporte;

import co.edu.uniquindio.cinemauq.util.Formato;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Exporta el reporte como PDF sin librerías externas. Escribe directamente los objetos del
 * formato PDF 1.4 con las fuentes estándar Helvetica y Helvetica-Bold (codificación
 * WinAnsi, que incluye tildes y ñ). Hoja carta vertical: encabezado vino y dorado, tabla
 * con filas alternadas, encabezado de la tabla repetido en cada página, totales al final
 * y «Página i de n».
 */
public class ExportadorPdf implements ExportadorReporte {

    private static final float ANCHO = 612, ALTO = 792, MARGEN = 40;
    private static final float TAM = 9, ALTO_FILA = 16, ALTO_BANDA = 64;
    private static final Charset WIN = Charset.forName("windows-1252");

    // Anchos de Helvetica y Helvetica-Bold (unidades de 1/1000), caracteres 32 a 126.
    private static final int[] NORMAL = {278, 278, 355, 556, 556, 889, 667, 191, 333, 333, 389, 584, 278, 333, 278, 278,
            556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 278, 278, 584, 584, 584, 556, 1015, 667, 667, 722, 722, 667,
            611, 778, 722, 278, 500, 667, 556, 833, 722, 778, 667, 778, 722, 667, 611, 722, 667, 944, 667, 667, 611, 278,
            278, 278, 469, 556, 333, 556, 556, 500, 556, 556, 278, 556, 556, 222, 222, 500, 222, 833, 556, 556, 556, 556,
            333, 500, 278, 556, 500, 722, 500, 500, 500, 334, 260, 334, 584};
    private static final int[] NEGRITA = {278, 333, 474, 556, 556, 889, 722, 238, 333, 333, 389, 584, 278, 333, 278, 278,
            556, 556, 556, 556, 556, 556, 556, 556, 556, 556, 333, 333, 584, 584, 584, 611, 975, 722, 722, 722, 722, 667,
            611, 778, 722, 278, 556, 722, 611, 833, 722, 778, 667, 778, 722, 667, 611, 722, 667, 944, 667, 667, 611, 333,
            278, 333, 584, 556, 333, 556, 611, 556, 611, 556, 333, 611, 611, 278, 278, 556, 278, 889, 611, 611, 611, 611,
            389, 556, 333, 611, 556, 778, 556, 556, 500, 389, 280, 389, 584};

    @Override
    public String extension() {
        return "pdf";
    }

    @Override
    public String descripcion() {
        return "PDF (.pdf)";
    }

    @Override
    public void exportar(Reporte reporte, Path destino) throws IOException {
        Files.write(destino, generar(reporte));
    }

    /** Bytes del PDF (se separa de exportar para poder probarlo sin escribir archivos). */
    public byte[] generar(Reporte r) {
        float[] anchos = anchosColumnas(r);
        List<List<List<String>>> paginas = paginar(r);
        int n = paginas.size();
        List<String> contenidos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            contenidos.add(pagina(r, paginas.get(i), anchos, i + 1, n, i == n - 1));
        }
        return documento(contenidos);
    }

    // ===================== Diseño de cada página =====================

    private List<List<List<String>>> paginar(Reporte r) {
        float inicioTabla = ALTO - ALTO_BANDA - 50;
        int porPagina = (int) ((inicioTabla - MARGEN - 30) / ALTO_FILA) - 1;
        int filasTotales = r.getTotales().size() + 2;
        List<List<List<String>>> paginas = new ArrayList<>();
        List<List<String>> actual = new ArrayList<>();
        for (List<String> fila : r.getFilas()) {
            if (actual.size() == porPagina) {
                paginas.add(actual);
                actual = new ArrayList<>();
            }
            actual.add(fila);
        }
        paginas.add(actual);
        if (actual.size() + filasTotales > porPagina) {   // los totales no caben: van en una página aparte
            paginas.add(new ArrayList<>());
        }
        return paginas;
    }

    private String pagina(Reporte r, List<List<String>> filas, float[] anchos, int numero, int total, boolean ultima) {
        StringBuilder s = new StringBuilder();
        // Banda vino con línea dorada
        rect(s, 0, ALTO - ALTO_BANDA, ANCHO, ALTO_BANDA, "0.482 0.118 0.169");
        rect(s, 0, ALTO - ALTO_BANDA - 4, ANCHO, 4, "0.831 0.627 0.090");
        texto(s, MARGEN, ALTO - 30, "CINEMAUQ", true, 10, "0.957 0.816 0.435");
        texto(s, MARGEN, ALTO - 50, r.getTitulo(), true, 18, "1 1 1");
        texto(s, MARGEN, ALTO - ALTO_BANDA - 22, "Periodo: " + r.getPeriodo() + "     Generado: "
                + Formato.fechaHora(r.getGenerado()), false, 9, "0.42 0.36 0.37");

        float y = ALTO - ALTO_BANDA - 50;
        // Encabezado de la tabla (se repite en cada página)
        rect(s, MARGEN, y - 4, ANCHO - 2 * MARGEN, ALTO_FILA, "0.482 0.118 0.169");
        celdas(s, r, r.getColumnas(), anchos, y, true, "1 1 1");
        y -= ALTO_FILA;
        boolean par = false;
        for (List<String> fila : filas) {
            if (par) {
                rect(s, MARGEN, y - 4, ANCHO - 2 * MARGEN, ALTO_FILA, "0.965 0.937 0.894");
            }
            celdas(s, r, fila, anchos, y, false, "0.17 0.10 0.10");
            y -= ALTO_FILA;
            par = !par;
        }
        if (r.getFilas().isEmpty() && numero == 1) {
            texto(s, MARGEN + 4, y, "Sin datos en el periodo.", false, TAM, "0.42 0.36 0.37");
            y -= ALTO_FILA;
        }
        linea(s, MARGEN, y + ALTO_FILA - 4, ANCHO - MARGEN, "0.831 0.627 0.090");
        if (ultima) {
            y -= 10;
            for (String[] t : r.getTotales()) {
                texto(s, MARGEN + 4, y, t[0] + ":", true, 10, "0.482 0.118 0.169");
                texto(s, MARGEN + 190, y, t[1], false, 10, "0.17 0.10 0.10");
                y -= ALTO_FILA;
            }
        }
        // Pie de página
        linea(s, MARGEN, 34, ANCHO - MARGEN, "0.831 0.627 0.090");
        texto(s, MARGEN, 22, "CinemaUQ · Universidad del Quindío", false, 8, "0.42 0.36 0.37");
        String pag = "Página " + numero + " de " + total;
        texto(s, ANCHO - MARGEN - ancho(pag, 8, false), 22, pag, false, 8, "0.42 0.36 0.37");
        return s.toString();
    }

    private void celdas(StringBuilder s, Reporte r, List<String> valores, float[] anchos, float y, boolean negrita, String color) {
        float x = MARGEN;
        for (int i = 0; i < valores.size(); i++) {
            String v = recortar(valores.get(i), anchos[i] - 8, negrita);
            float tx = r.esNumerica(i) ? x + anchos[i] - 4 - ancho(v, TAM, negrita) : x + 4;
            texto(s, tx, y, v, negrita, TAM, color);
            x += anchos[i];
        }
    }

    /** Reparte el ancho útil según el texto más largo de cada columna. */
    private float[] anchosColumnas(Reporte r) {
        int n = r.getColumnas().size();
        float[] a = new float[n];
        float suma = 0;
        for (int i = 0; i < n; i++) {
            a[i] = ancho(r.getColumnas().get(i), TAM, true) + 12;
            for (List<String> f : r.getFilas()) {
                a[i] = Math.max(a[i], ancho(f.get(i), TAM, false) + 12);
            }
            suma += a[i];
        }
        float util = ANCHO - 2 * MARGEN;
        for (int i = 0; i < n; i++) {
            a[i] = a[i] * util / suma;
        }
        return a;
    }

    private String recortar(String v, float max, boolean negrita) {
        if (ancho(v, TAM, negrita) <= max) {
            return v;
        }
        String t = v;
        while (!t.isEmpty() && ancho(t + "…", TAM, negrita) > max) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "…";
    }

    // ===================== Primitivas de dibujo =====================

    private void rect(StringBuilder s, float x, float y, float w, float h, String rgb) {
        s.append(rgb).append(" rg ").append(f(x)).append(' ').append(f(y)).append(' ').append(f(w)).append(' ')
                .append(f(h)).append(" re f\n");
    }

    private void linea(StringBuilder s, float x1, float y, float x2, String rgb) {
        s.append(rgb).append(" RG 0.8 w ").append(f(x1)).append(' ').append(f(y)).append(" m ").append(f(x2)).append(' ')
                .append(f(y)).append(" l S\n");
    }

    private void texto(StringBuilder s, float x, float y, String t, boolean negrita, float tam, String rgb) {
        s.append("BT ").append(rgb).append(" rg /").append(negrita ? "F2 " : "F1 ").append(f(tam)).append(" Tf ")
                .append(f(x)).append(' ').append(f(y)).append(" Td (").append(escapar(t)).append(") Tj ET\n");
    }

    static float ancho(String texto, float tam, boolean negrita) {
        int[] tabla = negrita ? NEGRITA : NORMAL;
        float total = 0;
        for (char c : texto.toCharArray()) {
            total += anchoCaracter(c, tabla);
        }
        return total * tam / 1000f;
    }

    private static int anchoCaracter(char c, int[] tabla) {
        if (c >= 32 && c <= 126) {
            return tabla[c - 32];
        }
        switch (c) {
            case '…', '—': return 1000;
            case '–', '€': return 556;
            case '·': return 278;
            case '°': return 400;
            case '¡': return 333;
            case '¿': return 611;
            default:
                String base = Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
                return !base.isEmpty() && base.charAt(0) >= 32 && base.charAt(0) <= 126 ? tabla[base.charAt(0) - 32] : 556;
        }
    }

    /** Escapa el texto para un string PDF; los bytes se escriben luego en WinAnsi. */
    private static String escapar(String t) {
        StringBuilder sb = new StringBuilder();
        for (char c : t.toCharArray()) {
            if (c == '(' || c == ')' || c == '\\') {
                sb.append('\\');
            }
            sb.append(WIN.newEncoder().canEncode(c) ? c : '?');
        }
        return sb.toString();
    }

    private static String f(float v) {
        return String.format(Locale.ROOT, "%.2f", v);
    }

    // ===================== Estructura del archivo PDF =====================

    private byte[] documento(List<String> contenidos) {
        int n = contenidos.size();
        // Objetos: 1 catálogo, 2 páginas, 3 Helvetica, 4 Helvetica-Bold, luego (página, contenido) por cada página
        List<byte[]> objetos = new ArrayList<>();
        objetos.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) {
            kids.append(5 + 2 * i).append(" 0 R ");
        }
        objetos.add(ascii("<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + n + " >>"));
        objetos.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        objetos.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>"));
        for (int i = 0; i < n; i++) {
            objetos.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) ANCHO + " " + (int) ALTO + "] "
                    + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + (6 + 2 * i) + " 0 R >>"));
            byte[] flujo = contenidos.get(i).getBytes(WIN);
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            o.writeBytes(ascii("<< /Length " + flujo.length + " >>\nstream\n"));
            o.writeBytes(flujo);
            o.writeBytes(ascii("\nendstream"));
            objetos.add(o.toByteArray());
        }
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.writeBytes(ascii("%PDF-1.4\n%âãÏÓ\n"));
        int[] offsets = new int[objetos.size()];
        for (int i = 0; i < objetos.size(); i++) {
            offsets[i] = pdf.size();
            pdf.writeBytes(ascii((i + 1) + " 0 obj\n"));
            pdf.writeBytes(objetos.get(i));
            pdf.writeBytes(ascii("\nendobj\n"));
        }
        int xref = pdf.size();
        StringBuilder x = new StringBuilder("xref\n0 " + (objetos.size() + 1) + "\n0000000000 65535 f \n");
        for (int off : offsets) {
            x.append(String.format("%010d 00000 n \n", off));
        }
        x.append("trailer\n<< /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n");
        pdf.writeBytes(ascii(x.toString()));
        return pdf.toByteArray();
    }

    private static byte[] ascii(String s) {
        return s.getBytes(Charset.forName("ISO-8859-1"));
    }
}
