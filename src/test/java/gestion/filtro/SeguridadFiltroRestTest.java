package gestion.filtro;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

/** Regla: el servicio REST exige sesión, usuario vigente y token CSRF en cabecera para escribir. */
class SeguridadFiltroRestTest {

    private static final String TOKEN = "token-de-la-sesion";

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession sesion;
    private FilterChain cadena;
    private StringWriter cuerpo;

    @BeforeEach
    void setUp() throws Exception {
        request  = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        sesion   = mock(HttpSession.class);
        cadena   = mock(FilterChain.class);
        cuerpo   = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(cuerpo));
        when(request.getContextPath()).thenReturn("/app");
        when(request.getServletPath()).thenReturn("/rest");
        when(request.getMethod()).thenReturn("GET");
    }

    private void conSesion() {
        Usuario u = new Usuario(5, "ana", null, "usuario");
        when(request.getSession(false)).thenReturn(sesion);
        when(sesion.getAttribute("usuarioLogueado")).thenReturn(u);
        when(sesion.getAttribute(SeguridadFiltro.ATRIBUTO_CSRF)).thenReturn(TOKEN);
    }

    /** UsuarioDAO que devuelve al usuario 5 (o null si el usuario ya no existe en la BD). */
    private MockedConstruction<UsuarioDAO> bd(boolean existe) {
        return mockConstruction(UsuarioDAO.class, (dao, ctx) ->
                when(dao.buscarPorId(5)).thenReturn(existe ? new Usuario(5, "ana", "hash", "usuario") : null));
    }

    @Test
    void sinSesionLaApiRespondeCon401JsonYNoRedirige() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        new SeguridadFiltro().doFilter(request, response, cadena);

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        verify(response, never()).sendRedirect(any());
        verify(cadena, never()).doFilter(any(), any());
        assertTrue(cuerpo.toString().contains("\"resultado\""));
    }

    @Test
    void sinSesionLasPaginasDelClienteRedirigenAlLogin() throws Exception {
        when(request.getServletPath()).thenReturn("/rest-ui/titulacion.jsp");
        when(request.getSession(false)).thenReturn(null);

        new SeguridadFiltro().doFilter(request, response, cadena);

        verify(response).sendRedirect("/app/control?idAccion=mostrarLogin");
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void unaLecturaConSesionPasaSinToken() throws Exception {
        conSesion();
        try (MockedConstruction<UsuarioDAO> dao = bd(true)) {
            new SeguridadFiltro().doFilter(request, response, cadena);
        }
        verify(cadena).doFilter(request, response);
        verify(response).setHeader("Cache-Control", "no-store");
    }

    @Test
    void unaEscrituraSinCabeceraCsrfSeRechazaCon403() throws Exception {
        for (String metodo : new String[] {"POST", "PUT", "DELETE"}) {
            request  = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);
            cadena   = mock(FilterChain.class);
            when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
            when(request.getServletPath()).thenReturn("/rest");
            when(request.getMethod()).thenReturn(metodo);
            conSesion();

            try (MockedConstruction<UsuarioDAO> dao = bd(true)) {
                new SeguridadFiltro().doFilter(request, response, cadena);
            }

            verify(response).setStatus(403);
            verify(cadena, never()).doFilter(any(), any());
        }
    }

    @Test
    void unaEscrituraConTokenIncorrectoSeRechaza() throws Exception {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getHeader("X-CSRF-Token")).thenReturn("otro-token");
        conSesion();

        try (MockedConstruction<UsuarioDAO> dao = bd(true)) {
            new SeguridadFiltro().doFilter(request, response, cadena);
        }

        verify(response).setStatus(403);
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void unaEscrituraConElTokenDeLaSesionPasa() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("X-CSRF-Token")).thenReturn(TOKEN);
        conSesion();

        try (MockedConstruction<UsuarioDAO> dao = bd(true)) {
            new SeguridadFiltro().doFilter(request, response, cadena);
        }

        verify(cadena).doFilter(request, response);
        verify(response, never()).setStatus(403);
    }

    @Test
    void unUsuarioBorradoDeLaBdPierdeElAccesoALaApiAlMomento() throws Exception {
        conSesion();

        try (MockedConstruction<UsuarioDAO> dao = bd(false)) {
            new SeguridadFiltro().doFilter(request, response, cadena);
        }

        verify(sesion).invalidate();
        verify(response).setStatus(401);
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void siFallaLaBdLaApiRespondeConJsonYNoConUnaPaginaDeError() throws Exception {
        conSesion();

        try (MockedConstruction<UsuarioDAO> dao = mockConstruction(UsuarioDAO.class, (d, ctx) ->
                when(d.buscarPorId(5)).thenThrow(new RuntimeException("conexión perdida")))) {
            new SeguridadFiltro().doFilter(request, response, cadena);
        }

        verify(response).setStatus(500);
        verify(response).setContentType("application/json");
        verify(cadena, never()).doFilter(any(), any());
        assertTrue(cuerpo.toString().contains("\"resultado\""));
    }

    @Test
    void unIdAccionEnLaUrlNoConvierteUnaPeticionDeLaApiEnUna405() throws Exception {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getParameter("idAccion")).thenReturn("eliminarAsignatura");
        when(request.getHeader("X-CSRF-Token")).thenReturn(TOKEN);
        conSesion();

        try (MockedConstruction<UsuarioDAO> dao = bd(true)) {
            new SeguridadFiltro().doFilter(request, response, cadena);
        }

        verify(response, never()).sendError(405);
        verify(cadena).doFilter(request, response);
    }

    @Test
    void elParametroDeRutaNoSirveParaSaltarseElFiltro() throws Exception {
        // getServletPath() no incluye ";x" — /rest;.css sigue siendo /rest
        when(request.getServletPath()).thenReturn("/rest");
        when(request.getRequestURI()).thenReturn("/app/rest;a.css");
        when(request.getSession(false)).thenReturn(null);

        new SeguridadFiltro().doFilter(request, response, cadena);

        verify(response).setStatus(401);
        verify(cadena, never()).doFilter(any(), any());
    }
}
