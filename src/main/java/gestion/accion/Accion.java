package gestion.accion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Contrato que debe cumplir toda acción del sistema.
 * Cada implementación encapsula la lógica de negocio de una operación concreta
 * y devuelve la ruta del JSP al que el controlador debe hacer forward,
 * o {@code null} si la acción ya realizó una redirección internamente.
 */
public interface Accion {

    /**
     * Ejecuta la lógica de la acción.
     * @return ruta del JSP al que hacer forward, o null si ya se hizo redirect.
     */
    String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
