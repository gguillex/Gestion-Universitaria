package gestion.modelo;

import gestion.bean.Titulacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO que encapsula todas las operaciones SQL sobre la tabla {@code titulaciones}. */
public class TitulacionDAO {

    public List<Titulacion> listar() throws Exception {
        List<Titulacion> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM titulaciones ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Titulacion(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")));
            }
        }
        return lista;
    }

    public Titulacion buscarPorId(int id) throws Exception {
        String sql = "SELECT id, nombre, descripcion FROM titulaciones WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Titulacion(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"));
                }
            }
        }
        return null;
    }

    public void insertar(Titulacion t) throws Exception {
        String sql = "INSERT INTO titulaciones (nombre, descripcion) VALUES (?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves != null && claves.next()) t.setId(claves.getInt(1));
            }
        }
    }

    public void actualizar(Titulacion t) throws Exception {
        String sql = "UPDATE titulaciones SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM titulaciones WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean tieneAsignaturas(int idTitulacion) throws Exception {
        String sql = "SELECT COUNT(*) FROM asignaturas WHERE id_titulacion = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTitulacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
