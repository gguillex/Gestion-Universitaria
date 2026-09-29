package gestion.accion.titulaciones;

import gestion.accion.Accion;
import gestion.modelo.TitulacionDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionEliminarTitulacion implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        TitulacionDAO dao = new TitulacionDAO();

        if (dao.tieneAsignaturas(id)) {
            request.setAttribute("error", "No se puede eliminar la titulación: tiene asignaturas asociadas");
            request.setAttribute("titulaciones", dao.listar());
            return "/WEB-INF/vistas/titulaciones/lista.jsp";
        }

        dao.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
        return null;
    }
}
