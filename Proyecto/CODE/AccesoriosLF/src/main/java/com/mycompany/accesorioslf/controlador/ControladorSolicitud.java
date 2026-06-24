package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.SolicitudContacto;
import com.mycompany.accesorioslf.modelo.ProductoSolicitado;
import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorSolicitud {
    private Connection conexion;

    public ControladorSolicitud() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int guardarSolicitudContacto(String nombre, String telefono, String descripcion) {
        String sql = "INSERT INTO solicitudes_contacto (nombre, telefono, descripcion_pedido, fecha) VALUES (?, ?, ?, ?)";
        int idGenerado = -1;
        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, telefono);
            pstmt.setString(3, descripcion);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) idGenerado = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerado;
    }

    public List<SolicitudContacto> getSolicitudesContacto() {
        List<SolicitudContacto> lista = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes_contacto ORDER BY fecha DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new SolicitudContacto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("descripcion_pedido"),
                    LocalDate.parse(rs.getString("fecha"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void agregarProductoSolicitado(int solicitudId, int productoId, int cantidad) {
        String sql = "INSERT INTO productos_solicitados (solicitud_id, producto_id, cantidad, estado) VALUES (?, ?, ?, 'solicitado')";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, solicitudId);
            pstmt.setInt(2, productoId);
            pstmt.setInt(3, cantidad);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ProductoSolicitado> getProductosSolicitadosPorSolicitud(int solicitudId) {
        List<ProductoSolicitado> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos_solicitados WHERE solicitud_id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, solicitudId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new ProductoSolicitado(
                    rs.getInt("id"),
                    rs.getInt("solicitud_id"),
                    rs.getInt("producto_id"),
                    rs.getInt("cantidad"),
                    rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Object[]> getSolicitudesConProductos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT s.id, s.nombre, s.telefono, s.fecha, GROUP_CONCAT(p.nombre || ' (' || ps.cantidad || ')', ', ') as productos "
                   + "FROM solicitudes_contacto s "
                   + "LEFT JOIN productos_solicitados ps ON s.id = ps.solicitud_id "
                   + "LEFT JOIN productos p ON ps.producto_id = p.id "
                   + "GROUP BY s.id ORDER BY s.fecha DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("fecha"),
                    rs.getString("productos") != null ? rs.getString("productos") : "Ninguno"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}