package gestion.rest.util;

import gestion.bean.Asignatura;
import gestion.bean.Profesor;
import gestion.bean.Titulacion;
import gestion.rest.exception.BadRequestException;
import org.json.JSONObject;

/**
 * Conversión entre los beans del dominio (los mismos que usa la interfaz MVC) y
 * {@link JSONObject}. Los métodos {@code ...ToJSON} construyen la respuesta; los
 * {@code jsonTo...} leen lo que el cliente envía.
 *
 * <p>La lectura es estricta: {@code org.json} por sí solo convierte sin avisar (un
 * {@code "30"} en número, {@code 2.9} en 2, {@code 4294967297} en 1), así que los enteros
 * y los textos se comprueban aquí y un tipo equivocado es un 400 con su mensaje.
 */
public final class ParserObject {

    private ParserObject() {}

    // ---------------------------------------------------------------- lectura estricta

    /** Entero obligatorio: rechaza cadenas, decimales y valores fuera del rango de {@code int}. */
    public static int entero(JSONObject json, String clave) throws BadRequestException {
        Object valor = json.opt(clave);
        if (valor instanceof Integer i) return i;
        if (valor instanceof Long l && l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) return l.intValue();
        throw new BadRequestException("\"" + clave + "\" debe ser un número entero");
    }

    /** Entero opcional: ausente o null es {@code null}; si viene, debe ser un entero válido. */
    public static Integer enteroOpcional(JSONObject json, String clave) throws BadRequestException {
        return json.has(clave) && !json.isNull(clave) ? Integer.valueOf(entero(json, clave)) : null;
    }

    /** Texto opcional: ausente o null es {@code null}; si viene, debe ser una cadena. */
    public static String texto(JSONObject json, String clave) throws BadRequestException {
        if (!json.has(clave) || json.isNull(clave)) return null;
        if (json.get(clave) instanceof String s) return s;
        throw new BadRequestException("\"" + clave + "\" debe ser un texto");
    }

    // ---------------------------------------------------------------- Titulacion

    public static JSONObject titulacionToJSON(Titulacion t) {
        JSONObject json = new JSONObject();
        json.put("id",          t.getId());
        json.put("nombre",      t.getNombre());
        json.put("descripcion", t.getDescripcion() == null ? JSONObject.NULL : t.getDescripcion());
        return json;
    }

    public static Titulacion jsonToTitulacion(JSONObject json) throws BadRequestException {
        Titulacion t = new Titulacion();
        if (json.has("id")) t.setId(entero(json, "id"));
        t.setNombre(texto(json, "nombre"));
        t.setDescripcion(texto(json, "descripcion"));
        return t;
    }

    // ---------------------------------------------------------------- Profesor

    public static JSONObject profesorToJSON(Profesor p) {
        JSONObject json = new JSONObject();
        json.put("id",     p.getId());
        json.put("nombre", p.getNombre());
        json.put("email",  p.getEmail() == null ? JSONObject.NULL : p.getEmail());
        return json;
    }

    public static Profesor jsonToProfesor(JSONObject json) throws BadRequestException {
        Profesor p = new Profesor();
        if (json.has("id")) p.setId(entero(json, "id"));
        p.setNombre(texto(json, "nombre"));
        p.setEmail(texto(json, "email"));
        return p;
    }

    // ---------------------------------------------------------------- Asignatura

    public static JSONObject asignaturaToJSON(Asignatura a) {
        JSONObject json = new JSONObject();
        json.put("id",               a.getId());
        json.put("nombre",           a.getNombre());
        json.put("capacidadMaxima",  a.getCapacidadMaxima());
        json.put("idTitulacion",     a.getIdTitulacion());
        json.put("idProfesor",       a.getIdProfesor() == null ? JSONObject.NULL : a.getIdProfesor());
        // Campos de visualización que salen de los JOIN del DAO
        json.put("nombreTitulacion", a.getNombreTitulacion() == null ? JSONObject.NULL : a.getNombreTitulacion());
        json.put("nombreProfesor",   a.getNombreProfesor() == null ? JSONObject.NULL : a.getNombreProfesor());
        return json;
    }

    public static Asignatura jsonToAsignatura(JSONObject json) throws BadRequestException {
        Asignatura a = new Asignatura();
        if (json.has("id")) a.setId(entero(json, "id"));
        a.setNombre(texto(json, "nombre"));
        if (json.has("capacidadMaxima")) a.setCapacidadMaxima(entero(json, "capacidadMaxima"));
        if (json.has("idTitulacion"))    a.setIdTitulacion(entero(json, "idTitulacion"));
        // idProfesor puede venir ausente o explícitamente null (sin asignar)
        a.setIdProfesor(enteroOpcional(json, "idProfesor"));
        return a;
    }
}
