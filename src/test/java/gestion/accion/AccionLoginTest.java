package gestion.accion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.bean.Usuario;
import gestion.modelo.UsuarioDAO;
import gestion.util.Passwords;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

class AccionLoginTest {

    private static final String VISTA_LOGIN = "/WEB-INF/vistas/login.jsp";
    private static final String DESTINO_OK  = "/app/control?idAccion=listarTitulaciones";

    /** El limitador de intentos es estático: cada test usa su propia IP para no interferir. */
    private static final AtomicInteger SIGUIENTE_IP = new AtomicInteger(1);

    private static String hashDe1234;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession sesion;

    @BeforeAll
    static void calcularHash() {
        hashDe1234 = Passwords.hashear("1234"); // PBKDF2 es lento a propósito: una vez basta
    }

    @BeforeEach
    void setUp() {
        request  = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        sesion   = mock(HttpSession.class);
        when(request.getContextPath()).thenReturn("/app");
        when(request.getRemoteAddr()).thenReturn("10.0.0." + SIGUIENTE_IP.getAndIncrement());
        when(request.getSession(false)).thenReturn(sesion); // sesión anónima del formulario
    }

    private void credenciales(String nombre, String password) {
        when(request.getParameter("nombre")).thenReturn(nombre);
        when(request.getParameter("password")).thenReturn(password);
    }

    private static MockedConstruction<UsuarioDAO> bdCon(Usuario usuario) {
        return mockConstruction(UsuarioDAO.class,
                (dao, ctx) -> when(dao.buscarPorNombre(usuario.getNombre())).thenReturn(usuario));
    }

    @Test
    void loginValidoGuardaElUsuarioEnSesionYRedirige() throws Exception {
        credenciales("ana", "1234");
        try (MockedConstruction<UsuarioDAO> bd = bdCon(new Usuario(4, "ana", hashDe1234, "usuario"))) {

            assertNull(new AccionLogin().ejecutar(request, response));

            ArgumentCaptor<Usuario> enSesion = ArgumentCaptor.forClass(Usuario.class);
            verify(sesion).setAttribute(eq("usuarioLogueado"), enSesion.capture());
            assertEquals("ana", enSesion.getValue().getNombre());
            assertNull(enSesion.getValue().getPassword(), "el hash no debe guardarse en la sesión");
            verify(request).changeSessionId(); // anti session fixation
            verify(response).sendRedirect(DESTINO_OK);
        }
    }

    @Test
    void contrasenaIncorrectaVuelveAlLoginConError() throws Exception {
        credenciales("ana", "mala");
        try (MockedConstruction<UsuarioDAO> bd = bdCon(new Usuario(4, "ana", hashDe1234, "usuario"))) {

            assertEquals(VISTA_LOGIN, new AccionLogin().ejecutar(request, response));

            verify(request).setAttribute("errorLogin", "Usuario o contraseña incorrectos");
            verify(sesion, never()).setAttribute(eq("usuarioLogueado"), any());
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    void usuarioInexistenteDaElMismoErrorQueUnaContrasenaIncorrecta() throws Exception {
        credenciales("nadie", "1234");
        try (MockedConstruction<UsuarioDAO> bd = mockConstruction(UsuarioDAO.class)) { // buscarPorNombre → null

            assertEquals(VISTA_LOGIN, new AccionLogin().ejecutar(request, response));

            verify(request).setAttribute("errorLogin", "Usuario o contraseña incorrectos");
            verify(sesion, never()).setAttribute(eq("usuarioLogueado"), any());
        }
    }

    @Test
    void contrasenaAntiguaEnClaroEntraYSeMigraAHash() throws Exception {
        credenciales("viejo", "claro");
        // La acción pone el password a null después de guardar, así que se copia
        // el valor en el momento exacto de la llamada a actualizar()
        AtomicReference<String> passwordGuardada = new AtomicReference<>();
        try (MockedConstruction<UsuarioDAO> bd = mockConstruction(UsuarioDAO.class, (dao, ctx) -> {
                 when(dao.buscarPorNombre("viejo")).thenReturn(new Usuario(9, "viejo", "claro", "usuario"));
                 doAnswer(inv -> {
                     passwordGuardada.set(inv.<Usuario>getArgument(0).getPassword());
                     return null;
                 }).when(dao).actualizar(any());
             })) {

            assertNull(new AccionLogin().ejecutar(request, response));

            verify(response).sendRedirect(DESTINO_OK);
            assertTrue(passwordGuardada.get().startsWith("pbkdf2$"), "se re-guarda como hash");
            assertTrue(Passwords.verificar("claro", passwordGuardada.get()));
        }
    }

    @Test
    void trasCincoFallosSeBloqueaAunqueLaSiguienteContrasenaSeaCorrecta() throws Exception {
        credenciales("ana", "mala");
        try (MockedConstruction<UsuarioDAO> bd = bdCon(new Usuario(4, "ana", hashDe1234, "usuario"))) {
            AccionLogin accion = new AccionLogin();
            for (int i = 0; i < 5; i++) accion.ejecutar(request, response);

            credenciales("ana", "1234");
            assertEquals(VISTA_LOGIN, accion.ejecutar(request, response));

            verify(request).setAttribute(eq("errorLogin"), contains("Demasiados intentos fallidos"));
            verify(sesion, never()).setAttribute(eq("usuarioLogueado"), any());
        }
    }
}
