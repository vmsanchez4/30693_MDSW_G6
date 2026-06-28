package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.SolicitudContacto;
import com.mycompany.accesorioslf.modelo.ProductoSolicitado;
import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorSolicitud implements ISolicitudControlador {
    private Connection conexion;
    private static final Logger LOGGER = Logger.getLogger(ControladorSolicitud.class.getName());

    public ControladorSolicitud() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la BD", e);
        }
    }

    @Override
    public int guardarSolicitudCompleta(String nombre, String telefono, String descripcion,
                                        List<ProductoSolicitado> productos) throws SQLException {
        int solicitudId = -1;
        conexion.setAutoCommit(false);
        try {
            String sqlSolicitud = "INSERT INTO solicitudes_contacto (nombre, telefono, descripcion_pedido, fecha) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlSolicitud, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, telefono);
                pstmt.setString(3, descripcion);
                pstmt.setString(4, LocalDate.now().toString());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) solicitudId = rs.getInt(1);
            }

            if (solicitudId == -1) throw new SQLException("No se pudo obtener ID de solicitud.");

            if (productos != null && !productos.isEmpty()) {
                String sqlProd = "INSERT INTO productos_solicitados (solicitud_id, producto_id, cantidad, estado) VALUES (?, ?, ?, 'solicitado')";
                try (PreparedStatement pstmt = conexion.prepareStatement(sqlProd)) {
                    for (ProductoSolicitado ps : productos) {
                        pstmt.setInt(1, solicitudId);
                        pstmt.setInt(2, ps.getProductoId());
                        pstmt.setInt(3, ps.getCantidad());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }

            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            LOGGER.log(Level.SEVERE, "Error al guardar solicitud completa", e);
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
        return solicitudId;
    }

    @Override
    public void agregarProductoSolicitado(int solicitudId, int productoId, int cantidad) throws SQLException {
        String sql = "INSERT INTO productos_solicitados (solicitud_id, producto_id, cantidad, estado) VALUES (?, ?, ?, 'solicitado')";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, solicitudId);
            pstmt.setInt(2, productoId);
            pstmt.setInt(3, cantidad);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al agregar producto solicitado", e);
            throw e;
        }
    }

    @Override
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
            LOGGER.log(Level.SEVERE, "Error al obtener solicitudes", e);
        }
        return lista;
    }

    @Override
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
            LOGGER.log(Level.SEVERE, "Error al obtener productos solicitados", e);
        }
        return lista;
    }

    @Override
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
            LOGGER.log(Level.SEVERE, "Error al obtener solicitudes con productos", e);
        }
        return lista;
    }
}