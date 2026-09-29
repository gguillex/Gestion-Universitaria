package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.bean.Asignatura;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción de matriculación de un alumno en una asignatura.
 *
 * <p>Flujo:
 * <ul>
 *   <li>GET sin parámetros → muestra formulario de selección (alumno + asignatura).</li>
 *   <li>POST con {@code idAlumno} e {@code idAsignatura} → valida y matricula.</li>
 * </ul>
 *
 * <p>Validaciones:
 * <ul>
 *   <li>El alumno no puede estar ya matriculado en la misma asignatura.</li>
 *   <li>La asignatura no puede superar su capacidad máxima.</li>
 * </ul>
 */
public class AccionMatricular implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idAlumnoParam    = request.getParameter("idAlumno");
        String idAsignaturaParam = request.getParameter("idAsignatura");

        AlumnoDAO    alumnoDao    = new AlumnoDAO();
        AsignaturaDAO asignaturaDao = new AsignaturaDAO();

        // Sin parámetros o por GET → mostrar formulario. Solo se matricula por POST,
        // que es donde el filtro comprueba el token CSRF.
        if (idAlumnoParam == null || idAlumnoParam.isEmpty() || !"POST".equals(request.getMethod())) {
            request.setAttribute("alumnos",     alumnoDao.listar());
            request.setAttribute("asignaturas", asignaturaDao.listar());
            return "/WEB-INF/vistas/alumnos/matricular.jsp";
        }

        int idAlumno     = Integer.parseInt(idAlumnoParam);
        int idAsignatura = Integer.parseInt(idAsignaturaParam);

        // Validación 1: ya matriculado
        if (alumnoDao.estaMatriculado(idAlumno, idAsignatura)) {
            request.setAttribute("error", "El alumno ya está matriculado en esa asignatura.");
            request.setAttribute("alumnos",     alumnoDao.listar());
            request.setAttribute("asignaturas", asignaturaDao.listar());
            return "/WEB-INF/vistas/alumnos/matricular.jsp";
        }

        // Validación 2: capacidad máxima. Comprobación y matriculación atómicas
        // (bloqueo de fila) para que dos matriculaciones concurrentes no puedan
        // superar juntas el hueco disponible.
        boolean matriculado = alumnoDao.matricular(idAlumno, idAsignatura);
        if (!matriculado) {
            Asignatura asignatura = asignaturaDao.buscarPorId(idAsignatura);
            request.setAttribute("error",
                "No se puede matricular: la asignatura ha alcanzado su capacidad máxima (" +
                asignatura.getCapacidadMaxima() + " alumnos).");
            request.setAttribute("alumnos",     alumnoDao.listar());
            request.setAttribute("asignaturas", asignaturaDao.listar());
            return "/WEB-INF/vistas/alumnos/matricular.jsp";
        }

        response.sendRedirect(request.getContextPath() +
            "/control?idAccion=matriculadosAsignatura&idAsignatura=" + idAsignatura);
        return null;
    }
}
