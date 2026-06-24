package estudiantecrud;

import estudiantecrud.vista.VistaEstudiante;
import javax.swing.SwingUtilities;

public class EstudianteCRUD {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaEstudiante vista = new VistaEstudiante();
            vista.setVisible(true);
        });
    }
}
