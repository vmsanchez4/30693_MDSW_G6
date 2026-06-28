package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorProducto;
import com.mycompany.accesorioslf.controlador.ControladorSolicitud;
import com.mycompany.accesorioslf.modelo.Producto;
import com.mycompany.accesorioslf.modelo.ProductoSolicitado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DialogoSeleccionProductos extends JDialog {
    private ControladorSolicitud controladorSolicitud;
    private ControladorProducto controladorProducto;
    private int solicitudId;
    private JTable tabla;
    private DefaultTableModel model;

    public DialogoSeleccionProductos(JFrame parent, int solicitudId) {
        super(parent, "Seleccionar productos solicitados", true);
        this.solicitudId = solicitudId;
        controladorSolicitud = new ControladorSolicitud();
        controladorProducto = new ControladorProducto();
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(600, 400);
        setLayout(new BorderLayout());
        // Cambio: columna 0 ahora se llama "Código"
        model = new DefaultTableModel(new String[]{"Código", "Producto", "Stock", "Cantidad solicitada"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // solo la columna cantidad es editable
            }
        };
        tabla = new JTable(model);
        tabla.setDefaultEditor(Object.class, new SpinnerCellEditor());
        tabla.setDefaultRenderer(Object.class, new SpinnerCellRenderer());

        for (Producto p : controladorProducto.getProductos()) {
            model.addRow(new Object[]{p.getId(), p.getNombre(), p.getStock(), 0});
        }

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar solicitud");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardarSolicitud());
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        add(botones, BorderLayout.SOUTH);

        setLocationRelativeTo(getParent());
    }

    private void guardarSolicitud() {
        List<ProductoSolicitado> seleccionados = new ArrayList<>();
        for (int i = 0; i < tabla.getRowCount(); i++) {
            Object value = model.getValueAt(i, 3);
            int cantidad = 0;
            if (value instanceof Integer) {
                cantidad = (int) value;
            } else if (value instanceof String) {
                try {
                    cantidad = Integer.parseInt((String) value);
                } catch (NumberFormatException ignored) {}
            }
            if (cantidad > 0) {
                int productoId = (int) model.getValueAt(i, 0);
                seleccionados.add(new ProductoSolicitado(0, solicitudId, productoId, cantidad, "solicitado"));
            }
        }

        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se seleccionó ningún producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            for (ProductoSolicitado ps : seleccionados) {
                controladorSolicitud.agregarProductoSolicitado(solicitudId, ps.getProductoId(), ps.getCantidad());
            }
            JOptionPane.showMessageDialog(this, "Solicitud guardada con los productos seleccionados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========== EDITOR DE CELDA CON JSPINNER ==========
    private class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner;
        private Object currentValue;

        public SpinnerCellEditor() {
            spinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
            spinner.addChangeListener(e -> {
                currentValue = spinner.getValue();
                fireEditingStopped();
            });
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            spinner.setValue(value != null ? value : 0);
            currentValue = spinner.getValue();
            return spinner;
        }
    }

    // ========== RENDERIZADOR DE CELDA CON JSPINNER ==========
    private class SpinnerCellRenderer implements TableCellRenderer {
        private JSpinner spinner;

        public SpinnerCellRenderer() {
            spinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
            spinner.setBorder(null);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            spinner.setValue(value != null ? value : 0);
            if (isSelected) {
                spinner.setBackground(table.getSelectionBackground());
            } else {
                spinner.setBackground(table.getBackground());
            }
            return spinner;
        }
    }
}