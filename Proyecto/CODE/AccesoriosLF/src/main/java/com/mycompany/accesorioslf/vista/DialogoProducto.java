package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DialogoProducto extends JDialog {
    private boolean guardado = false;
    private Producto producto;
    private JTextField txtNombre, txtStock, txtPrecio, txtDescripcion, txtImagen, txtModelo, txtProveedor, txtCategoria, txtContactoProveedor, txtTelefonoProveedor;

    // Expresión regular para caracteres permitidos en texto
    private static final String TEXTO_PERMITIDO = "^[a-zA-ZáéíóúñÑÁÉÍÓÚüÜ\\s\\.\\,\\-\\'\\(\\)0-9]+$";

    public DialogoProducto(JFrame parent, Producto productoExistente) {
        super(parent, productoExistente == null ? "Agregar producto" : "Editar producto", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(20);
        txtStock = new JTextField(10);
        txtPrecio = new JTextField(10);
        txtDescripcion = new JTextField(20);
        txtImagen = new JTextField(15);
        txtModelo = new JTextField(15);
        txtProveedor = new JTextField(15);
        txtCategoria = new JTextField(15);
        txtContactoProveedor = new JTextField(15);
        txtTelefonoProveedor = new JTextField(15);

        if (productoExistente != null) {
            txtNombre.setText(productoExistente.getNombre());
            txtStock.setText(String.valueOf(productoExistente.getStock()));
            txtPrecio.setText(String.format("%.2f", productoExistente.getPrecio()));
            txtDescripcion.setText(productoExistente.getDescripcion());
            txtImagen.setText(productoExistente.getImagen());
            txtModelo.setText(productoExistente.getModelo());
            txtProveedor.setText(productoExistente.getProveedor());
            txtCategoria.setText(productoExistente.getCategoria());
            txtContactoProveedor.setText(productoExistente.getContactoProveedor());
            txtTelefonoProveedor.setText(productoExistente.getTelefonoProveedor());
        }

        agregarFila(gbc, "Nombre (*):", txtNombre, 0);
        agregarFila(gbc, "Stock (*):", txtStock, 1);
        agregarFila(gbc, "Precio (*):", txtPrecio, 2);
        agregarFila(gbc, "Descripción (*):", txtDescripcion, 3);
        agregarFila(gbc, "Imagen (archivo):", txtImagen, 4);
        agregarFila(gbc, "Modelo (*):", txtModelo, 5);
        agregarFila(gbc, "Proveedor (*):", txtProveedor, 6);
        agregarFila(gbc, "Categoría (*):", txtCategoria, 7);
        agregarFila(gbc, "Contacto proveedor (*):", txtContactoProveedor, 8);
        agregarFila(gbc, "Teléfono proveedor (*):", txtTelefonoProveedor, 9);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        add(panelBotones, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    private void agregarFila(GridBagConstraints gbc, String etiqueta, JComponent campo, int y) {
        gbc.gridx = 0; gbc.gridy = y; add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; add(campo, gbc);
    }

    // Método de validación de texto
    private boolean validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio.");
        }
        if (!texto.matches(TEXTO_PERMITIDO)) {
            throw new IllegalArgumentException("El campo '" + campo + "' contiene caracteres no permitidos. Solo se permiten letras, números, espacios, puntos, comas, guiones, apóstrofes y paréntesis.");
        }
        return true;
    }

    private void guardar() {
        try {
            // Validar campos de texto (todos excepto imagen y teléfono)
            String nombre = txtNombre.getText().trim();
            validarTexto(nombre, "Nombre");

            String descripcion = txtDescripcion.getText().trim();
            validarTexto(descripcion, "Descripción");

            String modelo = txtModelo.getText().trim();
            validarTexto(modelo, "Modelo");

            String proveedor = txtProveedor.getText().trim();
            validarTexto(proveedor, "Proveedor");

            String categoria = txtCategoria.getText().trim();
            validarTexto(categoria, "Categoría");

            String contacto = txtContactoProveedor.getText().trim();
            validarTexto(contacto, "Contacto proveedor");

            // Teléfono (validación especial)
            String telefono = txtTelefonoProveedor.getText().trim();
            if (telefono.isEmpty()) {
                throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
            }
            if (!telefono.matches("[0-9\\-\\+\\s\\(\\)]+")) {
                throw new IllegalArgumentException("El teléfono solo puede contener números, espacios, guiones, paréntesis y el signo +.");
            }

            // Validar stock y precio (numéricos)
            String stockStr = txtStock.getText().trim();
            if (stockStr.isEmpty()) throw new IllegalArgumentException("El stock es obligatorio.");
            int stock = Integer.parseInt(stockStr);
            if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");

            String precioStr = txtPrecio.getText().trim();
            if (precioStr.isEmpty()) throw new IllegalArgumentException("El precio es obligatorio.");
            double precio = Double.parseDouble(precioStr.replace(',', '.'));
            if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");

            // Imagen (opcional)
            String imagen = txtImagen.getText().trim();

            producto = new Producto(0, nombre, stock, precio, descripcion, imagen, LocalDate.now().toString(),
                    modelo, proveedor, categoria, contacto, telefono);
            guardado = true;

            JOptionPane.showMessageDialog(this,
                    "Producto guardado con éxito.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stock y precio deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() { return guardado; }
    public Producto getProducto() { return producto; }
}