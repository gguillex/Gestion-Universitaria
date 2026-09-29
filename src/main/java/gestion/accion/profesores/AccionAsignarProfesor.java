package gestion.accion.profesores;

import gestion.accion.Accion;
import gestion.modelo.AsignaturaDAO;
import gestion.modelo.ProfesorDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionAsignarProfesor implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idAsigParam = request.getParameter("idAsignatura");
        String idProfParam = request.getParameter("idProfesor");

        // Si no hay parámetros aún, mostramos el formulario de asignación
        if (idAsigParam == null) {
            request.setAttribute("asignaturas", new AsignaturaDAO().listar());
            request.setAttribute("profesores",  new ProfesorDAO().listar());
            return "/WEB-INF/vistas/profesores/asignar.jsp";
        }

        int idAsignatura = Integer.parseInt(idAsigParam);
        Integer idProfesor = (idProfParam != null && !idProfParam.isEmpty())
                ? Integer.parseInt(idProfParam) : null;

        AsignaturaDAO dao = new AsignaturaDAO();
        dao.asignarProfesor(idAsignatura, idProfesor);

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarAsignaturas");
        return null;
    }
}
