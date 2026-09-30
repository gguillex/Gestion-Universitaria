package gestion.rest.controller;

import gestion.bean.Profesor;
import gestion.rest.exception.ApiException;
import gestion.rest.service.ProfesorService;
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
 * Servicio REST de profesores, bajo {@code /rest/profesor}.
 *
 * <pre>
 *  GET    /rest/profesor/listado      lista todos
 *  GET    /rest/profesor/datos/{id}   uno por id
 *  POST   /rest/profesor              alta (sin id en el cuerpo)
 *  PUT    /rest/profesor              modificación (con id en el cuerpo)
 *  DELETE /rest/profesor/{id}         baja (desvincula sus asignaturas)
 * </pre>
 */
@Path("/profesor")
@Produces(MediaType.APPLICATION_JSON)
public class ProfesorController {

    private final ProfesorService servicio = new ProfesorService();

    @GET
    @Path("/listado")
    public Response listado() throws ApiException {
        JSONArray lista = new JSONArray();
        for (Profesor p : servicio.listar()) lista.put(ParserObject.profesorToJSON(p));
        return RestUtils.ok(new JSONObject().put("profesores", lista));
    }

    @GET
    @Path("/datos/{id}")
    public Response datos(@PathParam("id") int id) throws ApiException {
        return RestUtils.ok(new JSONObject()
                .put("profesor", ParserObject.profesorToJSON(servicio.obtenerPorId(id))));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alta(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (json.has("id"))
            throw new ApiException(409, "No envíes id en el alta: se genera automáticamente");
        Profesor creado = servicio.alta(ParserObject.jsonToProfesor(json));
        return RestUtils.ok(new JSONObject().put("profesor", ParserObject.profesorToJSON(creado)));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificar(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (!json.has("id"))
            throw new ApiException(400, "Debes enviar el id del profesor a modificar");
        Profesor modificado = servicio.modificar(ParserObject.jsonToProfesor(json));
        return RestUtils.ok(new JSONObject().put("profesor", ParserObject.profesorToJSON(modificado)));
    }

    @DELETE
    @Path("/{id}")
    public Response borrar(@PathParam("id") int id) throws ApiException {
        servicio.eliminar(id);
        return RestUtils.ok(new JSONObject().put("resultado", "Profesor eliminado"));
    }
}
