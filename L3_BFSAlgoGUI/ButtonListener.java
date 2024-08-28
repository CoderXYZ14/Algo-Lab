import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonListener implements ActionListener {
    private String buttonName;
    private static int id = 0;
    private Vertex firstSelectedVertex = null; // Store the first selected vertex

    @Override
    public void actionPerformed(ActionEvent e) {
        buttonName = e.getActionCommand();

        GUI gui = Main.getGUI();
        if (gui == null) {
            System.err.println("GUI instance is null.");
            return;
        }

        GraphicalPicturePanel canvas = gui.getCanvas();
        if (canvas == null) {
            System.err.println("Canvas is null.");
            return;
        }

        if (buttonName.equals("Add Vertex")) {
            id = 1;
            System.out.println("Vertex Mode activated.");
        } else if (buttonName.equals("Add Edges")) {
            id = 2;
            System.out.println("Edge Mode activated.");
        } else if (buttonName.equals("Remove Vertex")) {
            id = 3;
            System.out.println("Remove Vertex Mode activated.");
        } else if (buttonName.equals("Remove Edge")) {
            id = 4;
            firstSelectedVertex = null; // Reset on mode activation
            System.out.println("Remove Edge Mode activated.");
        } else if (buttonName.equals("BFS Traversal")) {
            id = 5;
            System.out.println("BFS Mode activated.");
        } else if (buttonName.equals("Undo")) {
            id = 6;
            // Clear the canvas by resetting the graph
            canvas.clearGraph();
        }
    }

    public void handleVertexClick(Vertex clickedVertex) {
        if (id == 4) { // Remove Edge mode
            if (firstSelectedVertex == null) {
                // First vertex selected
                firstSelectedVertex = clickedVertex;
                System.out.println("First vertex selected: " + firstSelectedVertex.getLabel());
            } else {
                // Second vertex selected
                System.out.println("Second vertex selected: " + clickedVertex.getLabel());
                removeEdgeBetweenVertices(firstSelectedVertex, clickedVertex);
                firstSelectedVertex = null; // Reset after edge deletion
            }
        }
    }

    private void removeEdgeBetweenVertices(Vertex v1, Vertex v2) {
        GUI gui = Main.getGUI();
        GraphicalPicturePanel canvas = gui.getCanvas();
        canvas.getGraph().removeEdge(v1, v2);
        canvas.repaint();
    }

    public static int idOfButton() {
        return id;
    }

    public static void setIdButton(int newID) {
        id = newID;
    }
}
