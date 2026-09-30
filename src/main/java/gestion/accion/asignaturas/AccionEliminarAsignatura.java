package gestion.accion.asignaturas;

import gestion.accion.Accion;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción que elimina una asignatura.
 * Antes de borrar comprueba que no tenga alumnos matriculados:
 * si los tiene, muestra un mensaje de error y no realiza la eliminación.
 */
public class AccionEliminarAsignatura implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));

        // Validación: no se puede eliminar si tiene alumnos matriculados
        AsignaturaDAO dao = new AsignaturaDAO();
        int matriculados = new AlumnoDAO().contarMatriculados(id);
        if (matriculados > 0) {
            request.setAttribute("error",
                "No se puede eliminar: la asignatura tiene " + matriculados +
                " alumno(s) matriculado(s). Desmatrículos primero.");
            request.setAttribute("asignaturas", dao.listar());
            return "/WEB-INF/vistas/asignaturas/lista.jsp";
        }

        dao.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarAsignaturas");
        return null;
    }
}
