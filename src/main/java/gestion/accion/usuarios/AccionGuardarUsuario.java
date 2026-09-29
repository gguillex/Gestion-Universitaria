package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionGuardarUsuario implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession sesion = request.getSession(false);
        Usuario logueado = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
        if (logueado == null || !"admin".equals(logueado.getRol())) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        String idParam   = request.getParameter("id");
        String nombre    = request.getParameter("nombre");
        String password  = request.getParameter("password");
        String rol       = request.getParameter("rol");

        if (rol == null || rol.isEmpty()) rol = "usuario";

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setPassword(password);
        u.setRol(rol);
        boolean esEdicion = idParam != null && !idParam.isEmpty();
        if (esEdicion) u.setId(Integer.parseInt(idParam));

        // Validación de campos obligatorios (el atributo HTML "required" es del lado
        // cliente y se puede saltar con una petición directa al servlet)
        if (nombre == null || nombre.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "El nombre y la contraseña son obligatorios.");
            request.setAttribute("usuario", u);
            return "/WEB-INF/vistas/usuarios/formulario.jsp";
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (esEdicion) {
            dao.actualizar(u);
            // Si el admin se está editando a sí mismo, refrescar el bean en sesión:
            // si no, el rol/nombre antiguos seguirían aplicándose hasta el logout.
            if (u.getId() == logueado.getId()) {
                sesion.setAttribute("usuarioLogueado", u);
            }
        } else {
            dao.insertar(u);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarUsuarios");
        return null;
    }
}
