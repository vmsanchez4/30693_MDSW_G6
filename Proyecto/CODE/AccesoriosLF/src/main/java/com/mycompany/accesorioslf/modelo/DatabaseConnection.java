package com.mycompany.accesorioslf.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:accesorioslf.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver SQLite cargado.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite no encontrado.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBaseDatos() {
        // Tabla productos 
        String createProductos = "CREATE TABLE IF NOT EXISTS productos (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "nombre TEXT NOT NULL,\n"
                + "stock INTEGER NOT NULL,\n"
                + "precio REAL NOT NULL,\n"
                + "descripcion TEXT,\n"
                + "imagen TEXT,\n"
                + "fechaRegistro TEXT NOT NULL,\n"
                + "modelo TEXT,\n"
                + "proveedor TEXT,\n"
                + "categoria TEXT,\n"
                + "contacto_proveedor TEXT DEFAULT '',\n"
                + "telefono_proveedor TEXT DEFAULT ''\n"
                + ");";

        // Tabla usuarios 
        String createUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "usuario TEXT UNIQUE NOT NULL,\n"
                + "password TEXT NOT NULL,\n"
                + "rol TEXT DEFAULT 'admin'\n"
                + ");";

        // Tabla pedidos 
        String createPedidos = "CREATE TABLE IF NOT EXISTS pedidos (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "cliente_nombre TEXT NOT NULL,\n"
                + "fecha TEXT NOT NULL,\n"
                + "total REAL NOT NULL\n"
                + ");";

        String createDetallePedido = "CREATE TABLE IF NOT EXISTS detalle_pedido (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "pedido_id INTEGER NOT NULL,\n"
                + "producto_id INTEGER NOT NULL,\n"
                + "cantidad INTEGER NOT NULL,\n"
                + "precio_unitario REAL NOT NULL,\n"
                + "FOREIGN KEY(pedido_id) REFERENCES pedidos(id),\n"
                + "FOREIGN KEY(producto_id) REFERENCES productos(id)\n"
                + ");";

       

        //  Pedidos de clientes 
        String createPedidosCliente = "CREATE TABLE IF NOT EXISTS pedidos_cliente (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "nombre_cliente TEXT NOT NULL,\n"
                + "telefono TEXT NOT NULL,\n"
                + "fecha TEXT NOT NULL\n"
                + ");";

        String createDetallePedidoCliente = "CREATE TABLE IF NOT EXISTS detalle_pedido_cliente (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "pedido_cliente_id INTEGER NOT NULL,\n"
                + "producto_id INTEGER NOT NULL,\n"
                + "cantidad INTEGER NOT NULL,\n"
                + "FOREIGN KEY(pedido_cliente_id) REFERENCES pedidos_cliente(id),\n"
                + "FOREIGN KEY(producto_id) REFERENCES productos(id)\n"
                + ");";

        // Solicitudes a proveedores 
        String createSolicitudesProveedor = "CREATE TABLE IF NOT EXISTS solicitudes_proveedor (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "fecha TEXT NOT NULL,\n"
                + "estado TEXT DEFAULT 'pendiente'\n"
                + ");";

        String createDetalleSolicitudProveedor = "CREATE TABLE IF NOT EXISTS detalle_solicitud_proveedor (\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "solicitud_proveedor_id INTEGER NOT NULL,\n"
                + "producto_id INTEGER NOT NULL,\n"
                + "cantidad INTEGER NOT NULL,\n"
                + "FOREIGN KEY(solicitud_proveedor_id) REFERENCES solicitudes_proveedor(id),\n"
                + "FOREIGN KEY(producto_id) REFERENCES productos(id)\n"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
          
            stmt.execute(createProductos);
            stmt.execute(createUsuarios);
            stmt.execute(createPedidos);
            stmt.execute(createDetallePedido);
            stmt.execute(createPedidosCliente);
            stmt.execute(createDetallePedidoCliente);
            stmt.execute(createSolicitudesProveedor);
            stmt.execute(createDetalleSolicitudProveedor);
/*
            // Insertar admin si no existe
            stmt.execute("INSERT OR IGNORE INTO usuarios (usuario, password, rol) VALUES ('alicia', '1234', 'admin');");

            // Insertar productos de ejemplo si la tabla está vacía
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM productos");
            if (rs.next() && rs.getInt(1) == 0) {
                String insertEjemplos = "INSERT INTO productos (nombre, stock, precio, descripcion, imagen, fechaRegistro, modelo, proveedor, categoria, contacto_proveedor, telefono_proveedor) VALUES\n"
                        + "('Batería', 10, 89.99, 'Batería de 12V para auto', 'bateria.jpg', date('now'), 'YTX12', 'Bosch', 'Eléctrica', 'Carlos López', '555-1234'),\n"
                        + "('Aceite de motor', 20, 25.50, 'Aceite sintético 5W-30', 'aceite.jpg', date('now'), '5W30', 'Castrol', 'Lubricantes', 'María Gómez', '555-5678'),\n"
                        + "('Filtro de aire', 15, 12.75, 'Filtro de aire para motor', 'filtro.jpg', date('now'), 'CA-123', 'Mann', 'Filtros', 'Juan Pérez', '555-9012'),\n"
                        + "('Pastillas de freno', 8, 45.00, 'Juego de pastillas cerámicas', 'pastillas.jpg', date('now'), 'PD-456', 'Brembo', 'Frenos', 'Ana Martínez', '555-3456');";
                stmt.execute(insertEjemplos);
            }
*/
            System.out.println("Base de datos inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static {
        inicializarBaseDatos();
    }
}