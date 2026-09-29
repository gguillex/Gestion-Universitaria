package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.modelo.AlumnoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción que recupera todos los alumnos del sistema y los envía a la vista de lista.
 */
public class AccionListarAlumnos implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AlumnoDAO dao = new AlumnoDAO();
        request.setAttribute("alumnos", dao.listar());
        return "/WEB-INF/vistas/alumnos/lista.jsp";
    }
}
