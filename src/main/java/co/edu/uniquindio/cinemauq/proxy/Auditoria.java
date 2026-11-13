package co.edu.uniquindio.cinemauq.proxy;

import co.edu.uniquindio.cinemauq.modelo.RegistroAuditoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Bitácora de operaciones de administración que escribe el proxy. */
public class Auditoria {

    private final List<RegistroAuditoria> registros = new ArrayList<>();

    public void registrar(RegistroAuditoria registro) {
        registros.add(registro);
    }

    /** Más recientes primero. */
    public List<RegistroAuditoria> listar() {
        List<RegistroAuditoria> copia = new ArrayList<>(registros);
        Collections.reverse(copia);
        return copia;
    }
}
