package gestion.accion.profesores;

import gestion.accion.Accion;
import gestion.bean.Profesor;
import gestion.modelo.ProfesorDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionGuardarProfesor implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam = request.getParameter("id");
        String nombre  = request.getParameter("nombre");
        String email   = request.getParameter("email");

        Profesor p = new Profesor();
        p.setNombre(nombre);
        p.setEmail(email);

        ProfesorDAO dao = new ProfesorDAO();
        if (idParam != null && !idParam.isEmpty()) {
            p.setId(Integer.parseInt(idParam));
            dao.actualizar(p);
        } else {
            dao.insertar(p);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarProfesores");
        return null;
    }
}
