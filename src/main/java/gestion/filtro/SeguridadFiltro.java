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
import java.util.logging.Level;
import java.util.logging.Logger;

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
 *   <li>El servicio REST ({@code /rest/*}) y su cliente ({@code /rest-ui/*}) siguen las
 *       mismas reglas: sin sesión, la API responde 401 en JSON y el cliente redirige al
 *       login; todo lo que no sea lectura (POST, PUT, DELETE) debe llevar el token CSRF
 *       de la sesión en la cabecera {@code X-CSRF-Token}.</li>
 * </ul>
 */
@WebFilter("/*")
public class SeguridadFiltro implements Filter {

    /** Nombre del atributo de sesión y del parámetro de formulario del token CSRF. */
    public static final String ATRIBUTO_CSRF = "csrfToken";

    /** Cabecera con la que el cliente REST envía el token CSRF (no hay formulario donde ponerlo). */
    public static final String CABECERA_CSRF = "X-CSRF-Token";

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Logger LOG = Logger.getLogger(SeguridadFiltro.class.getName());

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
        boolean esRest = ruta.equals("/rest") || ruta.startsWith("/rest/");

        if (esRest) {
            // Las respuestas de la API dependen de la sesión: nada de cachés compartidas
            response.setHeader("Cache-Control", "no-store");
            if (!estaLogueado) {
                errorJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Sesión no iniciada");
                return;
            }
        }

        if (!(estaLogueado || esAccionPublica || esRecursoEstatico || esIndex)) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
            return;
        }

        boolean esPost = "POST".equals(request.getMethod());

        // Guardar y borrar cambian datos: nunca por GET (enlaces, precargas, <img>...).
        // Además, el token CSRF solo se comprueba en POST, así que por GET se lo saltarían.
        // (La API REST modifica con PUT y DELETE: esta regla es de las acciones de /control.)
        if (!esRest && esModificacion(idAccion) && !esPost) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // El rol y la existencia del usuario se leen de la BD en cada petición: si un
        // admin lo degrada o lo borra, el cambio se aplica ya, no al caducar la sesión.
        if (estaLogueado && !esRecursoEstatico) {
            Usuario actual;
            try {
                actual = usuarioActualizado(sesion);
            } catch (ServletException e) {
                if (!esRest) throw e;
                // La API promete JSON siempre, también cuando falla la base de datos
                LOG.log(Level.SEVERE, "No se pudo comprobar el usuario de la sesión", e);
                errorJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno del servidor");
                return;
            }
            if (actual == null) {
                sesion.invalidate();
                if (esRest) {
                    errorJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Sesión no iniciada");
                } else {
                    response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
                }
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

        // CSRF en la API REST: todo lo que no sea una lectura lleva el token en una cabecera
        if (esRest && !esMetodoSeguro(request.getMethod())
                && !tokenValido(request.getHeader(CABECERA_CSRF), sesion)) {
            errorJson(response, HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido");
            return;
        }

        // CSRF: todo POST (autenticado o de login/registro) debe traer el token de su sesión
        if (!esRest && esPost && (estaLogueado || esAccionPublica)
                && !tokenValido(request.getParameter(ATRIBUTO_CSRF), sesion)) {
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

    private static boolean esMetodoSeguro(String metodo) {
        return "GET".equals(metodo) || "HEAD".equals(metodo) || "OPTIONS".equals(metodo);
    }

    /** Respuesta de error de la API: JSON {@code {"resultado": mensaje}}, como el resto del servicio. */
    private static void errorJson(HttpServletResponse response, int codigo, String mensaje)
            throws IOException {
        response.setStatus(codigo);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"resultado\":\"" + mensaje + "\"}");
    }

    private static boolean tokenValido(String recibido, HttpSession sesion) {
        if (sesion == null) return false;
        Object esperado = sesion.getAttribute(ATRIBUTO_CSRF);
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
