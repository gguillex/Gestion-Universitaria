package gestion.accion.profesores;

import gestion.accion.Accion;
import gestion.modelo.ProfesorDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionListarProfesores implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProfesorDAO dao = new ProfesorDAO();
        request.setAttribute("profesores", dao.listar());
        return "/WEB-INF/vistas/profesores/lista.jsp";
    }
}
