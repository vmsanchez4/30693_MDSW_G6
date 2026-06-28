package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.Producto;
import java.sql.SQLException;
import java.util.List;

public interface IProductoControlador {
    List<Producto> getProductos();
    List<Producto> buscarPorNombre(String texto);
    void agregarProducto(Producto p) throws SQLException;
    void actualizarProducto(Producto p) throws SQLException;
    void eliminarProducto(int id) throws SQLException;
    Producto obtenerProductoPorId(int id);
}