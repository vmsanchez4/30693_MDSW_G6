package com.mycompany.accesorioslf.controlador;

import java.util.List;

public interface IUsuarioControlador {
    String autenticar(String usuario, String password);
    String obtenerRol(String usuario);
    List<String[]> obtenerUsuarios();
    void cambiarRol(String usuario, String nuevoRol);
}