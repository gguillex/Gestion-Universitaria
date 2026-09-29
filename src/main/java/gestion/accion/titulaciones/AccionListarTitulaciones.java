package gestion.accion.titulaciones;

import gestion.accion.Accion;
import gestion.modelo.TitulacionDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionListarTitulaciones implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TitulacionDAO dao = new TitulacionDAO();
        request.setAttribute("titulaciones", dao.listar());
        return "/WEB-INF/vistas/titulaciones/lista.jsp";
    }
}
