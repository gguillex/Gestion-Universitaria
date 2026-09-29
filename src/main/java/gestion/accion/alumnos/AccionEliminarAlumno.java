package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.modelo.AlumnoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción que elimina un alumno y todas sus matrículas asociadas.
 */
public class AccionEliminarAlumno implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        new AlumnoDAO().eliminar(id);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarAlumnos");
        return null;
    }
}
