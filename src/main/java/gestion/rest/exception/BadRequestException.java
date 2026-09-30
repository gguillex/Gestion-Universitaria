package gestion.rest.exception;

/** 400 — la petición es inválida (faltan campos obligatorios o tienen valores incorrectos). */
public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message);
        setHttpCode(400);
    }
}
