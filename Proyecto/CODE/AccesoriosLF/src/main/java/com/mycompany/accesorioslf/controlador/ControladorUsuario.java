package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorUsuario {
    private Connection conexion;

    public ControladorUsuario() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean autenticarAdmin(String usuario, String password) {
        String sql = "SELECT rol FROM usuarios WHERE usuario = ? AND password = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "admin".equals(rs.getString("rol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String obtenerRol(String usuario) {
        String sql = "SELECT rol FROM usuarios WHERE usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("rol");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String[]> obtenerUsuarios() {
        List<String[]> usuarios = new ArrayList<>();
        String sql = "SELECT usuario, rol FROM usuarios";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(new String[]{rs.getString("usuario"), rs.getString("rol")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public void cambiarRol(String usuario, String nuevoRol) {
        String sql = "UPDATE usuarios SET rol = ? WHERE usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoRol);
            pstmt.setString(2, usuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}