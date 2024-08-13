import java.util.*;

class Node {
    int value;
    int dist;

    Node(int value, int dist) {
        this.value = value;
        this.dist = dist;
    }
}

public class DijkstraUsingSet {

    private static int[] dijkstra(int V, ArrayList<ArrayList<ArrayList<Integer>>> adj, int S) {
        TreeSet<Node> set = new TreeSet<>((node1, node2) -> {
            if (node1.value != node2.value && node1.dist == node2.dist) {
                return 1;
            }
            return node1.dist - node2.dist;
        });
        // adding source node
        set.add(new Node(S, 0));
        int[] dist = new int[V];

        // Initialize distances to infinity
        for (int i = 0; i < V; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[S] = 0;
        int c = 1;
        while (!set.isEmpty()) {
            Node node = set.pollFirst();
            System.out
                    .print(c + "" + (c % 10 == 1 ? "st" : (c % 10 == 2 ? "nd" : (c % 10 == 3 ? "rd" : "th")))
                            + " Distances: ");
            c++;
            displayDistances(dist); // Display distances at each step

            for (ArrayList<Integer> adjNodes : adj.get(node.value)) {
                int adjNode = adjNodes.get(0);
                int adjNodeDist = adjNodes.get(1);

                if (dist[node.value] + adjNodeDist < dist[adjNode]) {
                    set.add(new Node(adjNode, dist[node.value] + adjNodeDist));
                    dist[adjNode] = dist[node.value] + adjNodeDist;
                }
            }
        }
        return dist;
    }

    private static void displayDistances(int[] dist) {

        for (int i : dist) {
            System.out.print((i == Integer.MAX_VALUE ? "INF" : i) + " ");
        }
        System.out.println();
    }

    // Main function
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of vertices: ");
        int V = sc.nextInt();

        ArrayList<ArrayList<ArrayList<Integer>>> adj = new ArrayList<>();

        // Initialize adjacency list
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        System.out.print("Enter the number of edges: ");
        int E = sc.nextInt();

        // Input graph edges
        for (int i = 0; i < E; i++) {
            System.out.print("Enter source node, destination node, and weight ");
            int src = sc.nextInt();
            int dest = sc.nextInt();
            int wgt = sc.nextInt();

            ArrayList<Integer> edge = new ArrayList<>();
            edge.add(dest);
            edge.add(wgt);
            adj.get(src).add(edge);

            // Add edge from dest to src (for undirected graph)
            // ArrayList<Integer> edge2 = new ArrayList<>();
            // edge2.add(src);
            // edge2.add(wgt);
            // adj.get(dest).add(edge2);
        }

        System.out.print("Enter the source vertex: ");
        int S = sc.nextInt();

        // Run Dijkstra's algorithm
        int[] distances = dijkstra(V, adj, S);

        // Display the final distances
        System.out.print("Final Distances from source: ");
        displayDistances(distances);
        sc.close();
    }
}
