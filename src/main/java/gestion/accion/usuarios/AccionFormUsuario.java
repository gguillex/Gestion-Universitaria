package gestion.accion.usuarios;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionFormUsuario implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Usuario logueado = Sesion.exigirAdmin(request, response);
        if (logueado == null) return null;

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
