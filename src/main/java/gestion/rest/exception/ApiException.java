package gestion.rest.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Excepción base del servicio REST. Lleva el código HTTP con el que se responde al
 * cliente; por defecto 500. {@code ErrorMapper} la traduce a {@code {"resultado": mensaje}}.
 */
public class ApiException extends Exception {

    private static final Logger LOG = Logger.getLogger(ApiException.class.getName());

    private int httpCode = 500;

    public ApiException(String message) {
        super(message);
    }

    public ApiException(int codigo, String mensaje) {
        super(mensaje);
        this.httpCode = codigo;
    }

    public int getHttpCode()          { return httpCode; }
    public void setHttpCode(int code) { this.httpCode = code; }

    /**
     * Error interno (500). El detalle técnico (SQL, rutas...) va al log del servidor y
     * nunca al cliente: solo recibe el mensaje genérico.
     */
    public static ApiException interno(String mensaje, Exception causa) {
        LOG.log(Level.SEVERE, mensaje, causa);
        return new ApiException(500, mensaje);
    }
}
