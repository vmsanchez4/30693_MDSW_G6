package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.Producto;
import com.mycompany.accesorioslf.modelo.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorProducto {
    private Connection conexion;

    public ControladorProducto() {
        try {
            conexion = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Producto> getProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY id";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(extraerProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Producto> buscarPorNombre(String texto) {
        List<Producto> lista = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) return getProductos();
        String sql = "SELECT * FROM productos WHERE nombre LIKE ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, "%" + texto + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(extraerProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void agregarProducto(Producto p) {
        String sql = "INSERT INTO productos (nombre, stock, precio, descripcion, imagen, fechaRegistro, modelo, proveedor, categoria, contacto_proveedor, telefono_proveedor) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setInt(2, p.getStock());
            pstmt.setDouble(3, p.getPrecio());
            pstmt.setString(4, p.getDescripcion());
            pstmt.setString(5, p.getImagen());
            pstmt.setString(6, p.getFechaRegistro());
            pstmt.setString(7, p.getModelo());
            pstmt.setString(8, p.getProveedor());
            pstmt.setString(9, p.getCategoria());
            pstmt.setString(10, p.getContactoProveedor());
            pstmt.setString(11, p.getTelefonoProveedor());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarProducto(Producto p) {
        String sql = "UPDATE productos SET nombre=?, stock=?, precio=?, descripcion=?, imagen=?, fechaRegistro=?, modelo=?, proveedor=?, categoria=?, contacto_proveedor=?, telefono_proveedor=? WHERE id=?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, p.getNombre());
            pstmt.setInt(2, p.getStock());
            pstmt.setDouble(3, p.getPrecio());
            pstmt.setString(4, p.getDescripcion());
            pstmt.setString(5, p.getImagen());
            pstmt.setString(6, p.getFechaRegistro());
            pstmt.setString(7, p.getModelo());
            pstmt.setString(8, p.getProveedor());
            pstmt.setString(9, p.getCategoria());
            pstmt.setString(10, p.getContactoProveedor());
            pstmt.setString(11, p.getTelefonoProveedor());
            pstmt.setInt(12, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Producto obtenerProductoPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return extraerProducto(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Producto extraerProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
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
    }
}