import java.awt.Color;

public class Edge {
    private Vertex first;
    private Vertex second;
    private Color color;
    private String label;

    public Edge(Vertex first, Vertex second) {
        this.first = first;
        this.second = second;
        this.label = "";
        this.color = Color.BLACK; // Default color
    }

    public Vertex getFirst() {
        return first;
    }

    public Vertex getSecond() {
        return second;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean connects(Vertex v1, Vertex v2) {
        return (first.equals(v1) && second.equals(v2)) || (first.equals(v2) && second.equals(v1));
    }

    public boolean contains(Vertex v) {
        return first.equals(v) || second.equals(v);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // @Override
    // public boolean equals(Object obj) {
    // if (this == obj)
    // return true;
    // if (obj == null || getClass() != obj.getClass())
    // return false;
    // Edge edge = (Edge) obj;
    // return first.equals(edge.first) && second.equals(edge.second);
    // }

    // @Override
    // public int hashCode() {
    // return 31 * first.hashCode() + second.hashCode();
    // }
}
