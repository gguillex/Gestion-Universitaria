package gestion.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.bean.Asignatura;
import gestion.bean.Profesor;
import gestion.bean.Titulacion;
import gestion.modelo.AlumnoDAO;
import gestion.modelo.AsignaturaDAO;
import gestion.modelo.ProfesorDAO;
import gestion.modelo.TitulacionDAO;
import gestion.rest.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

/** Reglas de negocio de asignaturas por REST: las mismas que en la interfaz MVC. */
class AsignaturaServiceTest {

    private static Asignatura asignatura(int id, String nombre, int capacidad, int idTit, Integer idProf) {
        return new Asignatura(id, nombre, capacidad, idTit, idProf);
    }

    @Test
    void noBajaLaCapacidadPorDebajoDeLosMatriculados() throws Exception {
        Asignatura existente = asignatura(7, "Redes", 30, 1, null);
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(7)).thenReturn(existente));
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(1)).thenReturn(new Titulacion()));
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class,
                     (dao, ctx) -> when(dao.contarMatriculados(7)).thenReturn(20))) {

            AsignaturaService servicio = new AsignaturaService();
            ApiException e = assertThrows(ApiException.class,
                    () -> servicio.modificar(asignatura(7, "Redes", 10, 1, null)));

            assertEquals(409, e.getHttpCode());
            verify(asignaturas.constructed().get(0), never()).actualizar(any());
        }
    }

    @Test
    void noEliminaUnaAsignaturaConAlumnosMatriculados() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(7)).thenReturn(asignatura(7, "Redes", 30, 1, null)));
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class);
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class,
                     (dao, ctx) -> when(dao.contarMatriculados(7)).thenReturn(3))) {

            AsignaturaService servicio = new AsignaturaService();
            ApiException e = assertThrows(ApiException.class, () -> servicio.eliminar(7));

            assertEquals(409, e.getHttpCode());
            verify(asignaturas.constructed().get(0), never()).eliminar(anyInt());
        }
    }

    @Test
    void eliminaUnaAsignaturaSinAlumnos() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(7)).thenReturn(asignatura(7, "Redes", 30, 1, null)));
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class);
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class,
                     (dao, ctx) -> when(dao.contarMatriculados(7)).thenReturn(0))) {

            new AsignaturaService().eliminar(7);

            verify(asignaturas.constructed().get(0)).eliminar(7);
        }
    }

    @Test
    void eliminarUnaAsignaturaQueNoExisteDa404() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class);
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class);
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class)) {

            AsignaturaService servicio = new AsignaturaService();
            assertEquals(404, assertThrows(ApiException.class, () -> servicio.eliminar(99)).getHttpCode());
        }
    }

    @Test
    void elAltaExigeUnaTitulacionExistente() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class);
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(9)).thenReturn(null));
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class)) {

            AsignaturaService servicio = new AsignaturaService();
            ApiException e = assertThrows(ApiException.class,
                    () -> servicio.alta(asignatura(0, "Redes", 30, 9, null)));

            assertEquals(404, e.getHttpCode());
            verify(asignaturas.constructed().get(0), never()).insertar(any());
        }
    }

    @Test
    void elAltaValidaNombreCapacidadYTitulacion() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class);
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class);
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class)) {

            AsignaturaService servicio = new AsignaturaService();
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(asignatura(0, "  ", 30, 1, null))).getHttpCode());
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(asignatura(0, "Redes", 0, 1, null))).getHttpCode());
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(asignatura(0, "Redes", 30, 0, null))).getHttpCode());
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(asignatura(0, "x".repeat(201), 30, 1, null))).getHttpCode());
        }
    }

    @Test
    void siLaRelecturaTrasElAltaFallaSeDevuelveLaAsignaturaCreada() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class, (dao, ctx) -> {
                 doAnswer(inv -> { ((Asignatura) inv.getArgument(0)).setId(11); return null; })
                         .when(dao).insertar(any(Asignatura.class));
                 when(dao.buscarPorId(11)).thenThrow(new RuntimeException("BD caída"));
             });
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(1)).thenReturn(new Titulacion()));
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class);
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class)) {

            Asignatura creada = new AsignaturaService().alta(asignatura(0, "Redes", 30, 1, null));

            // La fila ya existe: un error aquí haría que el cliente reintentase y la duplicase
            assertEquals(11, creada.getId());
        }
    }

    @Test
    void asignarUnProfesorQueNoExisteDa404() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(7)).thenReturn(asignatura(7, "Redes", 30, 1, null)));
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class);
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(4)).thenReturn(null));
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class)) {

            AsignaturaService servicio = new AsignaturaService();
            ApiException e = assertThrows(ApiException.class, () -> servicio.asignarProfesor(7, 4));

            assertEquals(404, e.getHttpCode());
            verify(asignaturas.constructed().get(0), never()).asignarProfesor(anyInt(), any());
        }
    }

    @Test
    void sinIdProfesorSeQuitaElProfesorDeLaAsignatura() throws Exception {
        try (MockedConstruction<AsignaturaDAO> asignaturas = mockConstruction(AsignaturaDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(7)).thenReturn(asignatura(7, "Redes", 30, 1, 4)));
             MockedConstruction<TitulacionDAO> titulaciones = mockConstruction(TitulacionDAO.class);
             MockedConstruction<ProfesorDAO> profesores = mockConstruction(ProfesorDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(4)).thenReturn(new Profesor()));
             MockedConstruction<AlumnoDAO> alumnos = mockConstruction(AlumnoDAO.class)) {

            new AsignaturaService().asignarProfesor(7, null);

            verify(asignaturas.constructed().get(0)).asignarProfesor(7, null);
        }
    }
}
