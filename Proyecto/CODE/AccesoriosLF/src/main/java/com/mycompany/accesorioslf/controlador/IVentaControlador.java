package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.DetallePedido;
import java.sql.SQLException;
import java.util.List;

public interface IVentaControlador {
    int registrarVenta(String clienteNombre, List<DetallePedido> detalles) throws SQLException;
    List<Object[]> getReporteAltaRotacion();
}