package gestion.accion.asignaturas;

import gestion.accion.Accion;
import gestion.modelo.AsignaturaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionListarAsignaturas implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        AsignaturaDAO dao = new AsignaturaDAO();
        request.setAttribute("asignaturas", dao.listar());
        return "/WEB-INF/vistas/asignaturas/lista.jsp";
    }
}
