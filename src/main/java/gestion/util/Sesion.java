package gestion.util;

import gestion.bean.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Acceso al usuario de la sesión y comprobación del rol de administrador. */
public final class Sesion {

    public static final String ATRIBUTO_USUARIO = "usuarioLogueado";

    private Sesion() {}

    /**
     * Devuelve el usuario de la sesión si es administrador. Si no lo es, redirige
     * al listado de titulaciones y devuelve {@code null}: la acción debe terminar.
     */
    public static Usuario exigirAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute(ATRIBUTO_USUARIO) : null;
        if (usuario == null || !"admin".equals(usuario.getRol())) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }
        return usuario;
    }
}
