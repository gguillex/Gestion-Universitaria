package gestion.accion.alumnos;

import gestion.accion.Accion;
import gestion.bean.Alumno;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Acción que muestra los alumnos matriculados en una asignatura concreta.
 *
 * <p>Si el parámetro {@code csv=true} está presente, escribe directamente
 * la respuesta en formato CSV descargable en lugar de hacer forward al JSP.
 */
public class AccionMatriculadosAsignatura implements Accion {

    @Override
    public String ejecutar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int idAsignatura = Integer.parseInt(request.getParameter("idAsignatura"));

        AlumnoDAO     alumnoDao    = new AlumnoDAO();
        AsignaturaDAO asignaturaDao = new AsignaturaDAO();

        List<Alumno> alumnos = alumnoDao.listarPorAsignatura(idAsignatura);

        // Exportación CSV si se solicita
        if ("true".equals(request.getParameter("csv"))) {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition",
                "attachment; filename=\"matriculados_" + idAsignatura + ".csv\"");
            PrintWriter out = response.getWriter();
            out.println("ID,Nombre,Email,DNI");
            for (Alumno a : alumnos) {
                out.println(
                    a.getId() + "," +
                    escapeCsv(a.getNombre()) + "," +
                    escapeCsv(a.getEmail())  + "," +
                    escapeCsv(a.getDni())
                );
            }
            return null; // ya se escribió la respuesta directamente
        }

        request.setAttribute("asignatura", asignaturaDao.buscarPorId(idAsignatura));
        request.setAttribute("alumnos",    alumnos);
        request.setAttribute("ocupacion",  alumnos.size());
        return "/WEB-INF/vistas/alumnos/matriculados.jsp";
    }

    /** Escapa un valor para incluirlo correctamente en un fichero CSV. */
    private String escapeCsv(String valor) {
        if (valor == null) return "";
        // Si contiene coma, comilla o salto de línea, se envuelve entre comillas dobles
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
