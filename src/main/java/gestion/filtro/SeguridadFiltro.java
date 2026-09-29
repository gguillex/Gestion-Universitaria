package gestion.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de seguridad que intercepta todas las peticiones antes de que lleguen
 * al controlador. Si no existe sesión activa y la acción solicitada no es pública
 * (login o autoregistro), redirige al formulario de inicio de sesión.
 */
@WebFilter("/*")
public class SeguridadFiltro implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String idAccion = request.getParameter("idAccion");
        HttpSession sesion = request.getSession(false);
        boolean estaLogueado = (sesion != null && sesion.getAttribute("usuarioLogueado") != null);

        // Login y autoregistro son accesibles sin sesión activa
        boolean esAccionPublica = "mostrarLogin".equals(idAccion) || "login".equals(idAccion)
                               || "mostrarRegistro".equals(idAccion) || "registro".equals(idAccion);
        String uri = request.getRequestURI();
        boolean esRecursoEstatico =
                uri.endsWith(".css") || uri.endsWith(".js")  ||
                uri.endsWith(".png") || uri.endsWith(".jpg") ||
                uri.endsWith(".gif") || uri.endsWith(".ico");
        boolean esIndex = uri.endsWith("/") || uri.endsWith("/index.jsp");

        if (estaLogueado || esAccionPublica || esRecursoEstatico || esIndex) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
        }
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}
