package gestion.accion.usuarios;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.accion.Accion;
import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;

/** Regla: solo el rol admin puede usar el CRUD de usuarios. */
class AccesoSoloAdminTest {

    private static final String REDIRECCION = "/app/control?idAccion=listarTitulaciones";

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession sesion;

    static Stream<Arguments> accionesDeUsuarios() {
        return Stream.of(
                Arguments.of("listarUsuarios",  new AccionListarUsuarios()),
                Arguments.of("formUsuario",     new AccionFormUsuario()),
                Arguments.of("guardarUsuario",  new AccionGuardarUsuario()),
                Arguments.of("eliminarUsuario", new AccionEliminarUsuario()));
    }

    @BeforeEach
    void setUp() {
        request  = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        sesion   = mock(HttpSession.class);
        when(request.getContextPath()).thenReturn("/app");
        // Datos válidos por si la acción llega a ejecutarse (caso admin)
        when(request.getParameter("id")).thenReturn("2");
        when(request.getParameter("nombre")).thenReturn("pepe");
        when(request.getParameter("password")).thenReturn("secreta");
        when(request.getParameter("rol")).thenReturn("usuario");
    }

    private void sesionDe(String rol) {
        when(request.getSession(false)).thenReturn(sesion);
        when(sesion.getAttribute("usuarioLogueado")).thenReturn(new Usuario(1, "alguien", null, rol));
    }

    @ParameterizedTest(name = "{0}: sin sesión → redirige y no toca la BD")
    @MethodSource("accionesDeUsuarios")
    void sinSesionSeRechaza(String nombre, Accion accion) throws Exception {
        when(request.getSession(false)).thenReturn(null);
        try (MockedConstruction<UsuarioDAO> daos = mockConstruction(UsuarioDAO.class)) {

            assertNull(accion.ejecutar(request, response));

            verify(response).sendRedirect(REDIRECCION);
            assertTrue(daos.constructed().isEmpty(), "no debe llegar a crear el DAO");
        }
    }

    @ParameterizedTest(name = "{0}: rol usuario → redirige y no toca la BD")
    @MethodSource("accionesDeUsuarios")
    void usuarioNormalSeRechaza(String nombre, Accion accion) throws Exception {
        sesionDe("usuario");
        try (MockedConstruction<UsuarioDAO> daos = mockConstruction(UsuarioDAO.class)) {

            assertNull(accion.ejecutar(request, response));

            verify(response).sendRedirect(REDIRECCION);
            assertTrue(daos.constructed().isEmpty(), "no debe llegar a crear el DAO");
        }
    }

    @ParameterizedTest(name = "{0}: rol admin → se ejecuta")
    @MethodSource("accionesDeUsuarios")
    void adminPuedeAcceder(String nombre, Accion accion) throws Exception {
        sesionDe("admin");
        try (MockedConstruction<UsuarioDAO> daos = mockConstruction(UsuarioDAO.class,
                (dao, ctx) -> when(dao.buscarPorId(2)).thenReturn(new Usuario(2, "pepe", "hash", "usuario")))) {

            accion.ejecutar(request, response);

            verify(response, never()).sendRedirect(REDIRECCION);
            assertFalse(daos.constructed().isEmpty(), "el admin sí llega a usar el DAO");
        }
    }
}
