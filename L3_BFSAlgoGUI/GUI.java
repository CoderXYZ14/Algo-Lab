import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {
    private GraphicalPicturePanel canvas;

    public GUI() {
        setTitle("Graph GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        canvas = new GraphicalPicturePanel();
        add(canvas);// add canvas to the content pane of the JFrame

        JPanel buttonPanel = new JPanel();
        addButtons(buttonPanel);

        add(buttonPanel, "South");
    }

    public void addButtons(JPanel panel) {
        String[] buttons = { "Add Vertex", "Add Edges", "Remove Vertex", "Remove Edge", "BFS Traversal", "Undo" };
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(new ButtonListener());
            panel.add(button);
        }
    }

    public GraphicalPicturePanel getCanvas() {
        return canvas;
    }
}
