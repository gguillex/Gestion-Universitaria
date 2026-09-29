package gestion.accion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acción pública (no requiere sesión) que muestra el formulario de autoregistro.
 */
public class AccionMostrarRegistro implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "/WEB-INF/vistas/registro.jsp";
    }
}
