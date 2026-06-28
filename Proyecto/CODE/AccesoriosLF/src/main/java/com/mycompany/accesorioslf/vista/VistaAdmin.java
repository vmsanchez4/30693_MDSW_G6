package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.*;
import com.mycompany.accesorioslf.modelo.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class VistaAdmin extends JFrame {
    private String usuario;
    private ControladorProducto controladorProducto;
    private ControladorVenta controladorVenta;
    private ControladorUsuario controladorUsuario;
    private ControladorPedidoCliente controladorPedidoCliente;
    private ControladorSolicitudProveedor controladorSolicitudProveedor;

    // Tabla de productos con su modelo y filtro
    private JTable tablaProductos;
    private ProductoTableModel modeloTablaProductos;
    private TableRowSorter<ProductoTableModel> sorterProductos;
    private JTextField txtBuscarProducto;

    // Tablas para historial de ventas y otros
    private JTable tablaPedidosCliente;
    private DefaultTableModel modeloTablaPedidosCliente;
    private JTable tablaSolicitudesProveedor;
    private DefaultTableModel modeloTablaSolicitudesProveedor;
    private Set<Integer> productosSolicitados;

    // Para alertas de stock
    private DefaultTableModel modelAlertas;

    // Componentes del panel de ventas
    private JTable tablaHistorialVentas;
    private DefaultTableModel modelHistorialVentas;
    private JLabel lblTotalVentas, lblMontoTotal, lblPromedioDiario;

    public VistaAdmin(String usuario) {
        this.usuario = usuario;
        controladorProducto = new ControladorProducto();
        controladorVenta = new ControladorVenta();
        controladorUsuario = new ControladorUsuario();
        controladorPedidoCliente = new ControladorPedidoCliente();
        controladorSolicitudProveedor = new ControladorSolicitudProveedor();

        inicializarUI();
        cargarProductos();
        cargarPedidosCliente();
        cargarSolicitudesProveedor();
        cargarHistorialVentas();
        actualizarEstadisticas();
        actualizarAlertasStock();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Bienvenido, " + usuario + "!",
                    "Acceso concedido",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void inicializarUI() {
        setTitle("Administración - AccesoriosLF - Usuario: " + usuario);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Productos", crearPanelProductos());
        tabbedPane.addTab("Registrar Venta", crearPanelVenta());
        tabbedPane.addTab("Reporte Alta Rotación", crearPanelReporte());
        tabbedPane.addTab("Pedidos de Clientes", crearPanelPedidosCliente());
        tabbedPane.addTab("Solicitudes a Proveedores", crearPanelSolicitudesProveedor());
        tabbedPane.addTab("Alertas Stock Bajo", crearPanelAlertasStock());
        tabbedPane.addTab("Usuarios", crearPanelUsuarios());

        JPanel panelVolver = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVolver = new JButton("Volver al catálogo público");
        btnVolver.addActionListener(e -> volverCatalogo());
        panelVolver.add(btnVolver);
        add(panelVolver, BorderLayout.SOUTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    // ================== PRODUCTOS (con búsqueda por código y nombre) ==================
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar por código o nombre:"));
        txtBuscarProducto = new JTextField(20);
        txtBuscarProducto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarProductos(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarProductos(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarProductos(); }
        });
        panelBusqueda.add(txtBuscarProducto);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        modeloTablaProductos = new ProductoTableModel();
        tablaProductos = new JTable(modeloTablaProductos);
        sorterProductos = new TableRowSorter<>(modeloTablaProductos);
        tablaProductos.setRowSorter(sorterProductos);
        tablaProductos.setRowHeight(30);
        tablaProductos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // Obtener el producto real a través del modelo (con el filtro aplicado)
                int modelRow = tablaProductos.convertRowIndexToModel(row);
                Producto p = modeloTablaProductos.getProductoEnFila(modelRow);
                if (p.getStock() < 5) {
                    c.setBackground(new Color(255, 200, 200));
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(tablaProductos);
        panel.add(scroll, BorderLayout.CENTER);

        // Botones de acción
        JPanel botones = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("Agregar producto");
        JButton btnEditar = new JButton("Editar producto");
        JButton btnEliminar = new JButton("Eliminar producto");
        JButton btnVerProveedor = new JButton("Ver Proveedor");
        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnVerProveedor.addActionListener(e -> verProveedor());
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnVerProveedor);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private void filtrarProductos() {
        String texto = txtBuscarProducto.getText().trim();
        if (texto.isEmpty()) {
            sorterProductos.setRowFilter(null);
        } else {
            // Buscar en columna 0 (Código/ID) y columna 1 (Nombre)
            sorterProductos.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0, 1));
        }
    }

    private void cargarProductos() {
        productosSolicitados = controladorSolicitudProveedor.getProductosSolicitadosIds();
        modeloTablaProductos.setIdsSolicitados(productosSolicitados);
        modeloTablaProductos.setProductos(controladorProducto.getProductos());
        filtrarProductos(); // reaplica el filtro
        actualizarAlertasStock();
    }

    private void agregarProducto() {
        DialogoProducto dialogo = new DialogoProducto(this, null);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            try {
                controladorProducto.agregarProducto(dialogo.getProducto());
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto agregado con éxito.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tablaProductos.convertRowIndexToModel(filaSeleccionada);
        Producto original = modeloTablaProductos.getProductoEnFila(modelRow);
        DialogoProducto dialogo = new DialogoProducto(this, original);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            Producto actualizado = dialogo.getProducto();
            actualizado.setId(original.getId());
            actualizado.setFechaRegistro(original.getFechaRegistro());
            try {
                controladorProducto.actualizarProducto(actualizado);
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto actualizado con éxito.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tablaProductos.convertRowIndexToModel(filaSeleccionada);
        Producto p = modeloTablaProductos.getProductoEnFila(modelRow);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar " + p.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controladorProducto.eliminarProducto(p.getId());
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verProveedor() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para ver el proveedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tablaProductos.convertRowIndexToModel(filaSeleccionada);
        Producto p = modeloTablaProductos.getProductoEnFila(modelRow);
        new DialogoVerProveedor(this, p).setVisible(true);
    }

    // ================== PANEL DE VENTAS (con historial y estadísticas) ==================
    private JPanel crearPanelVenta() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Panel superior: estadísticas
        JPanel panelStats = new JPanel(new GridLayout(1, 3, 10, 5));
        panelStats.setBorder(BorderFactory.createTitledBorder("Estadísticas (últimos 30 días)"));
        lblTotalVentas = new JLabel("Total ventas: 0");
        lblMontoTotal = new JLabel("Monto total: $0.00");
        lblPromedioDiario = new JLabel("Promedio diario: $0.00");
        panelStats.add(lblTotalVentas);
        panelStats.add(lblMontoTotal);
        panelStats.add(lblPromedioDiario);
        panel.add(panelStats, BorderLayout.NORTH);

        // Tabla de historial
        modelHistorialVentas = new DefaultTableModel(new String[]{"ID", "Cliente", "Fecha", "Total"}, 0);
        tablaHistorialVentas = new JTable(modelHistorialVentas);
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorialVentas);
        scrollHistorial.setBorder(BorderFactory.createTitledBorder("Historial de ventas"));
        panel.add(scrollHistorial, BorderLayout.CENTER);

        // Botón Nueva Venta y actualizar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevaVenta = new JButton("Nueva Venta");
        JButton btnRefrescarVentas = new JButton("Refrescar historial");
        btnNuevaVenta.addActionListener(e -> new DialogoVenta(this, controladorProducto, controladorVenta).setVisible(true));
        btnRefrescarVentas.addActionListener(e -> {
            cargarHistorialVentas();
            actualizarEstadisticas();
        });
        panelBotones.add(btnNuevaVenta);
        panelBotones.add(btnRefrescarVentas);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarHistorialVentas() {
        modelHistorialVentas.setRowCount(0);
        for (Pedido p : controladorVenta.getHistorialVentas()) {
            modelHistorialVentas.addRow(new Object[]{
                p.getId(),
                p.getClienteNombre(),
                p.getFecha().toString(),
                String.format("%.2f", p.getTotal())
            });
        }
    }

    private void actualizarEstadisticas() {
        Object[] stats = controladorVenta.getEstadisticasVentas();
        lblTotalVentas.setText("Total ventas: " + stats[0]);
        lblMontoTotal.setText("Monto total: $" + String.format("%.2f", (double) stats[1]));
        lblPromedioDiario.setText("Promedio diario: $" + String.format("%.2f", (double) stats[2]));
    }

    // ================== RESTO DE PESTAÑAS (sin cambios) ==================
    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel modeloReporte = new DefaultTableModel(new String[]{"ID", "Producto", "Vendidos (30d)", "Stock Actual", "Velocidad (día)"}, 0);
        JTable tablaReporte = new JTable(modeloReporte);
        JScrollPane scroll = new JScrollPane(tablaReporte);
        panel.add(scroll, BorderLayout.CENTER);
        JButton btnActualizar = new JButton("Actualizar Reporte");
        btnActualizar.addActionListener(e -> {
            modeloReporte.setRowCount(0);
            for (Object[] fila : controladorVenta.getReporteAltaRotacion()) {
                modeloReporte.addRow(fila);
            }
        });
        panel.add(btnActualizar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPedidosCliente() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloTablaPedidosCliente = new DefaultTableModel(new String[]{"ID", "Cliente", "Teléfono", "Fecha", "Productos"}, 0);
        tablaPedidosCliente = new JTable(modeloTablaPedidosCliente);
        JScrollPane scroll = new JScrollPane(tablaPedidosCliente);
        panel.add(scroll, BorderLayout.CENTER);
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarPedidosCliente());
        panel.add(btnRefrescar, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarPedidosCliente() {
        modeloTablaPedidosCliente.setRowCount(0);
        for (PedidoCliente p : controladorPedidoCliente.getPedidosCliente()) {
            StringBuilder productos = new StringBuilder();
            for (ItemPedidoCliente item : p.getItems()) {
                productos.append(item.getProducto().getNombre()).append(" x").append(item.getCantidad()).append(", ");
            }
            String prodStr = productos.length() > 0 ? productos.substring(0, productos.length() - 2) : "Ninguno";
            modeloTablaPedidosCliente.addRow(new Object[]{
                p.getId(), p.getNombreCliente(), p.getTelefono(), p.getFecha().toString(), prodStr
            });
        }
    }

    private JPanel crearPanelSolicitudesProveedor() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloTablaSolicitudesProveedor = new DefaultTableModel(new String[]{"ID", "Fecha", "Estado", "Productos"}, 0);
        tablaSolicitudesProveedor = new JTable(modeloTablaSolicitudesProveedor);
        JScrollPane scroll = new JScrollPane(tablaSolicitudesProveedor);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout());
        JButton btnNueva = new JButton("Nueva Solicitud");
        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        JButton btnRefrescar = new JButton("Refrescar");

        btnNueva.addActionListener(e -> {
            try {
                nuevaSolicitudProveedor();
                cargarProductos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCambiarEstado.addActionListener(e -> {
            try {
                cambiarEstadoSolicitud();
                cargarProductos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnRefrescar.addActionListener(e -> {
            cargarSolicitudesProveedor();
            cargarProductos();
        });

        botones.add(btnNueva);
        botones.add(btnCambiarEstado);
        botones.add(btnRefrescar);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarSolicitudesProveedor() {
        modeloTablaSolicitudesProveedor.setRowCount(0);
        for (SolicitudProveedor s : controladorSolicitudProveedor.getSolicitudesProveedor()) {
            StringBuilder productos = new StringBuilder();
            for (ItemSolicitudProveedor item : s.getItems()) {
                productos.append(item.getProducto().getNombre()).append(" x").append(item.getCantidad()).append(", ");
            }
            String prodStr = productos.length() > 0 ? productos.substring(0, productos.length() - 2) : "Ninguno";
            modeloTablaSolicitudesProveedor.addRow(new Object[]{
                s.getId(), s.getFecha().toString(), s.getEstado(), prodStr
            });
        }
    }

    private void nuevaSolicitudProveedor() throws SQLException {
        new DialogoSolicitudProveedor(this, controladorSolicitudProveedor, controladorProducto).setVisible(true);
        cargarSolicitudesProveedor();
        cargarProductos();
    }

    private void cambiarEstadoSolicitud() throws SQLException {
        int fila = tablaSolicitudesProveedor.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTablaSolicitudesProveedor.getValueAt(fila, 0);
        String estadoActual = (String) modeloTablaSolicitudesProveedor.getValueAt(fila, 2);
        String[] opciones = {"pendiente", "en_proceso", "completado"};
        String nuevo = (String) JOptionPane.showInputDialog(this, "Seleccione el nuevo estado:", "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE, null, opciones, estadoActual);
        if (nuevo != null && !nuevo.equals(estadoActual)) {
            controladorSolicitudProveedor.actualizarEstadoSolicitud(id, nuevo);
            cargarSolicitudesProveedor();
            cargarProductos();
            JOptionPane.showMessageDialog(this, "Estado actualizado con éxito.");
        }
    }

    private JPanel crearPanelAlertasStock() {
        JPanel panel = new JPanel(new BorderLayout());
        modelAlertas = new DefaultTableModel(new String[]{"ID", "Producto", "Stock", "Proveedor", "Contacto", "Teléfono"}, 0);
        JTable tablaAlertas = new JTable(modelAlertas);
        JScrollPane scroll = new JScrollPane(tablaAlertas);
        panel.add(scroll, BorderLayout.CENTER);
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarAlertasStock());
        panel.add(btnActualizar, BorderLayout.SOUTH);
        actualizarAlertasStock();
        return panel;
    }

    private void actualizarAlertasStock() {
        if (modelAlertas == null) return;
        modelAlertas.setRowCount(0);
        for (Producto p : controladorProducto.getProductos()) {
            if (p.getStock() < 5) {
                modelAlertas.addRow(new Object[]{p.getId(), p.getNombre(), p.getStock(), p.getProveedor(), p.getContactoProveedor(), p.getTelefonoProveedor()});
            }
        }
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel modelUsuarios = new DefaultTableModel(new String[]{"Usuario", "Rol"}, 0);
        JTable tablaUsuarios = new JTable(modelUsuarios);
        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        panel.add(scroll, BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout());
        JButton btnHacerAdmin = new JButton("Cambiar rol a administrador");
        JButton btnHacerCliente = new JButton("Cambiar rol a cliente");
        btnHacerAdmin.addActionListener(e -> cambiarRol(tablaUsuarios, modelUsuarios, "admin"));
        btnHacerCliente.addActionListener(e -> cambiarRol(tablaUsuarios, modelUsuarios, "cliente"));
        botones.add(btnHacerAdmin);
        botones.add(btnHacerCliente);
        panel.add(botones, BorderLayout.SOUTH);
        cargarUsuarios(modelUsuarios);
        return panel;
    }

    private void cargarUsuarios(DefaultTableModel model) {
        model.setRowCount(0);
        for (String[] u : controladorUsuario.obtenerUsuarios()) {
            model.addRow(u);
        }
    }

    private void cambiarRol(JTable tabla, DefaultTableModel model, String nuevoRol) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String usuario = (String) model.getValueAt(fila, 0);
        if ("alicia".equals(usuario)) {
            JOptionPane.showMessageDialog(this, "No se puede cambiar el rol del administrador principal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        controladorUsuario.cambiarRol(usuario, nuevoRol);
        cargarUsuarios(model);
        JOptionPane.showMessageDialog(this, "Rol actualizado con éxito.");
    }

    private void volverCatalogo() {
        this.dispose();
        new VistaCatalogoPublico().setVisible(true);
    }

    // ---------- TableModel para productos ----------
    private class ProductoTableModel extends AbstractTableModel {
        private List<Producto> productos;
        private Set<Integer> idsSolicitados;
        // Cambio: columna 0 ahora se llama "Código"
        private final String[] columnas = {"Código", "Nombre", "Stock", "Precio", "Descripción", "Imagen", "FechaRegistro", "Modelo", "Proveedor", "Categoría", "Contacto", "Teléfono", "Solicitado"};

        public void setProductos(List<Producto> productos) {
            this.productos = productos;
            fireTableDataChanged();
        }

        public void setIdsSolicitados(Set<Integer> idsSolicitados) {
            this.idsSolicitados = idsSolicitados;
            fireTableDataChanged();
        }

        public Producto getProductoEnFila(int row) {
            return productos.get(row);
        }

        @Override public int getRowCount() { return productos == null ? 0 : productos.size(); }
        @Override public int getColumnCount() { return columnas.length; }
        @Override public String getColumnName(int col) { return columnas[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Producto p = productos.get(row);
            switch (col) {
                case 0: return p.getId(); // Mostramos el ID como código
                case 1: return p.getNombre();
                case 2: return p.getStock();
                case 3: return String.format("%.2f", p.getPrecio());
                case 4: return p.getDescripcion();
                case 5: return p.getImagen();
                case 6: return p.getFechaRegistro();
                case 7: return p.getModelo();
                case 8: return p.getProveedor();
                case 9: return p.getCategoria();
                case 10: return p.getContactoProveedor();
                case 11: return p.getTelefonoProveedor();
                case 12:
                    if (idsSolicitados != null && idsSolicitados.contains(p.getId())) {
                        return "Sí";
                    } else {
                        return "No";
                    }
                default: return "";
            }
        }
    }
}