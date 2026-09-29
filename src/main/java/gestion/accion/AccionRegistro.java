package gestion.accion;

import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Passwords;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Acción pública (no requiere sesión) que procesa el autoregistro de un nuevo usuario.
 *
 * <p>Validaciones:
 * <ul>
 *   <li>Nombre y contraseña son obligatorios.</li>
 *   <li>La contraseña y su confirmación deben coincidir.</li>
 *   <li>El nombre de usuario no puede estar ya registrado.</li>
 * </ul>
 *
 * <p>El rol asignado siempre es "usuario"; el rol admin solo puede asignarse
 * desde la zona privada de gestión de usuarios.
 */
public class AccionRegistro implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre     = request.getParameter("nombre");
        String password   = request.getParameter("password");
        String confirmar  = request.getParameter("confirmar");

        // Validación de campos obligatorios
        if (nombre == null || nombre.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorRegistro", "El nombre y la contraseña son obligatorios.");
            return "/WEB-INF/vistas/registro.jsp";
        }

        // Validación de coincidencia de contraseñas
        if (!password.equals(confirmar)) {
            request.setAttribute("errorRegistro", "Las contraseñas no coinciden.");
            request.setAttribute("nombrePrevio", nombre);
            return "/WEB-INF/vistas/registro.jsp";
        }

        UsuarioDAO dao = new UsuarioDAO();

        // Validación de nombre duplicado
        if (dao.existeNombre(nombre.trim())) {
            request.setAttribute("errorRegistro", "Ese nombre de usuario ya está en uso.");
            return "/WEB-INF/vistas/registro.jsp";
        }

        // Crear usuario con rol "usuario" (nunca admin desde autoregistro)
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre.trim());
        nuevo.setPassword(Passwords.hashear(password));
        nuevo.setRol("usuario");
        try {
            dao.insertar(nuevo);
        } catch (SQLIntegrityConstraintViolationException e) {
            // Red de seguridad ante la carrera entre existeNombre() y este insert:
            // dos registros simultáneos con el mismo nombre pueden pasar ambos la
            // comprobación previa; la restricción UNIQUE de la BD frena al segundo.
            request.setAttribute("errorRegistro", "Ese nombre de usuario ya está en uso.");
            return "/WEB-INF/vistas/registro.jsp";
        }

        // Redirigir al login con mensaje de éxito
        response.sendRedirect(request.getContextPath() +
            "/control?idAccion=mostrarLogin&registroOk=1");
        return null;
    }
}
