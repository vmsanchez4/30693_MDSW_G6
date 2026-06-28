package com.mycompany.accesorioslf.controlador;

import com.mycompany.accesorioslf.modelo.ProductoSolicitado;
import com.mycompany.accesorioslf.modelo.SolicitudContacto;
import java.sql.SQLException;
import java.util.List;

public interface ISolicitudControlador {
    int guardarSolicitudCompleta(String nombre, String telefono, String descripcion,
                                 List<ProductoSolicitado> productos) throws SQLException;
    List<SolicitudContacto> getSolicitudesContacto();
    List<ProductoSolicitado> getProductosSolicitadosPorSolicitud(int solicitudId);
    List<Object[]> getSolicitudesConProductos();
    // Método auxiliar para agregar productos a una solicitud ya existente (si se usa)
    void agregarProductoSolicitado(int solicitudId, int productoId, int cantidad) throws SQLException;
}