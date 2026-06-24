package com.mycompany.accesorioslf.modelo;

public class Producto {
    private int id;
    private String nombre;
    private int stock;
    private double precio;
    private String descripcion;
    private String imagen;
    private String fechaRegistro;
    private String modelo;
    private String proveedor;
    private String categoria;
    private String contactoProveedor;
    private String telefonoProveedor;

    public Producto(int id, String nombre, int stock, double precio, String descripcion, String imagen,
                    String fechaRegistro, String modelo, String proveedor, String categoria,
                    String contactoProveedor, String telefonoProveedor) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.fechaRegistro = fechaRegistro;
        this.modelo = modelo;
        this.proveedor = proveedor;
        this.categoria = categoria;
        this.contactoProveedor = contactoProveedor;
        this.telefonoProveedor = telefonoProveedor;
    }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getContactoProveedor() { return contactoProveedor; }
    public void setContactoProveedor(String contactoProveedor) { this.contactoProveedor = contactoProveedor; }
    public String getTelefonoProveedor() { return telefonoProveedor; }
    public void setTelefonoProveedor(String telefonoProveedor) { this.telefonoProveedor = telefonoProveedor; }

    public String getDescripcionBreve() {
        if (descripcion == null) return "";
        return descripcion.length() > 60 ? descripcion.substring(0, 60) : descripcion;
    }

    @Override
    public String toString() {
        return nombre + " (Stock: " + stock + ")";
    }
}