package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.bean.Alumno;
import gestion.modelo.AlumnoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción que muestra el formulario de alta o edición de un alumno.
 * Si se recibe el parámetro {@code id}, carga los datos del alumno para edición.
 * Si no, prepara un alumno vacío para el alta.
 */
public class AccionFormAlumno implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        Alumno alumno = new Alumno();

        if (idParam != null && !idParam.isEmpty()) {
            Alumno encontrado = new AlumnoDAO().buscarPorId(Integer.parseInt(idParam));
            if (encontrado != null) alumno = encontrado;
        }

        request.setAttribute("alumno", alumno);
        return "/WEB-INF/vistas/alumnos/formulario.jsp";
    }
}
