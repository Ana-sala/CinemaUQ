package co.edu.uniquindio.cinemauq.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Genera identificadores legibles por prefijo: CMP-0001, FUN-0002, ... */
public final class GeneradorId {

    private static final Map<String, AtomicInteger> CONTADORES = new ConcurrentHashMap<>();

    private GeneradorId() {
    }

    public static String siguiente(String prefijo) {
        int n = CONTADORES.computeIfAbsent(prefijo, p -> new AtomicInteger()).incrementAndGet();
        return String.format("%s-%04d", prefijo, n);
    }
}
