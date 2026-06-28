package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorSolicitud;
import com.mycompany.accesorioslf.modelo.ProductoSolicitado;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DialogoSolicitud extends JDialog {
    private ControladorSolicitud controladorSolicitud;
    private JTextField txtNombre, txtTelefono;

    public DialogoSolicitud(JFrame parent) {
        super(parent, "Solicitar producto", true);
        controladorSolicitud = new ControladorSolicitud();
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(400, 200);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; txtNombre = new JTextField(15); add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; txtTelefono = new JTextField(15); add(txtTelefono, gbc);

        JButton btnSiguiente = new JButton("Siguiente (seleccionar productos)");
        btnSiguiente.addActionListener(e -> siguiente());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnSiguiente, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void siguiente() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y teléfono son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Usar el método unificado para guardar solicitud y luego abrir selección
        // Pero como necesitamos el ID, primero guardamos la solicitud sin productos y luego abrimos selección.
        try {
            // Guardamos la solicitud sin productos (descripción vacía)
            int idSolicitud = controladorSolicitud.guardarSolicitudCompleta(nombre, telefono, "", null);
            if (idSolicitud != -1) {
                new DialogoSeleccionProductos((JFrame) getParent(), idSolicitud).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}