import javax.swing.SwingUtilities;

public class Main {
    private static GUI gui;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui = new GUI();
            gui.setVisible(true);
        });
    }

    public static GUI getGUI() {
        return gui;
    }
}
