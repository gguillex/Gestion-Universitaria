package gestion.accion;

import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AccionLogin implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nombre   = request.getParameter("nombre");
        String password = request.getParameter("password");

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.validarCredenciales(nombre, password);

        if (usuario != null) {
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuarioLogueado", usuario);
            response.sendRedirect(request.getContextPath() + "/control?idAccion=listarTitulaciones");
            return null;
        }

        request.setAttribute("errorLogin", "Usuario o contraseña incorrectos");
        return "/WEB-INF/vistas/login.jsp";
    }
}
