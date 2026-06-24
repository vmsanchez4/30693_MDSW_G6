package estudiantecrud.vista;

import estudiantecrud.controlador.EstudianteController;
import estudiantecrud.modelo.Estudiante;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VistaEstudiante extends JFrame {
    private EstudianteController controlador;
    private JTextField txtId, txtNombre, txtEdad;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnMostrarTodo;
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;

    public VistaEstudiante() {
        controlador = new EstudianteController();
        initUI();
        cargarTabla();
    }

    private void initUI() {
        setTitle("CRUD de Estudiantes - MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

       
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        panelForm.add(new JLabel("ID (4 dígitos):"));
        txtId = new JTextField();
        panelForm.add(txtId);
        panelForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Edad:"));
        txtEdad = new JTextField();
        panelForm.add(txtEdad);
        add(panelForm, BorderLayout.NORTH);

       
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnMostrarTodo = new JButton("Mostrar Todo");
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnMostrarTodo);
        add(panelBotones, BorderLayout.CENTER);

        
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Edad"}, 0);
        tablaEstudiantes = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaEstudiantes);
        add(scrollPane, BorderLayout.SOUTH);

       
        btnAgregar.addActionListener(e -> agregarEstudiante());
        btnActualizar.addActionListener(e -> actualizarEstudiante());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
        btnMostrarTodo.addActionListener(e -> {
            cargarTabla();                
            limpiarFormulario();          
            JOptionPane.showMessageDialog(this, "Lista actualizada con todos los estudiantes.");
        });

        
        tablaEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaEstudiantes.getSelectedRow();
                if (row != -1) {
                    txtId.setText(modeloTabla.getValueAt(row, 0).toString());
                    txtNombre.setText(modeloTabla.getValueAt(row, 1).toString());
                    txtEdad.setText(modeloTabla.getValueAt(row, 2).toString());
                }
            }
        });
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Estudiante> estudiantes = controlador.obtenerTodos();
        for (Estudiante e : estudiantes) {
            modeloTabla.addRow(new Object[]{e.getId(), e.getNombre(), e.getEdad()});
        }
    }

    private void agregarEstudiante() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || edadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero.");
            return;
        }

        String resultado = controlador.agregarEstudiante(id, nombre, edad);
        if (resultado == null) { 
            cargarTabla();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Estudiante agregado correctamente.");
        } else {
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    private void actualizarEstudiante() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla o ingrese ID.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();
        if (nombre.isEmpty() || edadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y edad son obligatorios.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Edad inválida.");
            return;
        }

        String resultado = controlador.actualizarEstudiante(id, nombre, edad);
        if (resultado == null) {
            cargarTabla();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Estudiante actualizado.");
        } else {
            JOptionPane.showMessageDialog(this, resultado);
        }
    }

    private void eliminarEstudiante() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla o ingrese ID.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar estudiante con ID " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = controlador.eliminarEstudiante(id);
            if (resultado == null) {
                cargarTabla();
                limpiarFormulario();
                JOptionPane.showMessageDialog(this, "Estudiante eliminado.");
            } else {
                JOptionPane.showMessageDialog(this, resultado);
            }
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        tablaEstudiantes.clearSelection();
    }
}