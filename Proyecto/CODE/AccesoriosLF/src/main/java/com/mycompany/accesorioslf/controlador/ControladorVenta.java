package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.DetallePedido;
import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorVenta {
    private Connection conexion;

    public ControladorVenta() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int registrarVenta(String clienteNombre, List<DetallePedido> detalles) {
        String sqlPedido = "INSERT INTO pedidos (cliente_nombre, fecha, total) VALUES (?, ?, ?)";
        int pedidoId = -1;
        double total = detalles.stream().mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario()).sum();
        try (PreparedStatement pstmt = conexion.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, clienteNombre);
            pstmt.setString(2, LocalDate.now().toString());
            pstmt.setDouble(3, total);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) pedidoId = rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }

        if (pedidoId != -1) {
            String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlDetalle)) {
                for (DetallePedido d : detalles) {
                    pstmt.setInt(1, pedidoId);
                    pstmt.setInt(2, d.getProductoId());
                    pstmt.setInt(3, d.getCantidad());
                    pstmt.setDouble(4, d.getPrecioUnitario());
                    pstmt.addBatch();
                    actualizarStock(d.getProductoId(), -d.getCantidad());
                }
                pstmt.executeBatch();
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return pedidoId;
    }

    private void actualizarStock(int productoId, int cambio) {
        String sql = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, cambio);
            pstmt.setInt(2, productoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getReporteAltaRotacion() {
        List<Object[]> reporte = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, SUM(d.cantidad) as vendidos, p.stock as stock_actual, "
                   + "ROUND(SUM(d.cantidad) / 30.0, 2) as velocidad "
                   + "FROM detalle_pedido d "
                   + "JOIN productos p ON d.producto_id = p.id "
                   + "JOIN pedidos ped ON d.pedido_id = ped.id "
                   + "WHERE ped.fecha >= date('now', '-30 days') "
                   + "GROUP BY p.id "
                   + "ORDER BY vendidos DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reporte.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("vendidos"),
                    rs.getInt("stock_actual"),
                    rs.getDouble("velocidad")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reporte;
    }
}