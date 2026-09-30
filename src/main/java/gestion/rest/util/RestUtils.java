package gestion.rest.util;

import gestion.rest.exception.ApiException;
import gestion.rest.exception.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

/** Utilidades compartidas por los controllers REST: lectura del cuerpo y respuestas JSON. */
public final class RestUtils {

    /** Tamaño máximo del cuerpo de una petición: sobra para cualquier entidad de este dominio. */
    static final int MAX_BODY = 64 * 1024;

    private RestUtils() {}

    /** Lee el cuerpo de la petición y lo interpreta como {@link JSONObject}. */
    public static JSONObject leerBody(InputStream inputStream) throws ApiException {
        byte[] bytes;
        try {
            bytes = inputStream.readNBytes(MAX_BODY + 1);
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el cuerpo de la petición");
        }
        if (bytes.length > MAX_BODY) {
            throw new ApiException(413, "El cuerpo de la petición es demasiado grande");
        }
        try {
            return new JSONObject(new String(bytes, StandardCharsets.UTF_8));
        } catch (JSONException e) {
            throw new BadRequestException("El cuerpo de la petición no es un JSON válido");
        }
    }

    /** Respuesta correcta con un objeto JSON. */
    public static Response ok(JSONObject json) {
        return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
    }

    /** Respuesta de error {@code {"resultado": mensaje}} con el código HTTP indicado. */
    public static Response error(int codigo, String mensaje) {
        JSONObject json = new JSONObject();
        json.put("resultado", mensaje);
        return Response.status(codigo).type(MediaType.APPLICATION_JSON).entity(json.toString()).build();
    }

    public static Response error(ApiException e) {
        return error(e.getHttpCode(), e.getMessage());
    }
}
