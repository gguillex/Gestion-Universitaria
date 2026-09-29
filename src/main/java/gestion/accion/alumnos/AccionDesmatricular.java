package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.modelo.AlumnoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción que desmatricula a un alumno de una asignatura.
 * Recibe {@code idAlumno} e {@code idAsignatura} y elimina la fila de la tabla matriculas.
 * Redirige al listado de matriculados de la asignatura afectada.
 */
public class AccionDesmatricular implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int idAlumno     = Integer.parseInt(request.getParameter("idAlumno"));
        int idAsignatura = Integer.parseInt(request.getParameter("idAsignatura"));

        new AlumnoDAO().desmatricular(idAlumno, idAsignatura);

        response.sendRedirect(request.getContextPath() +
            "/control?idAccion=matriculadosAsignatura&idAsignatura=" + idAsignatura);
        return null;
    }
}
