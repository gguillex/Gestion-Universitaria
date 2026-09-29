package gestion.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Cuenta fallos por clave (p. ej. IP + usuario) y bloquea la clave durante un
 * tiempo al llegar al máximo. Vive en memoria: se reinicia al reiniciar Tomcat.
 */
public final class LimitadorIntentos {

    private static final int MAX_CLAVES = 10_000;

    private record Estado(int fallos, long ultimoFallo, long bloqueadoHasta) {}

    private final int  maxFallos;
    private final long ventanaMs;
    private final ConcurrentHashMap<String, Estado> estados = new ConcurrentHashMap<>();

    /**
     * @param maxFallos fallos seguidos que provocan el bloqueo
     * @param ventanaMs duración del bloqueo; también el tiempo tras el que se
     *                  olvidan los fallos sueltos
     */
    public LimitadorIntentos(int maxFallos, long ventanaMs) {
        this.maxFallos = maxFallos;
        this.ventanaMs = ventanaMs;
    }

    /** Minutos (redondeados hacia arriba) que le quedan de bloqueo a la clave; 0 si no está bloqueada. */
    public long minutosBloqueado(String clave) {
        Estado e = estados.get(clave);
        if (e == null) return 0;
        long resto = e.bloqueadoHasta() - System.currentTimeMillis();
        return resto > 0 ? (resto + 59_999) / 60_000 : 0;
    }

    public void registrarFallo(String clave) {
        long ahora = System.currentTimeMillis();
        if (estados.size() >= MAX_CLAVES) limpiar(ahora);
        estados.compute(clave, (k, e) -> {
            // Fallos antiguos o un bloqueo ya cumplido no cuentan
            int previos = (e == null || ahora - e.ultimoFallo() > ventanaMs) ? 0 : e.fallos();
            int fallos = previos + 1;
            return fallos >= maxFallos
                    ? new Estado(0, ahora, ahora + ventanaMs)
                    : new Estado(fallos, ahora, 0);
        });
    }

    public void registrarExito(String clave) {
        estados.remove(clave);
    }

    /** Evita que la memoria crezca sin límite con claves que ya no importan. */
    private void limpiar(long ahora) {
        estados.entrySet().removeIf(en ->
                ahora - en.getValue().ultimoFallo() > ventanaMs
                && en.getValue().bloqueadoHasta() < ahora);
    }
}
