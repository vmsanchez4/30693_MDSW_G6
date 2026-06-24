package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorPedidoCliente;
import com.mycompany.accesorioslf.controlador.ControladorProducto;
import com.mycompany.accesorioslf.modelo.ItemPedidoCliente;
import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogoPedidoCliente extends JDialog {
    private ControladorProducto controladorProducto;
    private ControladorPedidoCliente controladorPedidoCliente;
    private JComboBox<Producto> cbProducto;
    private JTextField txtCantidad;
    private JTextField txtNombre, txtTelefono;
    private List<ItemPedidoCliente> items;
    private JTable tabla;
    private ItemsTableModel modeloTabla;

    private static final String TEXTO_PERMITIDO = "^[a-zA-ZáéíóúñÑÁÉÍÓÚüÜ\\s\\.\\,\\-\\'\\(\\)0-9]+$";

    public DialogoPedidoCliente(JFrame parent) {
        super(parent, "Hacer pedido personalizado", true);
        controladorProducto = new ControladorProducto();
        controladorPedidoCliente = new ControladorPedidoCliente();
        items = new ArrayList<>();
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(600, 500);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Datos  cliente
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nombre (*):"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(15);
        add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Teléfono (*):"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        add(txtTelefono, gbc);

        // Selección de producto
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        cbProducto = new JComboBox<>(controladorProducto.getProductos().toArray(new Producto[0]));
        add(cbProducto, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(5);
        add(txtCantidad, gbc);

        JButton btnAgregar = new JButton("Agregar al pedido");
        btnAgregar.addActionListener(e -> agregarItem());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnAgregar, gbc);
        gbc.gridwidth = 1;

        // Tabla de items
        modeloTabla = new ItemsTableModel();
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        add(scroll, gbc);
        gbc.weightx = 0; gbc.weighty = 0;

        // Botones 
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnGuardar = new JButton("Guardar pedido");
        JButton btnCancelar = new JButton("Cancelar");
        btnEliminar.addActionListener(e -> eliminarItem());
        btnGuardar.addActionListener(e -> guardarPedido());
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(panelBotones, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void agregarItem() {
        Producto p = (Producto) cbProducto.getSelectedItem();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        try {
            String cantStr = txtCantidad.getText().trim();
            if (cantStr.isEmpty()) throw new NumberFormatException();
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida (mayor a 0).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        items.add(new ItemPedidoCliente(p, cantidad));
        modeloTabla.fireTableDataChanged();
        txtCantidad.setText("");
        JOptionPane.showMessageDialog(this, "Producto agregado al pedido.", "Agregado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarItem() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            items.remove(fila);
            modeloTabla.fireTableDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void guardarPedido() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validar nombre
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!nombre.matches(TEXTO_PERMITIDO)) {
            JOptionPane.showMessageDialog(this, "El nombre contiene caracteres no permitidos. Solo se permiten letras, números, espacios, puntos, comas, guiones, apóstrofes y paréntesis.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar teléfono
        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El teléfono es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!telefono.matches("[0-9\\-\\+\\s\\(\\)]+")) {
            JOptionPane.showMessageDialog(this, "El teléfono solo puede contener números, espacios, guiones, paréntesis y el signo +.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = controladorPedidoCliente.guardarPedidoCliente(nombre, telefono, items);
        if (id != -1) {
            JOptionPane.showMessageDialog(this, "Pedido guardado con éxito. Número: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //  TableModel  items 
    private class ItemsTableModel extends AbstractTableModel {
        private final String[] columnas = {"Producto", "Cantidad"};

        @Override public int getRowCount() { return items.size(); }
        @Override public int getColumnCount() { return 2; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        @Override public Object getValueAt(int row, int col) {
            ItemPedidoCliente item = items.get(row);
            if (col == 0) return item.getProducto().getNombre();
            else return item.getCantidad();
        }
    }
}