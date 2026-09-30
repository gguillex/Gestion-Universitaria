package gestion.rest.util;

import gestion.rest.exception.ApiException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

/**
 * Traduce cualquier excepción del servicio REST a {@code {"resultado": mensaje}} con el
 * código HTTP que corresponde, de modo que el cliente nunca recibe una página de error
 * HTML ni un detalle interno. Los controllers solo lanzan {@link ApiException}.
 */
@Provider
public class ErrorMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(ErrorMapper.class.getName());

    @Override
    public Response toResponse(Throwable e) {
        if (e instanceof ApiException api) {
            return RestUtils.error(api);
        }
        if (e instanceof JSONException) {
            // Un campo con el tipo equivocado (p. ej. capacidadMaxima: "abc")
            return RestUtils.error(400, "El JSON tiene campos con un tipo o valor no válido");
        }
        if (e instanceof WebApplicationException web) {
            // 404 (ruta o id no numérico), 405 (verbo), 415 (no es application/json)...
            int codigo = web.getResponse().getStatus();
            return RestUtils.error(codigo, mensajeEstandar(codigo));
        }
        LOG.log(Level.SEVERE, "Error inesperado en el servicio REST", e);
        return RestUtils.error(500, "Error interno del servidor");
    }

    private static String mensajeEstandar(int codigo) {
        Response.Status estado = Response.Status.fromStatusCode(codigo);
        return estado != null ? estado.getReasonPhrase() : "Error " + codigo;
    }
}
