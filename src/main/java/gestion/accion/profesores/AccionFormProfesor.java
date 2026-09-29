package gestion.accion.profesores;

import gestion.accion.Accion;
import gestion.bean.Profesor;
import gestion.modelo.ProfesorDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionFormProfesor implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        Profesor p = new Profesor();
        if (idParam != null && !idParam.isEmpty()) {
            ProfesorDAO dao = new ProfesorDAO();
            Profesor encontrado = dao.buscarPorId(Integer.parseInt(idParam));
            if (encontrado != null) p = encontrado;
        }
        request.setAttribute("profesor", p);
        return "/WEB-INF/vistas/profesores/formulario.jsp";
    }
}
