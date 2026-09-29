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

        // Sin parámetros o por GET mostramos el formulario de asignación. Solo se
        // asigna por POST, que es donde el filtro comprueba el token CSRF.
        if (idAsigParam == null || !"POST".equals(request.getMethod())) {
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
