package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionListarUsuarios implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession sesion = request.getSession(false);
        Usuario u = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
        if (u == null || !"admin".equals(u.getRol())) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        UsuarioDAO dao = new UsuarioDAO();
        request.setAttribute("usuarios", dao.listar());
        return "/WEB-INF/vistas/usuarios/lista.jsp";
    }
}
