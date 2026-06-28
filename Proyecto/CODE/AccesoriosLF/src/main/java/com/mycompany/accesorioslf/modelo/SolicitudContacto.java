package com.mycompany.accesorioslf.modelo;

import java.time.LocalDate;

public class SolicitudContacto {
    private int id;
    private String nombre;
    private String telefono;
    private String descripcionPedido;
    private LocalDate fecha;

    public SolicitudContacto(int id, String nombre, String telefono, String descripcionPedido, LocalDate fecha) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.descripcionPedido = descripcionPedido;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDescripcionPedido() { return descripcionPedido; }
    public void setDescripcionPedido(String descripcionPedido) { this.descripcionPedido = descripcionPedido; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}