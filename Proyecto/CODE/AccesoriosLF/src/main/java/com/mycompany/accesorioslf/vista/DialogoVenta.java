package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorProducto;
import com.mycompany.accesorioslf.controlador.ControladorVenta;
import com.mycompany.accesorioslf.modelo.DetallePedido;
import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DialogoVenta extends JDialog {
    private ControladorProducto controladorProducto;
    private ControladorVenta controladorVenta;
    private JComboBox<Producto> cbProducto;
    private JTextField txtCantidad;
    private JTextField txtCliente;
    private List<DetallePedido> detalles;

    private static final String TEXTO_PERMITIDO = "^[a-zA-ZáéíóúñÑÁÉÍÓÚüÜ\\s\\.\\,\\-\\'\\(\\)0-9]+$";

    public DialogoVenta(JFrame parent, ControladorProducto controladorProducto, ControladorVenta controladorVenta) {
        super(parent, "Registrar Venta", true);
        this.controladorProducto = controladorProducto;
        this.controladorVenta = controladorVenta;
        detalles = new ArrayList<>();
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(450, 350);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nombre del cliente (*):"), gbc);
        gbc.gridx = 1;
        txtCliente = new JTextField(15);
        add(txtCliente, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        cbProducto = new JComboBox<>(controladorProducto.getProductos().toArray(new Producto[0]));
        add(cbProducto, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        txtCantidad = new JTextField(5);
        add(txtCantidad, gbc);

        JButton btnAgregar = new JButton("Agregar producto");
        btnAgregar.addActionListener(e -> agregarProducto());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnAgregar, gbc);

        JButton btnFinalizar = new JButton("Finalizar venta y generar recibo");
        btnFinalizar.addActionListener(e -> finalizarVenta());
        gbc.gridy = 4; add(btnFinalizar, gbc);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        gbc.gridy = 5; add(btnCancelar, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void agregarProducto() {
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

        if (cantidad > p.getStock()) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente. Disponible: " + p.getStock(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        detalles.add(new DetallePedido(0, 0, p.getId(), cantidad, p.getPrecio()));
        JOptionPane.showMessageDialog(this, "Producto agregado. Total items: " + detalles.size(), "Agregado", JOptionPane.INFORMATION_MESSAGE);
        txtCantidad.setText("");
    }

    private void finalizarVenta() {
        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!cliente.matches(TEXTO_PERMITIDO)) {
            JOptionPane.showMessageDialog(this, "El nombre contiene caracteres no permitidos. Solo se permiten letras, números, espacios, puntos, comas, guiones, apóstrofes y paréntesis.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (detalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int pedidoId = controladorVenta.registrarVenta(cliente, detalles);
        if (pedidoId != -1) {
            generarRecibo(pedidoId, cliente);
            JOptionPane.showMessageDialog(this, "Venta registrada exitosamente. Pedido #" + pedidoId, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarRecibo(int pedidoId, String cliente) {
        try (PrintWriter out = new PrintWriter(new FileWriter("recibo_" + pedidoId + ".txt"))) {
            out.println("========== RECIBO DE VENTA ==========");
            out.println("Número de pedido: " + pedidoId);
            out.println("Cliente: " + cliente);
            out.println("Fecha: " + java.time.LocalDate.now());
            out.println("------------------------------------");
            double total = 0;
            for (DetallePedido d : detalles) {
                Producto p = controladorProducto.obtenerProductoPorId(d.getProductoId());
                double subtotal = d.getCantidad() * d.getPrecioUnitario();
                out.printf("%s x%d = $%.2f%n", p.getNombre(), d.getCantidad(), subtotal);
                total += subtotal;
            }
            out.println("------------------------------------");
            out.printf("TOTAL: $%.2f%n", total);
            out.println("========== GRACIAS POR SU COMPRA ==========");
            JOptionPane.showMessageDialog(this, "Recibo generado: recibo_" + pedidoId + ".txt", "Recibo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}