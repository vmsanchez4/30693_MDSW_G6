package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControladorPedidoCliente implements IPedidoClienteControlador {
    private Connection conexion;
    private static final Logger LOGGER = Logger.getLogger(ControladorPedidoCliente.class.getName());

    public ControladorPedidoCliente() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la BD", e);
        }
    }

    @Override
    public int guardarPedidoCliente(String nombre, String telefono, List<ItemPedidoCliente> items) throws SQLException {
        int pedidoId = -1;
        conexion.setAutoCommit(false);
        try {
            String sqlPedido = "INSERT INTO pedidos_cliente (nombre_cliente, telefono, fecha) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, telefono);
                pstmt.setString(3, LocalDate.now().toString());
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) pedidoId = rs.getInt(1);
            }

            if (pedidoId == -1) throw new SQLException("No se pudo obtener ID del pedido.");

            String sqlDetalle = "INSERT INTO detalle_pedido_cliente (pedido_cliente_id, producto_id, cantidad) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sqlDetalle)) {
                for (ItemPedidoCliente item : items) {
                    pstmt.setInt(1, pedidoId);
                    pstmt.setInt(2, item.getProducto().getId());
                    pstmt.setInt(3, item.getCantidad());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            LOGGER.log(Level.SEVERE, "Error al guardar pedido cliente", e);
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
        return pedidoId;
    }

    @Override
    public List<PedidoCliente> getPedidosCliente() {
        List<PedidoCliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos_cliente ORDER BY fecha DESC";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre_cliente");
                String telefono = rs.getString("telefono");
                LocalDate fecha = LocalDate.parse(rs.getString("fecha"));
                List<ItemPedidoCliente> items = obtenerItemsPedidoCliente(id);
                lista.add(new PedidoCliente(id, nombre, telefono, fecha, items));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener pedidos cliente", e);
        }
        return lista;
    }

    private List<ItemPedidoCliente> obtenerItemsPedidoCliente(int pedidoId) {
        List<ItemPedidoCliente> items = new ArrayList<>();
        String sql = "SELECT d.producto_id, d.cantidad, p.* FROM detalle_pedido_cliente d "
                   + "JOIN productos p ON d.producto_id = p.id WHERE d.pedido_cliente_id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, pedidoId);
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
                items.add(new ItemPedidoCliente(prod, rs.getInt("cantidad")));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener items de pedido cliente", e);
        }
        return items;
    }
}