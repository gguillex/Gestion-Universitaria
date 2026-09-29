package gestion.accion.asignaturas;

import gestion.accion.Accion;
import gestion.bean.Asignatura;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import gestion.modelo.ProfesorDAO;
import gestion.modelo.TitulacionDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AccionGuardarAsignatura implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idParam     = request.getParameter("id");
        String nombre      = request.getParameter("nombre");
        String capacidad   = request.getParameter("capacidadMaxima");
        String idTitParam  = request.getParameter("idTitulacion");
        String idProfParam = request.getParameter("idProfesor");

        Asignatura a = new Asignatura();
        a.setNombre(nombre);
        a.setCapacidadMaxima(capacidad != null && !capacidad.isEmpty() ? Integer.parseInt(capacidad) : 30);
        a.setIdTitulacion(Integer.parseInt(idTitParam));
        if (idProfParam != null && !idProfParam.isEmpty()) {
            a.setIdProfesor(Integer.parseInt(idProfParam));
        } else {
            a.setIdProfesor(null);
        }

        AsignaturaDAO dao = new AsignaturaDAO();
        boolean esEdicion = idParam != null && !idParam.isEmpty();
        if (esEdicion) {
            a.setId(Integer.parseInt(idParam));

            // No permitir bajar la capacidad por debajo de los alumnos ya matriculados
            int matriculados = new AlumnoDAO().contarMatriculados(a.getId());
            if (a.getCapacidadMaxima() < matriculados) {
                request.setAttribute("error",
                    "No se puede fijar la capacidad en " + a.getCapacidadMaxima() +
                    ": la asignatura ya tiene " + matriculados + " alumno(s) matriculado(s).");
                request.setAttribute("asignatura",   a);
                request.setAttribute("titulaciones", new TitulacionDAO().listar());
                request.setAttribute("profesores",   new ProfesorDAO().listar());
                return "/WEB-INF/vistas/asignaturas/formulario.jsp";
            }

            dao.actualizar(a);
        } else {
            dao.insertar(a);
        }

        response.sendRedirect(request.getContextPath() + "/control?idAccion=listarAsignaturas");
        return null;
    }
}
