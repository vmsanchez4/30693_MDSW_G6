package com.mycompany.accesorioslf.modelo;

import java.time.LocalDate;
import java.util.List;

public class SolicitudProveedor {
    private int id;
    private LocalDate fecha;
    private String estado; // "pendiente", "en_proceso", "completado"
    private List<ItemSolicitudProveedor> items;

    public SolicitudProveedor(int id, LocalDate fecha, String estado, List<ItemSolicitudProveedor> items) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<ItemSolicitudProveedor> getItems() {
        return items;
    }

    public void setItems(List<ItemSolicitudProveedor> items) {
        this.items = items;
    }
    
}
