package gestion.rest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gestion.bean.Asignatura;
import gestion.bean.Titulacion;
import gestion.rest.exception.BadRequestException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class ParserObjectTest {

    @Test
    void laDescripcionAusenteOEnNullNoDaError() throws Exception {
        Titulacion sin = ParserObject.jsonToTitulacion(new JSONObject("{\"nombre\":\"Física\"}"));
        Titulacion nula = ParserObject.jsonToTitulacion(new JSONObject("{\"nombre\":\"Física\",\"descripcion\":null}"));

        assertNull(sin.getDescripcion());
        assertNull(nula.getDescripcion());
        assertTrue(ParserObject.titulacionToJSON(sin).isNull("descripcion"));
    }

    @Test
    void elIdProfesorPuedeVenirAusenteONulo() throws Exception {
        assertNull(ParserObject.jsonToAsignatura(new JSONObject(
                "{\"nombre\":\"Redes\",\"capacidadMaxima\":30,\"idTitulacion\":1}")).getIdProfesor());
        assertNull(ParserObject.jsonToAsignatura(new JSONObject(
                "{\"nombre\":\"Redes\",\"capacidadMaxima\":30,\"idTitulacion\":1,\"idProfesor\":null}")).getIdProfesor());
        assertEquals(4, ParserObject.jsonToAsignatura(new JSONObject(
                "{\"nombre\":\"Redes\",\"capacidadMaxima\":30,\"idTitulacion\":1,\"idProfesor\":4}")).getIdProfesor());
    }

    private static Asignatura asignatura(String capacidad) throws Exception {
        return ParserObject.jsonToAsignatura(new JSONObject(
                "{\"nombre\":\"Redes\",\"capacidadMaxima\":" + capacidad + ",\"idTitulacion\":1}"));
    }

    @Test
    void unaCapacidadQueNoEsUnEnteroValidoSeRechazaConUn400() {
        // org.json los aceptaría convirtiéndolos sin avisar: "30" → 30, 2.9 → 2, 4294967297 → 1
        for (String malo : new String[] {"\"abc\"", "\"30\"", "2.9", "30.0", "4294967297", "true", "null", "[1]"}) {
            BadRequestException e = assertThrows(BadRequestException.class, () -> asignatura(malo), malo);
            assertEquals(400, e.getHttpCode());
            assertTrue(e.getMessage().contains("capacidadMaxima"), malo);
        }
    }

    @Test
    void losEnterosValidosSeAceptan() throws Exception {
        assertEquals(30, asignatura("30").getCapacidadMaxima());
        assertEquals(2147483647, asignatura("2147483647").getCapacidadMaxima());
    }

    @Test
    void elNombreDebeSerUnTexto() {
        for (String malo : new String[] {"{\"a\":1}", "12", "true", "[\"x\"]"}) {
            JSONObject json = new JSONObject("{\"nombre\":" + malo + "}");
            assertThrows(BadRequestException.class, () -> ParserObject.jsonToTitulacion(json), malo);
            assertThrows(BadRequestException.class, () -> ParserObject.jsonToProfesor(json), malo);
        }
    }

    @Test
    void elIdProfesorTambienSeLeeConEstrictez() {
        JSONObject json = new JSONObject(
                "{\"nombre\":\"Redes\",\"capacidadMaxima\":30,\"idTitulacion\":1,\"idProfesor\":\"4\"}");
        assertThrows(BadRequestException.class, () -> ParserObject.jsonToAsignatura(json));
    }

    @Test
    void laRespuestaDeUnaAsignaturaIncluyeLosNombresDelJoin() {
        Asignatura a = new Asignatura(2, "Redes", 30, 1, 4);
        a.setNombreTitulacion("Informática");
        a.setNombreProfesor("Marta");

        JSONObject json = ParserObject.asignaturaToJSON(a);

        assertEquals("Informática", json.getString("nombreTitulacion"));
        assertEquals("Marta", json.getString("nombreProfesor"));
        assertEquals(30, json.getInt("capacidadMaxima"));
    }
}
