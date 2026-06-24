package com.mycompany.accesorioslf.modelo;

import java.time.LocalDate;

public class Pedido {
    private int id;
    private String clienteNombre;
    private LocalDate fecha;
    private double total;

    public Pedido(int id, String clienteNombre, LocalDate fecha, double total) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.fecha = fecha;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}