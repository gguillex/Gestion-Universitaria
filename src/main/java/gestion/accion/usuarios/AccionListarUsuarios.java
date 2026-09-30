package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionListarUsuarios implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Usuario u = Sesion.exigirAdmin(request, response);
        if (u == null) return null;

        UsuarioDAO dao = new UsuarioDAO();
        request.setAttribute("usuarios", dao.listar());
        return "/WEB-INF/vistas/usuarios/lista.jsp";
    }
}
