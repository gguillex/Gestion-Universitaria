package gestion.modelo;

import gestion.bean.Alumno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que encapsula todas las operaciones SQL sobre las tablas
 * {@code alumnos} y {@code matriculas}. No contiene lógica de negocio.
 */
public class AlumnoDAO {

    /**
     * Devuelve todos los alumnos ordenados por nombre.
     */
    public List<Alumno> listar() throws Exception {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email, dni FROM alumnos ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(construir(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un alumno por su clave primaria. Devuelve {@code null} si no existe.
     */
    public Alumno buscarPorId(int id) throws Exception {
        String sql = "SELECT id, nombre, email, dni FROM alumnos WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construir(rs);
            }
        }
        return null;
    }

    /**
     * Inserta un nuevo alumno en la base de datos.
     */
    public void insertar(Alumno a) throws Exception {
        String sql = "INSERT INTO alumnos (nombre, email, dni) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getEmail());
            ps.setString(3, a.getDni());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de un alumno existente.
     */
    public void actualizar(Alumno a) throws Exception {
        String sql = "UPDATE alumnos SET nombre = ?, email = ?, dni = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getEmail());
            ps.setString(3, a.getDni());
            ps.setInt(4, a.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina un alumno y todas sus matrículas en una transacción atómica.
     */
    public void eliminar(int id) throws Exception {
        String sql1 = "DELETE FROM matriculas WHERE id_alumno = ?";
        String sql2 = "DELETE FROM alumnos WHERE id = ?";
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(sql1);
                 PreparedStatement ps2 = con.prepareStatement(sql2)) {
                ps1.setInt(1, id); ps1.executeUpdate();
                ps2.setInt(1, id); ps2.executeUpdate();
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Devuelve los alumnos matriculados en una asignatura concreta, ordenados por nombre.
     */
    public List<Alumno> listarPorAsignatura(int idAsignatura) throws Exception {
        List<Alumno> lista = new ArrayList<>();
        String sql =
            "SELECT a.id, a.nombre, a.email, a.dni " +
            "FROM alumnos a " +
            "JOIN matriculas m ON m.id_alumno = a.id " +
            "WHERE m.id_asignatura = ? " +
            "ORDER BY a.nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignatura);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(construir(rs));
            }
        }
        return lista;
    }

    /**
     * Cuenta cuántos alumnos están matriculados en una asignatura.
     * Se usa para validar la capacidad máxima antes de matricular.
     */
    public int contarMatriculados(int idAsignatura) throws Exception {
        String sql = "SELECT COUNT(*) FROM matriculas WHERE id_asignatura = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAsignatura);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Comprueba si un alumno ya está matriculado en una asignatura determinada.
     */
    public boolean estaMatriculado(int idAlumno, int idAsignatura) throws Exception {
        String sql = "SELECT COUNT(*) FROM matriculas WHERE id_alumno = ? AND id_asignatura = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            ps.setInt(2, idAsignatura);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Matricula a un alumno en una asignatura de forma atómica: bloquea la fila de la
     * asignatura ({@code SELECT ... FOR UPDATE}) y comprueba el hueco disponible dentro
     * de la misma transacción, de modo que dos matriculaciones concurrentes no puedan
     * superar juntas la capacidad máxima.
     *
     * @return {@code true} si se matriculó, {@code false} si no había hueco disponible.
     */
    public boolean matricular(int idAlumno, int idAsignatura) throws Exception {
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try {
                int capacidad;
                String sqlLock = "SELECT capacidad_maxima FROM asignaturas WHERE id = ? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(sqlLock)) {
                    ps.setInt(1, idAsignatura);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("La asignatura no existe.");
                        capacidad = rs.getInt(1);
                    }
                }

                int matriculados;
                String sqlCount = "SELECT COUNT(*) FROM matriculas WHERE id_asignatura = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlCount)) {
                    ps.setInt(1, idAsignatura);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        matriculados = rs.getInt(1);
                    }
                }

                if (matriculados >= capacidad) {
                    con.rollback();
                    return false;
                }

                String sqlInsert = "INSERT INTO matriculas (id_alumno, id_asignatura) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setInt(1, idAlumno);
                    ps.setInt(2, idAsignatura);
                    ps.executeUpdate();
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Desmatricula a un alumno de una asignatura (elimina la fila de {@code matriculas}).
     */
    public void desmatricular(int idAlumno, int idAsignatura) throws Exception {
        String sql = "DELETE FROM matriculas WHERE id_alumno = ? AND id_asignatura = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            ps.setInt(2, idAsignatura);
            ps.executeUpdate();
        }
    }

    /** Mapea una fila del ResultSet a un objeto Alumno. */
    private Alumno construir(ResultSet rs) throws SQLException {
        return new Alumno(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getString("dni")
        );
    }
}
