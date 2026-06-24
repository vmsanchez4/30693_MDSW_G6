package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorProducto;
import com.mycompany.accesorioslf.modelo.Producto;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class VistaCatalogoPublico extends JFrame {
    private ControladorProducto controladorProducto;
    private JPanel panelTarjetas;
    private JTextField txtBuscar;
    private List<Producto> productosMostrados;

    public VistaCatalogoPublico() {
        controladorProducto = new ControladorProducto();
        inicializarUI();
        cargarTarjetas();
    }

    private void inicializarUI() {
        setTitle("AccesoriosLF - Catálogo de Autopartes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscar = new JTextField(20);
        JButton btnHacerPedido = new JButton("Hacer pedido personalizado");
        JButton btnAdmin = new JButton("Admin Login");

        panelSuperior.add(new JLabel("Buscar por nombre (tiempo real):"));
        panelSuperior.add(txtBuscar);
        panelSuperior.add(btnHacerPedido);
        panelSuperior.add(btnAdmin);
        add(panelSuperior, BorderLayout.NORTH);

        panelTarjetas = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scroll = new JScrollPane(panelTarjetas);
        add(scroll, BorderLayout.CENTER);

        // Búsqueda 
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cargarTarjetas(); }
            @Override public void removeUpdate(DocumentEvent e) { cargarTarjetas(); }
            @Override public void changedUpdate(DocumentEvent e) { cargarTarjetas(); }
        });

        btnHacerPedido.addActionListener(e -> new DialogoPedidoCliente(this).setVisible(true));
        btnAdmin.addActionListener(e -> abrirLogin());
    }

    private void cargarTarjetas() {
        panelTarjetas.removeAll();
        String busqueda = txtBuscar.getText();
        productosMostrados = controladorProducto.buscarPorNombre(busqueda);
        for (Producto p : productosMostrados) {
            panelTarjetas.add(crearTarjeta(p));
        }
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel crearTarjeta(Producto p) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tarjeta.setBackground(Color.WHITE);

        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icono = cargarImagen(p.getImagen(), 120, 120);
        lblImagen.setIcon(icono);
        tarjeta.add(lblImagen, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridBagLayout());
        info.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 2, 2, 2);

        gbc.gridy = 0;
        info.add(new JLabel("<html><b>" + p.getNombre() + "</b></html>"), gbc);
        gbc.gridy = 1;
        info.add(new JLabel("Precio: $" + String.format("%.2f", p.getPrecio())), gbc);
        gbc.gridy = 2;
        info.add(new JLabel("Descripción: " + p.getDescripcionBreve()), gbc);
        gbc.gridy = 3;
        JButton btnVerMas = new JButton("Ver más");
        btnVerMas.addActionListener(e -> new DialogoDetalleProducto(this, p).setVisible(true));
        info.add(btnVerMas, gbc);

        tarjeta.add(info, BorderLayout.CENTER);
        return tarjeta;
    }

    private ImageIcon cargarImagen(String nombreArchivo, int ancho, int alto) {
        ImageIcon icono = null;
        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            java.io.File imgFile = new java.io.File("imagenes/" + nombreArchivo);
            if (!imgFile.exists()) imgFile = new java.io.File(nombreArchivo);
            if (imgFile.exists()) {
                Image img = new ImageIcon(imgFile.getPath()).getImage();
                icono = new ImageIcon(img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
            }
        }
        if (icono == null) {
            icono = new ImageIcon(new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB));
            Graphics g = icono.getImage().getGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, ancho, alto);
            g.setColor(Color.BLACK);
            g.drawString("Sin imagen", ancho / 4, alto / 2);
            g.dispose();
        }
        return icono;
    }

    private void abrirLogin() {
        DialogoInicioSesion login = new DialogoInicioSesion(this);
        login.setVisible(true);
        if (login.isAutenticado()) {
            this.dispose();
            VistaAdmin admin = new VistaAdmin(login.getUsuarioAutenticado());
            admin.setVisible(true);
        }
    }
}