package gestion.modelo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Regla de negocio: no se matricula si alumnos_actuales >= capacidad_maxima.
 * Solo se simula la frontera con la BD (ConexionBD + JDBC); el método del DAO
 * se ejecuta de verdad.
 */
class AlumnoDAOTest {

    private MockedStatic<ConexionBD> conexionBD;
    private Connection con;
    private PreparedStatement insert;

    @BeforeEach
    void setUp() {
        con = mock(Connection.class);
        insert = mock(PreparedStatement.class);
        conexionBD = mockStatic(ConexionBD.class);
        conexionBD.when(ConexionBD::getConexion).thenReturn(con);
    }

    @AfterEach
    void tearDown() {
        conexionBD.close();
    }

    /** Simula una asignatura con esa capacidad y ese número de alumnos ya matriculados. */
    private void asignaturaCon(int capacidad, int matriculados) throws Exception {
        PreparedStatement selectCapacidad = consultaQueDevuelve(capacidad);
        PreparedStatement selectCount     = consultaQueDevuelve(matriculados);
        when(con.prepareStatement(startsWith("SELECT capacidad_maxima"))).thenReturn(selectCapacidad);
        when(con.prepareStatement(startsWith("SELECT COUNT(*)"))).thenReturn(selectCount);
        when(con.prepareStatement(startsWith("INSERT INTO matriculas"))).thenReturn(insert);
    }

    private static PreparedStatement consultaQueDevuelve(int valor) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(valor);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(ps.executeQuery()).thenReturn(rs);
        return ps;
    }

    @Test
    void matriculaSiQuedaAlMenosUnHueco() throws Exception {
        asignaturaCon(30, 29);

        assertTrue(new AlumnoDAO().matricular(1, 5));
        verify(insert).executeUpdate();
        verify(con).commit();
    }

    @Test
    void noMatriculaSiLaAsignaturaEstaJustoLlena() throws Exception {
        asignaturaCon(30, 30);

        assertFalse(new AlumnoDAO().matricular(1, 5));
        verify(insert, never()).executeUpdate();
        verify(con).rollback();
        verify(con, never()).commit();
    }

    @Test
    void noMatriculaSiYaSuperaLaCapacidad() throws Exception {
        // Puede pasar si alguien reduce la capacidad con alumnos ya matriculados
        asignaturaCon(30, 31);

        assertFalse(new AlumnoDAO().matricular(1, 5));
        verify(insert, never()).executeUpdate();
    }
}
