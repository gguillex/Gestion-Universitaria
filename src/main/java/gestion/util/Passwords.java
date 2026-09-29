package gestion.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash y verificación de contraseñas con PBKDF2-HMAC-SHA256 (incluido en el JDK).
 *
 * <p>Formato almacenado: {@code pbkdf2$iteraciones$salt(base64)$hash(base64)}.
 * Las contraseñas antiguas en texto plano (sin ese prefijo) se siguen aceptando
 * para poder migrarlas al primer login: ver {@link #esLegado(String)} y
 * {@link MigrarPasswords}.
 */
public final class Passwords {

    private static final String PREFIJO         = "pbkdf2";
    private static final int    ITERACIONES     = 210_000;
    private static final int    MAX_ITERACIONES = 10_000_000;
    private static final int    BITS_HASH       = 256;
    private static final int    BYTES_SALT      = 16;
    private static final SecureRandom RANDOM    = new SecureRandom();

    /** Hash de referencia para gastar el mismo tiempo cuando el usuario no existe. */
    private static final String HASH_FICTICIO = hashear("usuario-inexistente");

    private Passwords() {}

    public static String hashear(String password) {
        byte[] salt = new byte[BYTES_SALT];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(password, salt, ITERACIONES);
        return PREFIJO + "$" + ITERACIONES + "$"
             + Base64.getEncoder().encodeToString(salt) + "$"
             + Base64.getEncoder().encodeToString(hash);
    }

    /** True si el valor almacenado sigue siendo texto plano (formato anterior). */
    public static boolean esLegado(String almacenado) {
        return almacenado == null || !almacenado.startsWith(PREFIJO + "$");
    }

    public static boolean verificar(String password, String almacenado) {
        if (password == null || almacenado == null) return false;
        if (esLegado(almacenado)) {
            return MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8),
                                         almacenado.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] partes = almacenado.split("\\$");
            if (partes.length != 4) return false;
            int iteraciones = Integer.parseInt(partes[1]);
            if (iteraciones < 1 || iteraciones > MAX_ITERACIONES) return false;
            byte[] salt     = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            return MessageDigest.isEqual(esperado, derivar(password, salt, iteraciones));
        } catch (IllegalArgumentException e) {
            // Valor corrupto en la BD (número o Base64 inválido): no autentica
            return false;
        }
    }

    /**
     * Hace el mismo trabajo que una verificación real. Se llama cuando el usuario
     * no existe para que el tiempo de respuesta no revele qué nombres están dados de alta.
     */
    public static void simularVerificacion(String password) {
        verificar(password == null ? "" : password, HASH_FICTICIO);
    }

    private static byte[] derivar(String password, byte[] salt, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iteraciones, BITS_HASH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        }
    }
}
