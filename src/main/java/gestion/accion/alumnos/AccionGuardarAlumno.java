package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.bean.Alumno;
import gestion.modelo.AlumnoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción que persiste un alumno (inserción o actualización).
 * Distingue entre alta y edición según la presencia del parámetro {@code id}.
 */
public class AccionGuardarAlumno implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        String nombre  = request.getParameter("nombre");
        String email   = request.getParameter("email");
        String dni     = request.getParameter("dni");

        // Validación básica: el nombre es obligatorio
        if (nombre == null || nombre.trim().isEmpty()) {
            request.setAttribute("error", "El nombre del alumno es obligatorio.");
            request.setAttribute("alumno", new Alumno());
            return "/WEB-INF/vistas/alumnos/formulario.jsp";
        }

        Alumno alumno = new Alumno();
        alumno.setNombre(nombre.trim());
        alumno.setEmail(email != null ? email.trim() : "");
        alumno.setDni(dni != null ? dni.trim() : "");

        AlumnoDAO dao = new AlumnoDAO();
        if (idParam != null && !idParam.isEmpty()) {
            alumno.setId(Integer.parseInt(idParam));
            dao.actualizar(alumno);
        } else {
            dao.insertar(alumno);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarAlumnos");
        return null;
    }
}
