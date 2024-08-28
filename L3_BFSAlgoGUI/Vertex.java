import java.awt.Color;

public class Vertex {
    private int x, y;
    private Color color;
    private boolean visited;
    private String label; // Add label field

    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = Color.BLUE;
        this.label = ""; // Initialize label
    }

    public Vertex(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.visited = false;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
