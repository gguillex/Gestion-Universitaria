package gestion.modelo;

import gestion.bean.Asignatura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que encapsula todas las operaciones SQL sobre {@code asignaturas}.
 * Las consultas de listado incluyen JOINs con {@code titulaciones} y {@code profesores}
 * para obtener los nombres de visualización sin lógica adicional en la capa de acciones.
 */
public class AsignaturaDAO {

    public List<Asignatura> listar() throws Exception {
        List<Asignatura> lista = new ArrayList<>();
        String sql =
            "SELECT a.id, a.nombre, a.capacidad_maxima, a.id_titulacion, a.id_profesor, " +
            "       t.nombre AS nombre_titulacion, p.nombre AS nombre_profesor " +
            "FROM asignaturas a " +
            "JOIN titulaciones t ON t.id = a.id_titulacion " +
            "LEFT JOIN profesores p ON p.id = a.id_profesor " +
            "ORDER BY a.nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Asignatura a = construir(rs);
                lista.add(a);
            }
        }
        return lista;
    }

    public Asignatura buscarPorId(int id) throws Exception {
        String sql =
            "SELECT a.id, a.nombre, a.capacidad_maxima, a.id_titulacion, a.id_profesor, " +
            "       t.nombre AS nombre_titulacion, p.nombre AS nombre_profesor " +
            "FROM asignaturas a " +
            "JOIN titulaciones t ON t.id = a.id_titulacion " +
            "LEFT JOIN profesores p ON p.id = a.id_profesor " +
            "WHERE a.id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construir(rs);
            }
        }
        return null;
    }

    public void insertar(Asignatura a) throws Exception {
        String sql = "INSERT INTO asignaturas (nombre, capacidad_maxima, id_titulacion, id_profesor) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNombre());
            ps.setInt(2, a.getCapacidadMaxima());
            ps.setInt(3, a.getIdTitulacion());
            if (a.getIdProfesor() != null) ps.setInt(4, a.getIdProfesor());
            else                            ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves != null && claves.next()) a.setId(claves.getInt(1));
            }
        }
    }

    public void actualizar(Asignatura a) throws Exception {
        String sql = "UPDATE asignaturas SET nombre = ?, capacidad_maxima = ?, id_titulacion = ?, id_profesor = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setInt(2, a.getCapacidadMaxima());
            ps.setInt(3, a.getIdTitulacion());
            if (a.getIdProfesor() != null) ps.setInt(4, a.getIdProfesor());
            else                            ps.setNull(4, Types.INTEGER);
            ps.setInt(5, a.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM asignaturas WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void asignarProfesor(int idAsignatura, Integer idProfesor) throws Exception {
        String sql = "UPDATE asignaturas SET id_profesor = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (idProfesor != null) ps.setInt(1, idProfesor);
            else                     ps.setNull(1, Types.INTEGER);
            ps.setInt(2, idAsignatura);
            ps.executeUpdate();
        }
    }

    private Asignatura construir(ResultSet rs) throws SQLException {
        Asignatura a = new Asignatura();
        a.setId(rs.getInt("id"));
        a.setNombre(rs.getString("nombre"));
        a.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        a.setIdTitulacion(rs.getInt("id_titulacion"));
        int idProf = rs.getInt("id_profesor");
        a.setIdProfesor(rs.wasNull() ? null : idProf);
        a.setNombreTitulacion(rs.getString("nombre_titulacion"));
        a.setNombreProfesor(rs.getString("nombre_profesor"));
        return a;
    }
}
