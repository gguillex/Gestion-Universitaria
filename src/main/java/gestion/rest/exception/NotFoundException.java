package gestion.rest.exception;

/** 404 — el recurso solicitado no existe. */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
        setHttpCode(404);
    }
}
