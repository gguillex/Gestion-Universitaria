package gestion.rest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gestion.rest.exception.ApiException;
import gestion.rest.exception.ConflictException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/** El cliente siempre recibe {"resultado": mensaje} con el código correcto, nunca un detalle interno. */
class ErrorMapperTest {

    private final ErrorMapper mapper = new ErrorMapper();

    private static String resultado(Response r) {
        return new JSONObject((String) r.getEntity()).getString("resultado");
    }

    @Test
    void unaApiExceptionConservaSuCodigoYMensaje() {
        Response r = mapper.toResponse(new ConflictException("tiene asignaturas"));

        assertEquals(409, r.getStatus());
        assertEquals("tiene asignaturas", resultado(r));
    }

    @Test
    void unCampoConTipoIncorrectoEsUn400() {
        Response r = mapper.toResponse(new JSONException("JSONObject[\"capacidadMaxima\"] is not a number"));

        assertEquals(400, r.getStatus());
        assertFalse(resultado(r).contains("capacidadMaxima"), "no se filtra el detalle del parser");
    }

    @Test
    void lasExcepcionesDeJaxRsConservanSuCodigo() {
        assertEquals(404, mapper.toResponse(new NotFoundException()).getStatus());
        assertEquals(405, mapper.toResponse(new NotAllowedException("GET")).getStatus());
    }

    @Test
    void unErrorInesperadoEsUn500SinDetalles() {
        Response r = mapper.toResponse(new IllegalStateException("password=secreta en jdbc:mysql://..."));

        assertEquals(500, r.getStatus());
        assertFalse(resultado(r).contains("secreta"));
    }

    @Test
    void elErrorInternoDelServicioNoLlevaElDetalleTecnico() {
        ApiException e = ApiException.interno("Error al listar las titulaciones", new Exception("SQL: tabla x"));

        assertEquals(500, e.getHttpCode());
        assertEquals("Error al listar las titulaciones", e.getMessage());
    }

    @Test
    void leerBodyRechazaJsonMalFormadoYCuerposEnormes() {
        ApiException malo = assertThrows(ApiException.class,
                () -> RestUtils.leerBody(new ByteArrayInputStream("{no es json".getBytes(StandardCharsets.UTF_8))));
        assertEquals(400, malo.getHttpCode());

        byte[] enorme = new byte[RestUtils.MAX_BODY + 1];
        ApiException grande = assertThrows(ApiException.class,
                () -> RestUtils.leerBody(new ByteArrayInputStream(enorme)));
        assertEquals(413, grande.getHttpCode());
    }

    @Test
    void leerBodyInterpretaUnJsonValidoEnUtf8() throws Exception {
        JSONObject json = RestUtils.leerBody(new ByteArrayInputStream(
                "{\"nombre\":\"Ingeniería\"}".getBytes(StandardCharsets.UTF_8)));

        assertTrue(json.getString("nombre").startsWith("Ingenier"));
        assertEquals("Ingeniería", json.getString("nombre"));
    }
}
