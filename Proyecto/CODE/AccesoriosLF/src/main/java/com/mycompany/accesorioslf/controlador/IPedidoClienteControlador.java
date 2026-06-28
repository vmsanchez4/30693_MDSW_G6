package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.ItemPedidoCliente;
import com.mycompany.accesorioslf.modelo.PedidoCliente;
import java.sql.SQLException;
import java.util.List;

public interface IPedidoClienteControlador {
    int guardarPedidoCliente(String nombre, String telefono, List<ItemPedidoCliente> items) throws SQLException;
    List<PedidoCliente> getPedidosCliente();
}