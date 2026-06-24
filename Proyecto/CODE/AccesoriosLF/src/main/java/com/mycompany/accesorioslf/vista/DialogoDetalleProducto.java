package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import java.awt.*;

public class DialogoDetalleProducto extends JDialog {
    public DialogoDetalleProducto(JFrame parent, Producto producto) {
        super(parent, "Ficha técnica - " + producto.getNombre(), true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        
        agregarCampo(gbc, "ID:", String.valueOf(producto.getId()), 0);
        agregarCampo(gbc, "Nombre:", producto.getNombre(), 1);
        agregarCampo(gbc, "Precio:", "$" + String.format("%.2f", producto.getPrecio()), 2);
        agregarCampo(gbc, "Stock:", String.valueOf(producto.getStock()), 3);
        agregarCampo(gbc, "Descripción:", producto.getDescripcion(), 4);
        agregarCampo(gbc, "Imagen:", producto.getImagen(), 5);
        agregarCampo(gbc, "Modelo:", producto.getModelo(), 7);
        agregarCampo(gbc, "Categoría:", producto.getCategoria(), 8);
        

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnCerrar, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    private void agregarCampo(GridBagConstraints gbc, String etiqueta, String valor, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        JTextField campo = new JTextField(valor, 20);
        campo.setEditable(false);
        add(campo, gbc);
    }
}