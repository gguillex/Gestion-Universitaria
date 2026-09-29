package gestion.filtro;

import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Filtro de seguridad que intercepta todas las peticiones antes de que lleguen
 * al controlador.
 *
 * <ul>
 *   <li>Sin sesión activa, solo deja pasar las acciones públicas (login y
 *       autoregistro), el índice y los recursos estáticos de {@code /css/}.</li>
 *   <li>Las acciones que guardan o borran datos solo se aceptan por POST.</li>
 *   <li>Todo POST (con sesión, o de login/registro) debe llevar el token CSRF de
 *       la sesión, que las vistas envían en el campo oculto {@code csrfToken}.</li>
 *   <li>El usuario de la sesión se relee de la BD en cada petición, para que los
 *       cambios de rol y los borrados tengan efecto inmediato.</li>
 * </ul>
 */
@WebFilter("/*")
public class SeguridadFiltro implements Filter {

    /** Nombre del atributo de sesión y del parámetro de formulario del token CSRF. */
    public static final String ATRIBUTO_CSRF = "csrfToken";

    private static final SecureRandom RANDOM = new SecureRandom();

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

        // getServletPath() no incluye los parámetros de ruta (";x.css"), a diferencia
        // de getRequestURI(): comprobar la extensión sobre la URI permitiría saltarse
        // el filtro con /control;.css
        String ruta = request.getServletPath();
        boolean esRecursoEstatico = ruta.startsWith("/css/");
        boolean esIndex = ruta.equals("/") || ruta.equals("/index.jsp");

        if (!(estaLogueado || esAccionPublica || esRecursoEstatico || esIndex)) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
            return;
        }

        boolean esPost = "POST".equals(request.getMethod());

        // Guardar y borrar cambian datos: nunca por GET (enlaces, precargas, <img>...).
        // Además, el token CSRF solo se comprueba en POST, así que por GET se lo saltarían.
        if (esModificacion(idAccion) && !esPost) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // El rol y la existencia del usuario se leen de la BD en cada petición: si un
        // admin lo degrada o lo borra, el cambio se aplica ya, no al caducar la sesión.
        if (estaLogueado && !esRecursoEstatico) {
            Usuario actual = usuarioActualizado(sesion);
            if (actual == null) {
                sesion.invalidate();
                response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
                return;
            }
            sesion.setAttribute("usuarioLogueado", actual);
        }

        // Los formularios de login y registro también llevan token: se crea la
        // sesión (anónima) al mostrarlos. Al autenticarse se le cambia el id.
        if (esAccionPublica && !esPost && sesion == null) {
            sesion = request.getSession(true);
        }
        if (sesion != null && (estaLogueado || esAccionPublica)
                && sesion.getAttribute(ATRIBUTO_CSRF) == null) {
            sesion.setAttribute(ATRIBUTO_CSRF, nuevoToken());
        }

        // CSRF: todo POST (autenticado o de login/registro) debe traer el token de su sesión
        if (esPost && (estaLogueado || esAccionPublica) && !tokenValido(request, sesion)) {
            if (esAccionPublica) {
                // Lo normal aquí es un formulario abierto con una sesión ya caducada:
                // se vuelve a mostrar con un aviso en vez de un 403
                String destino = "registro".equals(idAccion) ? "mostrarRegistro" : "mostrarLogin";
                response.sendRedirect(request.getContextPath()
                        + "/control?idAccion=" + destino + "&caducado=1");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido");
            }
            return;
        }

        chain.doFilter(req, res);
    }

    /** Relee el usuario de la sesión en la BD; null si ya no existe. */
    private static Usuario usuarioActualizado(HttpSession sesion) throws ServletException {
        Usuario enSesion = (Usuario) sesion.getAttribute("usuarioLogueado");
        try {
            Usuario actual = new UsuarioDAO().buscarPorId(enSesion.getId());
            if (actual != null) actual.setPassword(null); // el hash nunca viaja en la sesión
            return actual;
        } catch (Exception e) {
            throw new ServletException("No se pudo comprobar el usuario de la sesión", e);
        }
    }

    private static boolean esModificacion(String idAccion) {
        return idAccion != null
            && (idAccion.startsWith("guardar") || idAccion.startsWith("eliminar")
                || idAccion.equals("desmatricular")
                || idAccion.equals("login") || idAccion.equals("registro"));
    }

    private static boolean tokenValido(HttpServletRequest request, HttpSession sesion) {
        if (sesion == null) return false;
        Object esperado = sesion.getAttribute(ATRIBUTO_CSRF);
        String recibido = request.getParameter(ATRIBUTO_CSRF);
        return esperado != null && recibido != null
            && MessageDigest.isEqual(esperado.toString().getBytes(StandardCharsets.UTF_8),
                                     recibido.getBytes(StandardCharsets.UTF_8));
    }

    private static String nuevoToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    @Override public void init(FilterConfig cfg) {}
    @Override public void destroy() {}
}
