package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionFormUsuario implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession sesion = request.getSession(false);
        Usuario logueado = (sesion != null) ? (Usuario) sesion.getAttribute("usuarioLogueado") : null;
        if (logueado == null || !"admin".equals(logueado.getRol())) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        String idParam = request.getParameter("id");
        Usuario u = new Usuario();
        u.setRol("usuario");
        if (idParam != null && !idParam.isEmpty()) {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario encontrado = dao.buscarPorId(Integer.parseInt(idParam));
            if (encontrado != null) {
                encontrado.setPassword(null); // el hash no se envía a la vista
                u = encontrado;
            }
        }
        request.setAttribute("usuario", u);
        return "/WEB-INF/vistas/usuarios/formulario.jsp";
    }
}
