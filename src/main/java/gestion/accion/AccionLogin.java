package gestion.accion;

import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Passwords;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionLogin implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre   = request.getParameter("nombre");
        String password = request.getParameter("password");

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

            // Anti session fixation: si ya existía una sesión (posiblemente fijada
            // por un atacante) se le cambia el identificador al autenticarse.
            HttpSession sesion = request.getSession(false);
            if (sesion == null) {
                sesion = request.getSession(true);
            } else {
                request.changeSessionId();
            }
            sesion.setAttribute("usuarioLogueado", usuario);
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        request.setAttribute("errorLogin", "Usuario o contraseña incorrectos");
        return "/WEB-INF/vistas/login.jsp";
    }
}
