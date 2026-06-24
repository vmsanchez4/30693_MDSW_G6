package com.mycompany.accesorioslf.modelo;

public class ProductoSolicitado {
    private int id;
    private int solicitudId;
    private int productoId;
    private int cantidad;
    private String estado;

    public ProductoSolicitado(int id, int solicitudId, int productoId, int cantidad, String estado) {
        this.id = id;
        this.solicitudId = solicitudId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSolicitudId() { return solicitudId; }
    public void setSolicitudId(int solicitudId) { this.solicitudId = solicitudId; }
    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}