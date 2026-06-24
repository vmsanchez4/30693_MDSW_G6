package estudiantecrud.controlador;

import estudiantecrud.modelo.Estudiante;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteController {
    private List<Estudiante> estudiantes;
    private static final String ARCHIVO = "estudiantes.txt";

    public EstudianteController() {
        estudiantes = new ArrayList<>();
        cargarDesdeArchivo();
    }

    private boolean esIdValido(String id) {
        return id != null && id.matches("\\d{4}");
    }

    private void cargarDesdeArchivo() {
        File file = new File(ARCHIVO);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al crear archivo: " + e.getMessage());
            }
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 3) {
                    String id = partes[0];
                    String nombre = partes[1];
                    int edad = Integer.parseInt(partes[2]);
                    estudiantes.add(new Estudiante(id, nombre, edad));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer archivo: " + e.getMessage());
        }
    }

    private void guardarEnArchivo() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Estudiante e : estudiantes) {
                bw.write(e.getId() + "|" + e.getNombre() + "|" + e.getEdad());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }

    public List<Estudiante> obtenerTodos() {
        return new ArrayList<>(estudiantes);
    }

    
    public String agregarEstudiante(String id, String nombre, int edad) {
        if (!esIdValido(id)) {
            return "ERROR: El ID debe ser exactamente 4 dígitos numéricos.";
        }
        if (buscarPorId(id) != null) {
            return "ERROR: Ya existe un estudiante con ese ID.";
        }
        estudiantes.add(new Estudiante(id, nombre, edad));
        guardarEnArchivo();
        return null; 
    }

    public String actualizarEstudiante(String id, String nombre, int edad) {
        if (!esIdValido(id)) {
            return "ERROR: El ID debe ser exactamente 4 dígitos numéricos.";
        }
        Estudiante e = buscarPorId(id);
        if (e == null) {
            return "ERROR: No se encontró un estudiante con ese ID.";
        }
        e.setNombre(nombre);
        e.setEdad(edad);
        guardarEnArchivo();
        return null;
    }

    public String eliminarEstudiante(String id) {
        if (!esIdValido(id)) {
            return "ERROR: El ID debe ser exactamente 4 dígitos numéricos.";
        }
        Estudiante e = buscarPorId(id);
        if (e == null) {
            return "ERROR: No se encontró un estudiante con ese ID.";
        }
        estudiantes.remove(e);
        guardarEnArchivo();
        return null;
    }

    private Estudiante buscarPorId(String id) {
        for (Estudiante e : estudiantes) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }
}