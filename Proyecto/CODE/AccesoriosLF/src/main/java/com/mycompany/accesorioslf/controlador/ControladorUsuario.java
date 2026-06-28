package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorUsuario implements IUsuarioControlador {
    private Connection conexion;
    private static final Logger LOGGER = Logger.getLogger(ControladorUsuario.class.getName());

    public ControladorUsuario() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la BD", e);
        }
    }

    @Override
    public String autenticar(String usuario, String password) {
        String hashed = DatabaseConnection.hashPassword(password);
        String sql = "SELECT rol FROM usuarios WHERE usuario = ? AND password = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            pstmt.setString(2, hashed);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("rol");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en autenticación", e);
        }
        return null;
    }

    @Override
    public String obtenerRol(String usuario) {
        String sql = "SELECT rol FROM usuarios WHERE usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("rol");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener rol", e);
        }
        return null;
    }

    @Override
    public List<String[]> obtenerUsuarios() {
        List<String[]> usuarios = new ArrayList<>();
        String sql = "SELECT usuario, rol FROM usuarios";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(new String[]{rs.getString("usuario"), rs.getString("rol")});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios", e);
        }
        return usuarios;
    }

    @Override
    public void cambiarRol(String usuario, String nuevoRol) {
        String sql = "UPDATE usuarios SET rol = ? WHERE usuario = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoRol);
            pstmt.setString(2, usuario);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cambiar rol", e);
        }
    }
}