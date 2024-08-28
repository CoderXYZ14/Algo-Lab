import java.awt.Color;
import java.util.*;

public class Graph {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private GraphicalPicturePanel panel; // Reference to the panel

    public Graph(GraphicalPicturePanel panel) {
        this.panel = panel;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public Vertex getVertex(int x, int y) {
        for (Vertex v : vertices) {
            if (Math.abs(v.getX() - x) <= 10 && Math.abs(v.getY() - y) <= 10) {
                return v;
            }
        }
        return null; // No matching vertex found
    }

    public void addVertex(Vertex v) {
        vertices.add(v);
        updateVertexLabels();
    }

    public void removeVertex(Vertex v) {
        vertices.remove(v);
        updateVertexLabels();
        removeAssociatedEdges(v);
    }

    public void addEdge(Vertex x, Vertex y) {
        Edge e = new Edge(x, y);
        edges.add(e);
        updateEdgeLabels();
    }

    public boolean removeEdge(Vertex v1, Vertex v2) {
        Edge edgeToRemove = null;
        for (Edge edge : edges) {
            if (edge.connects(v1, v2)) {
                edgeToRemove = edge;
                break;
            }
        }
        if (edgeToRemove != null) {
            edges.remove(edgeToRemove);
            return true; // Edge was found and removed
        }
        return false; // No edge was found between v1 and v2
    }

    private void updateVertexLabels() {
        char label = 'A';
        for (Vertex v : vertices) {
            v.setLabel(String.valueOf(label));
            label++;
        }
    }

    private void updateEdgeLabels() {
        char label = 'a';
        for (Edge e : edges) {
            e.setLabel(String.valueOf(label));
            label++;
        }
    }

    private void removeAssociatedEdges(Vertex v) {
        edges.removeIf(e -> e.getFirst() == v || e.getSecond() == v);
        updateEdgeLabels();
    }

    public boolean isVertex(int x, int y) {// check if ur not making vertex on a exisitng node
        int radius = 10;
        for (Vertex vertex : vertices) {
            int dx = vertex.getX() - x;
            int dy = vertex.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) <= 2 * radius) {
                return true;
            }
        }
        return false;
    }

    // Method to perform BFS with animation
    public void performBFS(Vertex start) {
        List<Vertex> bfsTraversal = new ArrayList<>();
        boolean[] visited = new boolean[vertices.size()];
        Queue<Vertex> queue = new LinkedList<>();

        // Map each vertex to its index for visited array tracking
        Map<Vertex, Integer> vertexIndexMap = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndexMap.put(vertices.get(i), i);
        }

        // Start BFS with the initial vertex
        queue.add(start);
        visited[vertexIndexMap.get(start)] = true;
        start.setColor(Color.YELLOW);
        panel.repaint();

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            bfsTraversal.add(current);

            // Delay to visualize the BFS traversal
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Explore all adjacent vertices
            for (Vertex neighbor : getAdjacentVertices(current)) {
                int neighborIndex = vertexIndexMap.get(neighbor);
                if (!visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    queue.add(neighbor);
                    neighbor.setColor(Color.YELLOW);
                    // Change the edge color to yellow
                    Edge connectingEdge = getConnectingEdge(current, neighbor);
                    if (connectingEdge != null) {
                        connectingEdge.setColor(Color.YELLOW);
                    }
                    panel.repaint();
                }
            }
        }
    }

    private List<Vertex> getAdjacentVertices(Vertex vertex) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getFirst().equals(vertex)) {
                neighbors.add(edge.getSecond());
            } else if (edge.getSecond().equals(vertex)) {
                neighbors.add(edge.getFirst());
            }
        }
        return neighbors;
    }

    private Edge getConnectingEdge(Vertex v1, Vertex v2) {
        for (Edge edge : edges) {
            if (edge.connects(v1, v2)) {
                return edge;
            }
        }
        return null;
    }

}
