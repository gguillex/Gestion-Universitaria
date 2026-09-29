package gestion.accion.asignaturas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class AccionEliminarAsignaturaTest {

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        request  = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn("7");
        when(request.getContextPath()).thenReturn("/app");
    }

    @Test
    void noEliminaUnaAsignaturaConAlumnosMatriculados() throws Exception {
        try (MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class,
                     (dao, ctx) -> when(dao.contarMatriculados(7)).thenReturn(3));
             MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                     (dao, ctx) -> when(dao.listar()).thenReturn(List.of()))) {

            String vista = new AccionEliminarAsignatura().ejecutar(request, response);

            assertEquals("/WEB-INF/vistas/asignaturas/lista.jsp", vista);
            verify(request).setAttribute(eq("error"), contains("3 alumno(s) matriculado(s)"));
            for (AsignaturaDAO dao : asignaturas.constructed()) {
                verify(dao, never()).eliminar(anyInt());
            }
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    void eliminaUnaAsignaturaSinAlumnosYVuelveAlListado() throws Exception {
        try (MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class,
                     (dao, ctx) -> when(dao.contarMatriculados(7)).thenReturn(0));
             MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class)) {

            String vista = new AccionEliminarAsignatura().ejecutar(request, response);

            assertNull(vista, "tras borrar se hace redirect, no forward");
            assertTrue(asignaturas.constructed().size() >= 1);
            verify(asignaturas.constructed().get(0)).eliminar(7);
            verify(response).sendRedirect("/app/control?idAccion=listarAsignaturas");
        }
    }
}
