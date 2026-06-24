package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.*;
import com.mycompany.accesorioslf.modelo.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class VistaAdmin extends JFrame {
    private String usuario;
    private ControladorProducto controladorProducto;
    private ControladorVenta controladorVenta;
    private ControladorUsuario controladorUsuario;
    private ControladorPedidoCliente controladorPedidoCliente;
    private ControladorSolicitudProveedor controladorSolicitudProveedor;

    private JTable tablaProductos;
    private ProductoTableModel modeloTablaProductos;
    private JTable tablaPedidosCliente;
    private DefaultTableModel modeloTablaPedidosCliente;
    private JTable tablaSolicitudesProveedor;
    private DefaultTableModel modeloTablaSolicitudesProveedor;
    private Set<Integer> productosSolicitados;

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

    //  PRODUCTOS 
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloTablaProductos = new ProductoTableModel();
        tablaProductos = new JTable(modeloTablaProductos);
        tablaProductos.setRowHeight(30);
        tablaProductos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Producto p = modeloTablaProductos.getProductoEnFila(row);
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

    private void cargarProductos() {
        productosSolicitados = controladorSolicitudProveedor.getProductosSolicitadosIds();
        modeloTablaProductos.setIdsSolicitados(productosSolicitados);
        modeloTablaProductos.setProductos(controladorProducto.getProductos());
    }

    private void agregarProducto() {
        DialogoProducto dialogo = new DialogoProducto(this, null);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            controladorProducto.agregarProducto(dialogo.getProducto());
            cargarProductos();
            actualizarAlertasStock();
        }
    }

    private void editarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Producto original = modeloTablaProductos.getProductoEnFila(fila);
        DialogoProducto dialogo = new DialogoProducto(this, original);
        dialogo.setVisible(true);
        if (dialogo.isGuardado()) {
            Producto actualizado = dialogo.getProducto();
            actualizado.setId(original.getId());
            actualizado.setFechaRegistro(original.getFechaRegistro());
            controladorProducto.actualizarProducto(actualizado);
            cargarProductos();
            actualizarAlertasStock();
        }
    }

    private void eliminarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Producto p = modeloTablaProductos.getProductoEnFila(fila);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar " + p.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controladorProducto.eliminarProducto(p.getId());
            cargarProductos();
            actualizarAlertasStock();
            
            
            JOptionPane.showMessageDialog(this,
                    "Producto eliminado con éxito.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void verProveedor() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para ver el proveedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Producto p = modeloTablaProductos.getProductoEnFila(fila);
        new DialogoVerProveedor(this, p).setVisible(true);
    }

    //  VENTA 
    private JPanel crearPanelVenta() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnVenta = new JButton("Nueva Venta");
        btnVenta.addActionListener(e -> new DialogoVenta(this, controladorProducto, controladorVenta).setVisible(true));
        panel.add(btnVenta, BorderLayout.CENTER);
        return panel;
    }

    //  REPORTE 
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

    //  PEDIDOS DE CLIENTES 
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

    //  SOLICITUDES A PROVEEDORES 
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
            nuevaSolicitudProveedor();
            cargarProductos();
        });
        btnCambiarEstado.addActionListener(e -> {
            cambiarEstadoSolicitud();
            cargarProductos();
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

    private void nuevaSolicitudProveedor() {
        new DialogoSolicitudProveedor(this, controladorSolicitudProveedor, controladorProducto).setVisible(true);
        cargarSolicitudesProveedor();
        cargarProductos();
    }

    private void cambiarEstadoSolicitud() {
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
            // Mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "Estado de la solicitud actualizado con éxito.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //  ALERTAS STOCK BAJO 
    private JPanel crearPanelAlertasStock() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel modelAlertas = new DefaultTableModel(new String[]{"ID", "Producto", "Stock", "Proveedor", "Contacto", "Teléfono"}, 0);
        JTable tablaAlertas = new JTable(modelAlertas);
        JScrollPane scroll = new JScrollPane(tablaAlertas);
        panel.add(scroll, BorderLayout.CENTER);
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarAlertasStock(modelAlertas));
        panel.add(btnActualizar, BorderLayout.SOUTH);
        actualizarAlertasStock(modelAlertas);
        return panel;
    }

    private void actualizarAlertasStock() { /* no hace nada */ }
    private void actualizarAlertasStock(DefaultTableModel model) {
        model.setRowCount(0);
        for (Producto p : controladorProducto.getProductos()) {
            if (p.getStock() < 5) {
                model.addRow(new Object[]{p.getId(), p.getNombre(), p.getStock(), p.getProveedor(), p.getContactoProveedor(), p.getTelefonoProveedor()});
            }
        }
    }

    //  USUARIOS 
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
        
        JOptionPane.showMessageDialog(this,
                "Rol actualizado con éxito.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void volverCatalogo() {
        this.dispose();
        new VistaCatalogoPublico().setVisible(true);
    }

    //  TableModel para productos 
    private class ProductoTableModel extends AbstractTableModel {
        private List<Producto> productos;
        private Set<Integer> idsSolicitados;
        private final String[] columnas = {"ID", "Nombre", "Stock", "Precio", "Descripción", "Imagen", "FechaRegistro", "Modelo", "Proveedor", "Categoría", "Contacto", "Teléfono", "Solicitado"};

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

        @Override
        public int getRowCount() {
            return productos == null ? 0 : productos.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnas[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            Producto p = productos.get(row);
            switch (col) {
                case 0: return p.getId();
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