package gestion.accion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionLogout implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) sesion.invalidate();
        response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
        return null;
    }
}
