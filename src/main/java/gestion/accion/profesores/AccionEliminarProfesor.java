package gestion.accion.profesores;

import gestion.accion.Accion;
import gestion.modelo.ProfesorDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionEliminarProfesor implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        ProfesorDAO dao = new ProfesorDAO();
        dao.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarProfesores");
        return null;
    }
}
