package com.mycompany.accesorioslf;

import com.mycompany.accesorioslf.vista.VistaCatalogoPublico;
import javax.swing.SwingUtilities;

public class AccesoriosLF {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaCatalogoPublico catalogo = new VistaCatalogoPublico();
            catalogo.setVisible(true);
        });
        
    }
}