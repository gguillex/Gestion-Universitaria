package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Passwords;
import gestion.util.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionGuardarUsuario implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Usuario logueado = Sesion.exigirAdmin(request, response);
        if (logueado == null) return null;
        HttpSession sesion = request.getSession(false);

        String idParam   = request.getParameter("id");
        String nombre    = request.getParameter("nombre");
        String password  = request.getParameter("password");
        String rol       = request.getParameter("rol");

        // Solo se admiten los dos roles conocidos
        if (!"admin".equals(rol)) rol = "usuario";

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setRol(rol);
        boolean esEdicion = idParam != null && !idParam.isEmpty();
        if (esEdicion) u.setId(Integer.parseInt(idParam));

        // En edición la contraseña es opcional: vacía = conservar la actual.
        // Al crear es obligatoria. (El "required" HTML se puede saltar con una
        // petición directa al servlet, por eso se valida aquí.)
        boolean sinPassword = password == null || password.trim().isEmpty();
        if (nombre == null || nombre.trim().isEmpty() || (!esEdicion && sinPassword)) {
            request.setAttribute("error", esEdicion
                    ? "El nombre es obligatorio."
                    : "El nombre y la contraseña son obligatorios.");
            request.setAttribute("usuario", u);
            return "/WEB-INF/vistas/usuarios/formulario.jsp";
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (esEdicion) {
            if (sinPassword) {
                Usuario actual = dao.buscarPorId(u.getId());
                if (actual == null) {
                    response.sendRedirect(request.getContextPath() + "/control?idAccion=listarUsuarios");
                    return null;
                }
                u.setPassword(actual.getPassword());
            } else {
                u.setPassword(Passwords.hashear(password));
            }
            dao.actualizar(u);
            // Si el admin se está editando a sí mismo, refrescar el bean en sesión:
            // si no, el rol/nombre antiguos seguirían aplicándose hasta el logout.
            if (u.getId() == logueado.getId()) {
                u.setPassword(null); // el hash nunca viaja en la sesión
                sesion.setAttribute(Sesion.ATRIBUTO_USUARIO, u);
            }
        } else {
            u.setPassword(Passwords.hashear(password));
            dao.insertar(u);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarUsuarios");
        return null;
    }
}
