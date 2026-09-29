package gestion.accion.titulaciones;

import gestion.accion.Accion;
import gestion.bean.Titulacion;
import gestion.modelo.TitulacionDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionGuardarTitulacion implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam      = request.getParameter("id");
        String nombre       = request.getParameter("nombre");
        String descripcion  = request.getParameter("descripcion");

        Titulacion t = new Titulacion();
        t.setNombre(nombre);
        t.setDescripcion(descripcion);

        TitulacionDAO dao = new TitulacionDAO();
        if (idParam != null && !idParam.isEmpty()) {
            t.setId(Integer.parseInt(idParam));
            dao.actualizar(t);
        } else {
            dao.insertar(t);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
        return null;
    }
}
