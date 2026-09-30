package gestion.rest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gestion.bean.Profesor;
import gestion.bean.Titulacion;
import gestion.modelo.ProfesorDAO;
import gestion.modelo.TitulacionDAO;
import gestion.rest.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

/** Reglas de negocio de titulaciones y profesores por REST. */
class TitulacionServiceTest {

    private static Titulacion titulacion(int id, String nombre) {
        Titulacion t = new Titulacion();
        t.setId(id);
        t.setNombre(nombre);
        return t;
    }

    @Test
    void noEliminaUnaTitulacionConAsignaturas() throws Exception {
        try (MockedConstruction<TitulacionDAO> daos = mockConstruction(TitulacionDAO.class, (dao, ctx) -> {
            when(dao.buscarPorId(3)).thenReturn(titulacion(3, "Informática"));
            when(dao.tieneAsignaturas(3)).thenReturn(true);
        })) {
            TitulacionService servicio = new TitulacionService();
            ApiException e = assertThrows(ApiException.class, () -> servicio.eliminar(3));

            assertEquals(409, e.getHttpCode());
            verify(daos.constructed().get(0), never()).eliminar(anyInt());
        }
    }

    @Test
    void eliminaUnaTitulacionSinAsignaturas() throws Exception {
        try (MockedConstruction<TitulacionDAO> daos = mockConstruction(TitulacionDAO.class, (dao, ctx) -> {
            when(dao.buscarPorId(3)).thenReturn(titulacion(3, "Informática"));
            when(dao.tieneAsignaturas(3)).thenReturn(false);
        })) {
            new TitulacionService().eliminar(3);

            verify(daos.constructed().get(0)).eliminar(3);
        }
    }

    @Test
    void eliminarUnaTitulacionInexistenteDa404() throws Exception {
        try (MockedConstruction<TitulacionDAO> daos = mockConstruction(TitulacionDAO.class)) {
            TitulacionService servicio = new TitulacionService();
            assertEquals(404, assertThrows(ApiException.class, () -> servicio.eliminar(99)).getHttpCode());
        }
    }

    @Test
    void elAltaExigeNombreYRespetaLaLongitudDeLaColumna() throws Exception {
        try (MockedConstruction<TitulacionDAO> daos = mockConstruction(TitulacionDAO.class)) {
            TitulacionService servicio = new TitulacionService();
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(titulacion(0, null))).getHttpCode());
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(titulacion(0, "   "))).getHttpCode());
            assertEquals(400, assertThrows(ApiException.class,
                    () -> servicio.alta(titulacion(0, "x".repeat(201)))).getHttpCode());
            verify(daos.constructed().get(0), never()).insertar(any());
        }
    }

    @Test
    void laDescripcionTieneUnLimiteDeLongitud() throws Exception {
        try (MockedConstruction<TitulacionDAO> daos = mockConstruction(TitulacionDAO.class)) {
            TitulacionService servicio = new TitulacionService();
            Titulacion larga = titulacion(0, "Física");
            larga.setDescripcion("x".repeat(10_001));

            assertEquals(400, assertThrows(ApiException.class, () -> servicio.alta(larga)).getHttpCode());
            verify(daos.constructed().get(0), never()).insertar(any());

            Titulacion justa = titulacion(0, "Física");
            justa.setDescripcion("x".repeat(10_000));
            servicio.alta(justa);
            verify(daos.constructed().get(0)).insertar(justa);
        }
    }

    @Test
    void elAltaDevuelveLaTitulacionConSuIdGenerado() throws Exception {
        try (MockedConstruction<TitulacionDAO> daos = mockConstruction(TitulacionDAO.class, (dao, ctx) ->
                doAnswer(inv -> { ((Titulacion) inv.getArgument(0)).setId(42); return null; })
                        .when(dao).insertar(any(Titulacion.class)))) {

            Titulacion creada = new TitulacionService().alta(titulacion(0, "Física"));

            assertEquals(42, creada.getId());
        }
    }

    @Test
    void modificarUnProfesorInexistenteDa404YSinNombreDa400() throws Exception {
        try (MockedConstruction<ProfesorDAO> daos = mockConstruction(ProfesorDAO.class)) {
            ProfesorService servicio = new ProfesorService();
            Profesor sinNombre = new Profesor();
            Profesor conNombre = new Profesor();
            conNombre.setId(8);
            conNombre.setNombre("Marta");

            assertEquals(400, assertThrows(ApiException.class, () -> servicio.modificar(sinNombre)).getHttpCode());
            assertEquals(404, assertThrows(ApiException.class, () -> servicio.modificar(conNombre)).getHttpCode());
            verify(daos.constructed().get(0), never()).actualizar(any());
        }
    }

    @Test
    void eliminarUnProfesorDelegaEnElDaoTransaccional() throws Exception {
        Profesor p = new Profesor();
        p.setId(8);
        try (MockedConstruction<ProfesorDAO> daos = mockConstruction(ProfesorDAO.class,
                     (dao, ctx) -> when(dao.buscarPorId(8)).thenReturn(p))) {
            ProfesorService servicio = new ProfesorService();

            servicio.eliminar(8);
            assertSame(p, servicio.obtenerPorId(8));

            verify(daos.constructed().get(0)).eliminar(8);
        }
    }
}
