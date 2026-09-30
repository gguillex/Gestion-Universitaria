package gestion.modelo;

import gestion.bean.Profesor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que encapsula todas las operaciones SQL sobre la tabla {@code profesores}.
 * La eliminación de un profesor desvincula sus asignaturas en una transacción atómica
 * para mantener la coherencia referencial.
 */
public class ProfesorDAO {

    public List<Profesor> listar() throws Exception {
        List<Profesor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, email FROM profesores ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Profesor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email")));
            }
        }
        return lista;
    }

    public Profesor buscarPorId(int id) throws Exception {
        String sql = "SELECT id, nombre, email FROM profesores WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Profesor(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email"));
                }
            }
        }
        return null;
    }

    public void insertar(Profesor p) throws Exception {
        String sql = "INSERT INTO profesores (nombre, email) VALUES (?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getEmail());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves != null && claves.next()) p.setId(claves.getInt(1));
            }
        }
    }

    public void actualizar(Profesor p) throws Exception {
        String sql = "UPDATE profesores SET nombre = ?, email = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getEmail());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws Exception {
        String sql1 = "UPDATE asignaturas SET id_profesor = NULL WHERE id_profesor = ?";
        String sql2 = "DELETE FROM profesores WHERE id = ?";
        try (Connection con = ConexionBD.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(sql1);
                 PreparedStatement ps2 = con.prepareStatement(sql2)) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
                ps2.setInt(1, id);
                ps2.executeUpdate();
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
