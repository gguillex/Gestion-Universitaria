package gestion.accion.titulaciones;

import gestion.accion.Accion;
import gestion.bean.Titulacion;
import gestion.modelo.TitulacionDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionFormTitulacion implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        Titulacion t = new Titulacion();
        if (idParam != null && !idParam.isEmpty()) {
            int id = Integer.parseInt(idParam);
            TitulacionDAO dao = new TitulacionDAO();
            t = dao.buscarPorId(id);
            if (t == null) t = new Titulacion();
        }
        request.setAttribute("titulacion", t);
        return "/WEB-INF/vistas/titulaciones/formulario.jsp";
    }
}
