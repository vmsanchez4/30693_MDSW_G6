package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.*;
import java.util.Set;
import java.util.HashSet;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorSolicitudProveedor implements ISolicitudProveedorControlador {
    private Connection conexion;
    private static final Logger LOGGER = Logger.getLogger(ControladorSolicitudProveedor.class.getName());

    public ControladorSolicitudProveedor() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la BD", e);
        }
    }

    @Override
    public int crearSolicitudProveedor(List<ItemSolicitudProveedor> items) throws SQLException {
        int solicitudId = -1;
        conexion.setAutoCommit(false);
        try {
            String sqlSolicitud = "INSERT INTO solicitudes_proveedor (fecha, estado) VALUES (?, 'pendiente')";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlSolicitud, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, LocalDate.now().toString());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) solicitudId = rs.getInt(1);
            }

            if (solicitudId == -1) throw new SQLException("No se pudo obtener ID de solicitud.");

            String sqlDetalle = "INSERT INTO detalle_solicitud_proveedor (solicitud_proveedor_id, producto_id, cantidad) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlDetalle)) {
                for (ItemSolicitudProveedor item : items) {
                    pstmt.setInt(1, solicitudId);
                    pstmt.setInt(2, item.getProducto().getId());
                    pstmt.setInt(3, item.getCantidad());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            LOGGER.log(Level.SEVERE, "Error al crear solicitud a proveedor", e);
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
        return solicitudId;
    }

    @Override
    public void actualizarEstadoSolicitud(int solicitudId, String nuevoEstado) throws SQLException {
        String sql = "UPDATE solicitudes_proveedor SET estado = ? WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, solicitudId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar estado", e);
            throw e;
        }
    }

    @Override
    public List<SolicitudProveedor> getSolicitudesProveedor() {
        List<SolicitudProveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes_proveedor ORDER BY fecha DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
                String estado = rs.getString("estado");
                List<ItemSolicitudProveedor> items = obtenerItemsSolicitud(id);
                lista.add(new SolicitudProveedor(id, fecha, estado, items));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener solicitudes proveedor", e);
        }
        return lista;
    }

    @Override
    public Set<Integer> getProductosSolicitadosIds() {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT DISTINCT producto_id FROM detalle_solicitud_proveedor";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ids.add(rs.getInt("producto_id"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener IDs de productos solicitados", e);
        }
        return ids;
    }

    private List<ItemSolicitudProveedor> obtenerItemsSolicitud(int solicitudId) {
        List<ItemSolicitudProveedor> items = new ArrayList<>();
        String sql = "SELECT d.producto_id, d.cantidad, p.* FROM detalle_solicitud_proveedor d "
                   + "JOIN productos p ON d.producto_id = p.id WHERE d.solicitud_proveedor_id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, solicitudId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto(
                    rs.getInt("producto_id"),
                    rs.getString("nombre"),
                    rs.getInt("stock"),
                    rs.getDouble("precio"),
                    rs.getString("descripcion"),
                    rs.getString("imagen"),
                    rs.getString("fechaRegistro"),
                    rs.getString("modelo"),
                    rs.getString("proveedor"),
                    rs.getString("categoria"),
                    rs.getString("contacto_proveedor"),
                    rs.getString("telefono_proveedor")
                );
                items.add(new ItemSolicitudProveedor(prod, rs.getInt("cantidad")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener items de solicitud", e);
        }
        return items;
    }
}