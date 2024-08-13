import java.util.ArrayList;
import java.util.Scanner;

class Node {
    int value;
    int dist;

    Node(int value, int dist) {
        this.value = value;
        this.dist = dist;
    }
}

class MinHeap {
    private ArrayList<Node> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    public void add(Node node) {
        heap.add(node);
        int currentIndex = heap.size() - 1;
        heapifyUp(currentIndex);
    }

    public Node poll() {
        if (heap.isEmpty()) {
            return null;
        }
        Node root = heap.get(0);
        Node lastNode = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, lastNode);
            heapifyDown(0);
        }
        return root;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).dist >= heap.get(parentIndex).dist) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    private void heapifyDown(int index) {
        int smallest = index;
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        if (leftChild < heap.size() && heap.get(leftChild).dist < heap.get(smallest).dist) {
            smallest = leftChild;
        }

        if (rightChild < heap.size() && heap.get(rightChild).dist < heap.get(smallest).dist) {
            smallest = rightChild;
        }

        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    private void swap(int i, int j) {
        Node temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}

public class DijkstraUsingPQ {

    private static int[] dijkstra(int V, ArrayList<ArrayList<ArrayList<Integer>>> adj, int S) {
        MinHeap pq = new MinHeap();
        pq.add(new Node(S, 0));
        int[] dist = new int[V];

        for (int i = 0; i < V; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[S] = 0;
        int c = 1;

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            System.out.print(c + "" + (c % 10 == 1 ? "st" : (c % 10 == 2 ? "nd" : (c % 10 == 3 ? "rd" : "th")))
                    + " Distances: ");
            c++;
            displayDistances(dist); // Display distances at each step

            for (ArrayList<Integer> adjNodes : adj.get(node.value)) {
                int adjNode = adjNodes.get(0);
                int adjNodeDist = adjNodes.get(1);

                if (dist[node.value] + adjNodeDist < dist[adjNode]) {
                    dist[adjNode] = dist[node.value] + adjNodeDist;
                    pq.add(new Node(adjNode, dist[adjNode]));
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
