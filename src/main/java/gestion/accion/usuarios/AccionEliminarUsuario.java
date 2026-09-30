package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionEliminarUsuario implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Usuario logueado = Sesion.exigirAdmin(request, response);
        if (logueado == null) return null;

        int id = Integer.parseInt(request.getParameter("id"));
        UsuarioDAO dao = new UsuarioDAO();

        if (id == logueado.getId()) {
            request.setAttribute("error", "No puedes eliminar tu propio usuario mientras estás logueado");
            request.setAttribute("usuarios", dao.listar());
            return "/WEB-INF/vistas/usuarios/lista.jsp";
        }

        dao.eliminar(id);
        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarUsuarios");
        return null;
    }
}
