package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.ItemSolicitudProveedor;
import com.mycompany.accesorioslf.modelo.SolicitudProveedor;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface ISolicitudProveedorControlador {
    int crearSolicitudProveedor(List<ItemSolicitudProveedor> items) throws SQLException;
    void actualizarEstadoSolicitud(int solicitudId, String nuevoEstado) throws SQLException;
    List<SolicitudProveedor> getSolicitudesProveedor();
    Set<Integer> getProductosSolicitadosIds();
}