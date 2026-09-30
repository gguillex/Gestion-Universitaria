package gestion.rest.exception;

/** 409 — conflicto: dato duplicado, violación de una regla de negocio o de integridad. */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(message);
        setHttpCode(409);
    }
}
