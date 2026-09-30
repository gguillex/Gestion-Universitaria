package gestion.modelo;

import gestion.bean.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario buscarPorNombre(String nombre) throws Exception {
        String sql = "SELECT id, nombre, password, rol FROM usuarios WHERE nombre = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construir(rs);
                }
            }
        }
        return null;
    }

    public List<Usuario> listar() throws Exception {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, password, rol FROM usuarios ORDER BY nombre";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(construir(rs));
            }
        }
        return lista;
    }

    public Usuario buscarPorId(int id) throws Exception {
        String sql = "SELECT id, nombre, password, rol FROM usuarios WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construir(rs);
                }
            }
        }
        return null;
    }

    public void insertar(Usuario u) throws Exception {
        String sql = "INSERT INTO usuarios (nombre, password, rol) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRol());
            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario u) throws Exception {
        String sql = "UPDATE usuarios SET nombre = ?, password = ?, rol = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRol());
            ps.setInt(4, u.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws Exception {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Comprueba si ya existe un usuario con el nombre dado. Usado en el autoregistro. */
    public boolean existeNombre(String nombre) throws Exception {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private Usuario construir(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("password"),
                rs.getString("rol"));
    }
}
