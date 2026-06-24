package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorProducto;
import com.mycompany.accesorioslf.controlador.ControladorSolicitudProveedor;
import com.mycompany.accesorioslf.modelo.ItemSolicitudProveedor;
import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogoSolicitudProveedor extends JDialog {
    private ControladorSolicitudProveedor controladorSolicitud;
    private ControladorProducto controladorProducto;
    private JComboBox<Producto> cbProducto;
    private JTextField txtCantidad;
    private List<ItemSolicitudProveedor> items;
    private JTable tabla;
    private ItemsTableModel modeloTabla;

    public DialogoSolicitudProveedor(JFrame parent, ControladorSolicitudProveedor controladorSolicitud, ControladorProducto controladorProducto) {
        super(parent, "Nueva solicitud a proveedor", true);
        this.controladorSolicitud = controladorSolicitud;
        this.controladorProducto = controladorProducto;
        items = new ArrayList<>();
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(500, 400);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        cbProducto = new JComboBox<>(controladorProducto.getProductos().toArray(new Producto[0]));
        add(cbProducto, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(5);
        add(txtCantidad, gbc);

        JButton btnAgregar = new JButton("Agregar producto");
        btnAgregar.addActionListener(e -> agregarItem());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnAgregar, gbc);
        gbc.gridwidth = 1;

        // Tabla de items
        modeloTabla = new ItemsTableModel();
        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        add(scroll, gbc);
        gbc.weightx = 0; gbc.weighty = 0;

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnGuardar = new JButton("Guardar solicitud");
        JButton btnCancelar = new JButton("Cancelar");
        btnEliminar.addActionListener(e -> eliminarItem());
        btnGuardar.addActionListener(e -> guardarSolicitud());
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
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
            if (cantStr.isEmpty()) {
                throw new NumberFormatException();
            }
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida (mayor a 0).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        items.add(new ItemSolicitudProveedor(p, cantidad));
        modeloTabla.fireTableDataChanged();
        txtCantidad.setText("");
        JOptionPane.showMessageDialog(this, "Producto agregado a la solicitud.", "Agregado", JOptionPane.INFORMATION_MESSAGE);
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

    private void guardarSolicitud() {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int id = controladorSolicitud.crearSolicitudProveedor(items);
        if (id != -1) {
            JOptionPane.showMessageDialog(this, "Solicitud creada con éxito. Número: " + id, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- TableModel para items ----------
    private class ItemsTableModel extends AbstractTableModel {
        private final String[] columnas = {"Producto", "Cantidad"};

        @Override public int getRowCount() { return items.size(); }
        @Override public int getColumnCount() { return 2; }
        @Override public String getColumnName(int col) { return columnas[col]; }
        @Override public Object getValueAt(int row, int col) {
            ItemSolicitudProveedor item = items.get(row);
            if (col == 0) return item.getProducto().getNombre();
            else return item.getCantidad();
        }
    }
}