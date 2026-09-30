package gestion.rest.controller;

import gestion.bean.Asignatura;
import gestion.rest.exception.ApiException;
import gestion.rest.service.AsignaturaService;
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
 * Servicio REST de asignaturas, bajo {@code /rest/asignatura}: CRUD y asignación de profesor.
 *
 * <pre>
 *  GET    /rest/asignatura/listado          lista todas (con nombres de titulación y profesor)
 *  GET    /rest/asignatura/datos/{id}       una por id
 *  POST   /rest/asignatura                  alta (sin id en el cuerpo)
 *  PUT    /rest/asignatura                  modificación (con id en el cuerpo)
 *  PUT    /rest/asignatura/asignarProfesor  asigna o quita profesor {idAsignatura, idProfesor}
 *  DELETE /rest/asignatura/{id}             baja
 * </pre>
 */
@Path("/asignatura")
@Produces(MediaType.APPLICATION_JSON)
public class AsignaturaController {

    private final AsignaturaService servicio = new AsignaturaService();

    @GET
    @Path("/listado")
    public Response listado() throws ApiException {
        JSONArray lista = new JSONArray();
        for (Asignatura a : servicio.listar()) lista.put(ParserObject.asignaturaToJSON(a));
        return RestUtils.ok(new JSONObject().put("asignaturas", lista));
    }

    @GET
    @Path("/datos/{id}")
    public Response datos(@PathParam("id") int id) throws ApiException {
        return RestUtils.ok(new JSONObject()
                .put("asignatura", ParserObject.asignaturaToJSON(servicio.obtenerPorId(id))));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response alta(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (json.has("id"))
            throw new ApiException(409, "No envíes id en el alta: se genera automáticamente");
        Asignatura creada = servicio.alta(ParserObject.jsonToAsignatura(json));
        return RestUtils.ok(new JSONObject().put("asignatura", ParserObject.asignaturaToJSON(creada)));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modificar(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (!json.has("id"))
            throw new ApiException(400, "Debes enviar el id de la asignatura a modificar");
        Asignatura modificada = servicio.modificar(ParserObject.jsonToAsignatura(json));
        return RestUtils.ok(new JSONObject().put("asignatura", ParserObject.asignaturaToJSON(modificada)));
    }

    @PUT
    @Path("/asignarProfesor")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response asignarProfesor(InputStream cuerpo) throws ApiException {
        JSONObject json = RestUtils.leerBody(cuerpo);
        if (!json.has("idAsignatura"))
            throw new ApiException(400, "Falta idAsignatura");
        // idProfesor ausente o null = quitar el profesor
        Asignatura a = servicio.asignarProfesor(ParserObject.entero(json, "idAsignatura"),
                ParserObject.enteroOpcional(json, "idProfesor"));
        return RestUtils.ok(new JSONObject().put("asignatura", ParserObject.asignaturaToJSON(a)));
    }

    @DELETE
    @Path("/{id}")
    public Response borrar(@PathParam("id") int id) throws ApiException {
        servicio.eliminar(id);
        return RestUtils.ok(new JSONObject().put("resultado", "Asignatura eliminada"));
    }
}
