package gestion.accion.asignaturas;

import gestion.accion.Accion;
import gestion.bean.Asignatura;
import gestion.modelo.AsignaturaDAO;
import gestion.modelo.ProfesorDAO;
import gestion.modelo.TitulacionDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionFormAsignatura implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        Asignatura a = new Asignatura();
        a.setCapacidadMaxima(30);
        if (idParam != null && !idParam.isEmpty()) {
            AsignaturaDAO dao = new AsignaturaDAO();
            Asignatura encontrada = dao.buscarPorId(Integer.parseInt(idParam));
            if (encontrada != null) a = encontrada;
        }

        request.setAttribute("asignatura",  a);
        request.setAttribute("titulaciones", new TitulacionDAO().listar());
        request.setAttribute("profesores",   new ProfesorDAO().listar());
        return "/WEB-INF/vistas/asignaturas/formulario.jsp";
    }
}
