package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorProducto;
import com.mycompany.accesorioslf.controlador.ControladorSolicitud;
import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class DialogoSeleccionProductos extends JDialog {
    private ControladorSolicitud controladorSolicitud;
    private ControladorProducto controladorProducto;
    private int solicitudId;
    private JTable tabla;
    private DefaultTableModel model;
    private List<Producto> productos;
    private List<Integer> cantidades;

    public DialogoSeleccionProductos(JFrame parent, int solicitudId) {
        super(parent, "Seleccionar productos solicitados", true);
        this.solicitudId = solicitudId;
        controladorSolicitud = new ControladorSolicitud();
        controladorProducto = new ControladorProducto();
        this.productos = controladorProducto.getProductos();
        this.cantidades = new ArrayList<>();
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(600, 400);
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID", "Producto", "Stock", "Cantidad solicitada"}, 0);
        tabla = new JTable(model);
        for (Producto p : productos) {
            model.addRow(new Object[]{p.getId(), p.getNombre(), p.getStock(), 0});
            cantidades.add(0);
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
        boolean alguna = false;
        for (int i = 0; i < tabla.getRowCount(); i++) {
            int cantidad = (int) model.getValueAt(i, 3);
            if (cantidad > 0) {
                int productoId = (int) model.getValueAt(i, 0);
                controladorSolicitud.agregarProductoSolicitado(solicitudId, productoId, cantidad);
                alguna = true;
            }
        }
        if (alguna) {
            JOptionPane.showMessageDialog(this, "Solicitud guardada con los productos seleccionados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se seleccionó ningún producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
        dispose();
    }
}