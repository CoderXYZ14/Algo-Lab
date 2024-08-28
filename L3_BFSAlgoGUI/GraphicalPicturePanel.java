import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.BasicStroke;

public class GraphicalPicturePanel extends JPanel {
    private Vertex firstVertex = null;
    private Vertex secondVertex = null; // Add this to track the second vertex

    private Graph graph;

    public Graph getGraph() {
        return graph;
    }

    public GraphicalPicturePanel() {
        setLayout(null);

        graph = new Graph(this); // Pass the panel to the Graph constructor

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int id = ButtonListener.idOfButton();

                if (id == 1) { // Add Vertex Mode
                    if (!graph.isVertex(x, y)) {
                        Vertex v = new Vertex(x, y, Color.BLUE);
                        graph.addVertex(v);
                        repaint();
                    }
                } else if (id == 2) { // Add Edge Mode
                    Vertex v = graph.getVertex(x, y);
                    if (v != null) {
                        if (firstVertex == null) {
                            firstVertex = v; // Set first vertex
                        } else {
                            graph.addEdge(firstVertex, v);
                            firstVertex = null; // Reset for next edge
                            repaint();
                        }
                    }
                } else if (id == 3) { // Remove Vertex Mode
                    Vertex v = graph.getVertex(x, y);
                    if (v != null) {
                        graph.removeVertex(v);
                        repaint();
                    }
                } else if (id == 4) { // Remove Edge Mode
                    Vertex v = graph.getVertex(x, y);
                    if (v != null) {
                        if (firstVertex == null) {
                            firstVertex = v; // Set the first vertex
                        } else {
                            secondVertex = v; // Set the second vertex
                            // Check and remove the edge between the two vertices
                            if (graph.removeEdge(firstVertex, secondVertex)) {
                                System.out.println("Edge removed between " + firstVertex.getLabel() + " and "
                                        + secondVertex.getLabel());
                            } else {
                                System.out.println("No edge exists between " + firstVertex.getLabel() + " and "
                                        + secondVertex.getLabel());
                            }
                            firstVertex = null;
                            secondVertex = null;
                            repaint(); // Redraw the panel after removing the edge
                        }
                    }
                } else if (id == 5) { // BFS Traversal Mode
                    Vertex v = graph.getVertex(x, y);
                    if (v != null) {
                        new Thread(() -> graph.performBFS(v)).start(); // BFS can take time so to make the file
                                                                       // responseiv
                    }
                }
            }
        });
    }

    public void clearGraph() {
        graph.getVertices().clear();
        graph.getEdges().clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
    }

    private void drawGraph(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw edges with their respective colors
        for (Edge e : graph.getEdges()) {
            g2d.setColor(e.getColor());
            g2d.setStroke(new BasicStroke(2)); // Set edge width
            int x1 = e.getFirst().getX();
            int y1 = e.getFirst().getY();
            int x2 = e.getSecond().getX();
            int y2 = e.getSecond().getY();
            g2d.drawLine(x1, y1, x2, y2);
            g2d.drawString(e.getLabel(), (x1 + x2) / 2, (y1 + y2) / 2);// to set position of label
        }

        // Draw vertices
        for (Vertex v : graph.getVertices()) {
            g2d.setColor(v.getColor());
            g2d.fillOval(v.getX() - 15, v.getY() - 15, 30, 30); // Increase vertex radius
            g2d.setColor(Color.BLACK); // Set color for labels
            g2d.drawString(v.getLabel(), v.getX() - 10, v.getY() - 20);
        }
    }
}
