package com.mycompany.accesorioslf.modelo;

import java.time.LocalDate;
import java.util.List;

public class PedidoCliente {
    private int id;
    private String nombreCliente;
    private String telefono;
    private LocalDate fecha;
    private List<ItemPedidoCliente> items;

    public PedidoCliente(int id, String nombreCliente, String telefono, LocalDate fecha, List<ItemPedidoCliente> items) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.fecha = fecha;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<ItemPedidoCliente> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoCliente> items) {
        this.items = items;
    }

    
}