package gestion.accion.alumnos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.bean.Asignatura;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

/**
 * La comparación de capacidad vive en {@link AlumnoDAO#matricular} (ver AlumnoDAOTest);
 * aquí se comprueba cómo reacciona la acción a su resultado.
 */
class AccionMatricularTest {

    private static final String VISTA_FORM = "/WEB-INF/vistas/alumnos/matricular.jsp";

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        request  = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("idAlumno")).thenReturn("1");
        when(request.getParameter("idAsignatura")).thenReturn("5");
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void conLaAsignaturaLlenaMuestraErrorDeCapacidadYNoRedirige() throws Exception {
        Asignatura llena = new Asignatura();
        llena.setCapacidadMaxima(30);
        try (MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class, (dao, ctx) -> {
                 when(dao.estaMatriculado(1, 5)).thenReturn(false);
                 when(dao.matricular(1, 5)).thenReturn(false); // sin hueco
             });
             MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                 (dao, ctx) -> when(dao.buscarPorId(5)).thenReturn(llena))) {

            String vista = new AccionMatricular().ejecutar(request, response);

            assertEquals(VISTA_FORM, vista);
            verify(request).setAttribute(eq("error"), contains("capacidad máxima (30 alumnos)"));
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    void conHuecoMatriculaYRedirigeAlListadoDeMatriculados() throws Exception {
        try (MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class, (dao, ctx) -> {
                 when(dao.estaMatriculado(1, 5)).thenReturn(false);
                 when(dao.matricular(1, 5)).thenReturn(true);
             });
             MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class)) {

            String vista = new AccionMatricular().ejecutar(request, response);

            assertNull(vista);
            verify(response).sendRedirect("/app/control?idAccion=matriculadosAsignatura&idAsignatura=5");
            verify(request, never()).setAttribute(eq("error"), anyString());
        }
    }

    @Test
    void noMatriculaDosVecesAlMismoAlumno() throws Exception {
        try (MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class,
                 (dao, ctx) -> when(dao.estaMatriculado(1, 5)).thenReturn(true));
             MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class)) {

            String vista = new AccionMatricular().ejecutar(request, response);

            assertEquals(VISTA_FORM, vista);
            verify(request).setAttribute(eq("error"), contains("ya está matriculado"));
            verify(alumnos.constructed().get(0), never()).matricular(anyInt(), anyInt());
        }
    }

    @Test
    void porGetSoloMuestraElFormularioSinMatricular() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        try (MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class);
             MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class)) {

            String vista = new AccionMatricular().ejecutar(request, response);

            assertEquals(VISTA_FORM, vista);
            verify(alumnos.constructed().get(0), never()).matricular(anyInt(), anyInt());
        }
    }
}
