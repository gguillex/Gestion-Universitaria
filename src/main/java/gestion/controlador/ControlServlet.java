package gestion.controlador;

import gestion.accion.*;
import gestion.accion.titulaciones.*;
import gestion.accion.usuarios.*;
import gestion.accion.asignaturas.*;
import gestion.accion.profesores.*;
import gestion.accion.alumnos.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador frontal único de la aplicación (patrón Front Controller).
 * Recibe el parámetro {@code idAccion} en todas las peticiones, busca la acción
 * correspondiente en el mapa registrado en {@link #init()} y delega la ejecución.
 * Cualquier excepción no controlada se captura y se muestra en la vista de error.
 */
@WebServlet("/control")
public class ControlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Map<String, Accion> acciones;

    @Override
    public void init() {
        acciones = new HashMap<>();

        // Login / logout
        acciones.put("mostrarLogin",   new AccionMostrarLogin());
        acciones.put("login",          new AccionLogin());
        acciones.put("logout",         new AccionLogout());

        // Titulaciones
        acciones.put("listarTitulaciones",  new AccionListarTitulaciones());
        acciones.put("formTitulacion",      new AccionFormTitulacion());
        acciones.put("guardarTitulacion",   new AccionGuardarTitulacion());
        acciones.put("eliminarTitulacion",  new AccionEliminarTitulacion());

        // Usuarios (solo admin)
        acciones.put("listarUsuarios",  new AccionListarUsuarios());
        acciones.put("formUsuario",     new AccionFormUsuario());
        acciones.put("guardarUsuario",  new AccionGuardarUsuario());
        acciones.put("eliminarUsuario", new AccionEliminarUsuario());

        // Asignaturas
        acciones.put("listarAsignaturas",  new AccionListarAsignaturas());
        acciones.put("formAsignatura",     new AccionFormAsignatura());
        acciones.put("guardarAsignatura",  new AccionGuardarAsignatura());
        acciones.put("eliminarAsignatura", new AccionEliminarAsignatura());

        // Profesores
        acciones.put("listarProfesores",  new AccionListarProfesores());
        acciones.put("formProfesor",      new AccionFormProfesor());
        acciones.put("guardarProfesor",   new AccionGuardarProfesor());
        acciones.put("eliminarProfesor",  new AccionEliminarProfesor());
        acciones.put("asignarProfesor",   new AccionAsignarProfesor());

        // Alumnos y matrículas
        acciones.put("listarAlumnos",            new AccionListarAlumnos());
        acciones.put("formAlumno",               new AccionFormAlumno());
        acciones.put("guardarAlumno",            new AccionGuardarAlumno());
        acciones.put("eliminarAlumno",           new AccionEliminarAlumno());
        acciones.put("matricular",               new AccionMatricular());
        acciones.put("desmatricular",            new AccionDesmatricular());
        acciones.put("matriculadosAsignatura",   new AccionMatriculadosAsignatura());

        // Autoregistro (público, sin sesión)
        acciones.put("mostrarRegistro", new AccionMostrarRegistro());
        acciones.put("registro",        new AccionRegistro());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesar(request, response);
    }

    private void procesar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String idAccion = request.getParameter("idAccion");

        Accion accion = (idAccion != null) ? acciones.get(idAccion) : null;
        if (accion == null) {
            response.sendRedirect(request.getContextPath() + "/control?idAccion=mostrarLogin");
            return;
        }

        try {
            String jspDestino = accion.ejecutar(request, response);
            if (jspDestino != null) {
                request.getRequestDispatcher(jspDestino).forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/vistas/error.jsp").forward(request, response);
        }
    }
}
