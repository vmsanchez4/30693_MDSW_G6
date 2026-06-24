package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import java.awt.*;

public class DialogoVerProveedor extends JDialog {
    public DialogoVerProveedor(JFrame parent, Producto producto) {
        super(parent, "Información del proveedor", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addInfo(gbc, "Producto:", producto.getNombre(), 0);
        addInfo(gbc, "Proveedor:", producto.getProveedor(), 1);
        addInfo(gbc, "Contacto:", producto.getContactoProveedor(), 2);
        addInfo(gbc, "Teléfono:", producto.getTelefonoProveedor(), 3);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnCerrar, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    private void addInfo(GridBagConstraints gbc, String label, String value, int y) {
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(value != null ? value : "", 20);
        field.setEditable(false);
        add(field, gbc);
    }
}