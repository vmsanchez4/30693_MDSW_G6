package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.DetallePedido;
import com.mycompany.accesorioslf.modelo.Pedido;
import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorVenta implements IVentaControlador {
    private Connection conexion;
    private static final Logger LOGGER = Logger.getLogger(ControladorVenta.class.getName());

    public ControladorVenta() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la BD", e);
        }
    }

    @Override
    public int registrarVenta(String clienteNombre, List<DetallePedido> detalles) throws SQLException {
        int pedidoId = -1;
        double total = detalles.stream().mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario()).sum();

        conexion.setAutoCommit(false);
        try {
            String sqlPedido = "INSERT INTO pedidos (cliente_nombre, fecha, total) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, clienteNombre);
                pstmt.setString(2, LocalDate.now().toString());
                pstmt.setDouble(3, total);
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) pedidoId = rs.getInt(1);
            }

            if (pedidoId == -1) throw new SQLException("No se pudo obtener el ID del pedido.");

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
            }

            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            LOGGER.log(Level.SEVERE, "Error al registrar venta", e);
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
        return pedidoId;
    }

    private void actualizarStock(int productoId, int cambio) throws SQLException {
        String sql = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, cambio);
            pstmt.setInt(2, productoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar stock", e);
            throw e;
        }
    }

    @Override
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
            LOGGER.log(Level.SEVERE, "Error al generar reporte", e);
        }
        return reporte;
    }

    /**
     * Obtiene todas las ventas (pedidos) ordenadas por fecha descendente.
     */
    public List<Pedido> getHistorialVentas() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos ORDER BY fecha DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String cliente = rs.getString("cliente_nombre");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
                double total = rs.getDouble("total");
                lista.add(new Pedido(id, cliente, fecha, total));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener historial de ventas", e);
        }
        return lista;
    }

    /**
     * Calcula estadísticas: total de ventas, monto total, promedio diario (últimos 30 días).
     */
    public Object[] getEstadisticasVentas() {
        long totalVentas = 0;
        double montoTotal = 0.0;
        double promedioDiario = 0.0;
        String sql = "SELECT COUNT(*) as total_ventas, SUM(total) as monto_total, "
                   + "ROUND(SUM(total) / 30.0, 2) as promedio_diario "
                   + "FROM pedidos WHERE fecha >= date('now', '-30 days')";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                totalVentas = rs.getLong("total_ventas");
                montoTotal = rs.getDouble("monto_total");
                promedioDiario = rs.getDouble("promedio_diario");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener estadísticas de ventas", e);
        }
        return new Object[]{totalVentas, montoTotal, promedioDiario};
    }
}