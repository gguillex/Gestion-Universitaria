package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionEliminarUsuario implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession sesion = request.getSession(false);
        Usuario logueado = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
        if (logueado == null || !"admin".equals(logueado.getRol())) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        int id = Integer.parseInt(request.getParameter("id"));

        if (id == logueado.getId()) {
            request.setAttribute("error", "No puedes eliminar tu propio usuario mientras estás logueado");
            UsuarioDAO dao = new UsuarioDAO();
            request.setAttribute("usuarios", dao.listar());
            return "/WEB-INF/vistas/usuarios/lista.jsp";
        }

        UsuarioDAO dao = new UsuarioDAO();
        dao.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarUsuarios");
        return null;
    }
}
