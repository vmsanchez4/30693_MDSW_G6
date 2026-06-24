package com.mycompany.accesorioslf.vista;

import com.mycompany.accesorioslf.controlador.ControladorUsuario;
import javax.swing.*;
import java.awt.*;

public class DialogoInicioSesion extends JDialog {
    private boolean autenticado = false;
    private String usuarioAutenticado;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private ControladorUsuario controladorUsuario;
    private static int intentosFallidos = 0;
    private static long tiempoBloqueo = 0;
    private static final int MAX_INTENTOS = 3;
    private static final long TIEMPO_BLOQUEO_MS = 30000;

    public DialogoInicioSesion(JFrame parent) {
        super(parent, "Acceso Administrador", true);
        controladorUsuario = new ControladorUsuario();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        add(txtPassword, gbc);

        JButton btnOk = new JButton("Iniciar sesión");
        JButton btnCancel = new JButton("Cancelar");
        btnOk.addActionListener(e -> autenticar());
        btnCancel.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnOk);
        panelBotones.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(panelBotones, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    private void autenticar() {
        if (System.currentTimeMillis() < tiempoBloqueo) {
            long segundosRestantes = (tiempoBloqueo - System.currentTimeMillis()) / 1000;
            JOptionPane.showMessageDialog(this,
                    "Demasiados intentos fallidos. Espere " + segundosRestantes + " segundos.",
                    "Bloqueado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (controladorUsuario.autenticarAdmin(usuario, password)) {
            intentosFallidos = 0;
            usuarioAutenticado = usuario;
            autenticado = true;
            dispose();
        } else {
            intentosFallidos++;
            if (intentosFallidos >= MAX_INTENTOS) {
                tiempoBloqueo = System.currentTimeMillis() + TIEMPO_BLOQUEO_MS;
                JOptionPane.showMessageDialog(this,
                        "Has superado el número de intentos. Acceso bloqueado por 30 segundos.",
                        "Cuenta bloqueada", JOptionPane.ERROR_MESSAGE);
                intentosFallidos = 0;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Credenciales incorrectas. Intentos restantes: " + (MAX_INTENTOS - intentosFallidos),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            txtUsuario.setText("");
            txtPassword.setText("");
        }
    }

    public boolean isAutenticado() { return autenticado; }
    public String getUsuarioAutenticado() { return usuarioAutenticado; }
}