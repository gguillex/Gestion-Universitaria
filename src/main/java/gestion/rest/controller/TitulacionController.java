package gestion.rest.controller;

import gestion.bean.Titulacion;
import gestion.rest.exception.ApiException;
import gestion.rest.service.TitulacionService;
import gestion.rest.util.ParserObject;
import gestion.rest.util.RestUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servicio REST de titulaciones, bajo {@code /rest/titulacion}. Solo traduce HTTP y JSON:
 * la lógica está en {@link TitulacionService} y los errores los convierte {@code ErrorMapper}.
 *
 * <pre>
 *  GET    /rest/titulacion/listado      lista todas
 *  GET    /rest/titulacion/datos/{id}   una por id
 *  POST   /rest/titulacion              alta (sin id en el cuerpo)
 *  PUT    /rest/titulacion              modificación (con id en el cuerpo)
 *  DELETE /rest/titulacion/{id}         baja
 * </pre>
 */
@Path("/titulacion")
@Produces(MediaType.APPLICATION_JSON)
public class TitulacionController {

    private final TitulacionService servicio = new TitulacionService();

    @GET
    @Path("/listado")
    public Response listado() throws ApiException {
        JSONArray lista = new JSONArray();
        for (Titulacion t : servicio.listar()) lista.put(ParserObject.titulacionToJSON(t));
        return RestUtils.ok(new JSONObject().put("titulaciones", lista));
    }

    @GET
    @Path("/datos/{id}")
    public Response datos(@PathParam("id") int id) throws ApiException {
        return RestUtils.ok(new JSONObject()
                .put("titulacion", ParserObject.titulacionToJSON(servicio.obtenerPorId(id))));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alta(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (json.has("id"))
            throw new ApiException(409, "No envíes id en el alta: se genera automáticamente");
        Titulacion creada = servicio.alta(ParserObject.jsonToTitulacion(json));
        return RestUtils.ok(new JSONObject().put("titulacion", ParserObject.titulacionToJSON(creada)));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificar(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (!json.has("id"))
            throw new ApiException(400, "Debes enviar el id de la titulación a modificar");
        Titulacion modificada = servicio.modificar(ParserObject.jsonToTitulacion(json));
        return RestUtils.ok(new JSONObject().put("titulacion", ParserObject.titulacionToJSON(modificada)));
    }

    @DELETE
    @Path("/{id}")
    public Response borrar(@PathParam("id") int id) throws ApiException {
        servicio.eliminar(id);
        return RestUtils.ok(new JSONObject().put("resultado", "Titulación eliminada"));
    }
}
