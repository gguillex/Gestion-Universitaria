package gestion.accion;

import gestion.bean.Usuario;
import gestion.filtro.SeguridadFiltro;
import gestion.modelo.UsuarioDAO;
import gestion.util.LimitadorIntentos;
import gestion.util.Passwords;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Locale;

public class AccionLogin implements Accion {

    private static final long QUINCE_MINUTOS = 15 * 60 * 1000L;

    /** 5 fallos seguidos con el mismo usuario desde la misma IP → 15 min de bloqueo. */
    private static final LimitadorIntentos POR_USUARIO = new LimitadorIntentos(5, QUINCE_MINUTOS);
    /** 20 fallos desde una IP con cualquier usuario → 15 min (frena probar muchos nombres). */
    private static final LimitadorIntentos POR_IP      = new LimitadorIntentos(20, QUINCE_MINUTOS);

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre   = request.getParameter("nombre");
        String password = request.getParameter("password");

        // La clave incluye la IP para que un atacante no pueda bloquear la cuenta
        // de otro usuario que entra desde otro sitio
        String ip = request.getRemoteAddr();
        String claveUsuario = ip + "|" + (nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT));
        long minutos = Math.max(POR_USUARIO.minutosBloqueado(claveUsuario), POR_IP.minutosBloqueado(ip));
        if (minutos > 0) {
            request.setAttribute("errorLogin", "Demasiados intentos fallidos. Espera "
                    + minutos + (minutos == 1 ? " minuto" : " minutos") + " y vuelve a intentarlo.");
            return "/WEB-INF/vistas/login.jsp";
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = (nombre == null) ? null : dao.buscarPorNombre(nombre);

        if (usuario == null) {
            // Mismo coste que un login real: el tiempo de respuesta no delata
            // si el nombre de usuario existe
            Passwords.simularVerificacion(password);
        } else if (Passwords.verificar(password, usuario.getPassword())) {
            // Migración transparente: una contraseña guardada en texto plano se
            // re-guarda como hash en el primer login correcto.
            if (Passwords.esLegado(usuario.getPassword())) {
                usuario.setPassword(Passwords.hashear(password));
                dao.actualizar(usuario);
            }
            // El hash nunca viaja en la sesión.
            usuario.setPassword(null);
            POR_USUARIO.registrarExito(claveUsuario);

            // Anti session fixation: si ya existía una sesión (posiblemente fijada
            // por un atacante) se le cambia el identificador al autenticarse.
            HttpSession sesion = request.getSession(false);
            if (sesion == null) {
                sesion = request.getSession(true);
            } else {
                request.changeSessionId();
            }
            sesion.setAttribute("usuarioLogueado", usuario);
            // Token CSRF nuevo para la sesión autenticada (lo crea SeguridadFiltro)
            sesion.removeAttribute(SeguridadFiltro.ATRIBUTO_CSRF);
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        POR_USUARIO.registrarFallo(claveUsuario);
        POR_IP.registrarFallo(ip);
        request.setAttribute("errorLogin", "Usuario o contraseña incorrectos");
        return "/WEB-INF/vistas/login.jsp";
    }
}
