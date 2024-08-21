#include <bits/stdc++.h>
using namespace std;

class PriorityQueue {
    vector<pair<int, int>> heap;
    unordered_map<int, int> pos;

public:
    bool isEmpty() const {
        return heap.empty();
    }

    void addOrUpdate(int node, int weight) {
        if (pos.find(node) == pos.end()) {
            heap.push_back({weight, node});
            int i = heap.size() - 1;
            pos[node] = i;
            heapifyUp(i);
        } else if (weight < heap[pos[node]].first) {
            decreaseKey(node, weight);
        }
    }

    pair<int, int> poll() {
        pair<int, int> root = heap.front();
        pos.erase(root.second);

        heap[0] = heap.back();
        heap.pop_back();

        if (!heap.empty()) {
            pos[heap[0].second] = 0;
            heapifyDown(0);
        }

        return root;
    }

private:
    void decreaseKey(int node, int newWeight) {
        int i = pos[node];
        heap[i].first = newWeight;
        heapifyUp(i);
    }

    void heapifyUp(int i) {
        while (i != 0 && heap[parent(i)].first > heap[i].first) {
            swapNodes(i, parent(i));
            i = parent(i);
        }
    }

    void heapifyDown(int i) {
        int smallest = i;
        int leftChild = left(i);
        int rightChild = right(i);

        if (leftChild < heap.size() && heap[leftChild].first < heap[smallest].first) {
            smallest = leftChild;
        }

        if (rightChild < heap.size() && heap[rightChild].first < heap[smallest].first) {
            smallest = rightChild;
        }

        if (smallest != i) {
            swapNodes(i, smallest);
            heapifyDown(smallest);
        }
    }

    int parent(int i) const { return (i - 1) / 2; }
    int left(int i) const { return 2 * i + 1; }
    int right(int i) const { return 2 * i + 2; }

    void swapNodes(int i, int j) {
        swap(heap[i], heap[j]);
        pos[heap[i].second] = i;
        pos[heap[j].second] = j;
    }
};

class Graph {
public:
    int vertices;
    vector<vector<pair<int, int>>> adjacencyList;

    Graph(int V) : vertices(V) {
        adjacencyList.resize(V);
    }

    void addEdge(int src, int dest, int weight) {
        adjacencyList[src].push_back({dest, weight});
        adjacencyList[dest].push_back({src, weight});
    }
};

class PrimUsingPQ {
public:
    int findMSTWeight(Graph& graph) {
        PriorityQueue minHeap;
        vector<bool> visited(graph.vertices, false);
        vector<int> key(graph.vertices, INT_MAX);
        vector<int> parent(graph.vertices, -1);

        int mstWeightSum = 0;
        int iteration = 1;

        key[0] = 0;
        minHeap.addOrUpdate(0, 0);

        while (!minHeap.isEmpty()) {
            pair<int, int> current = minHeap.poll();
            int currentWeight = current.first;
            int currentNode = current.second;

            if (visited[currentNode]) continue;

            visited[currentNode] = true;
            mstWeightSum += currentWeight;

            if (parent[currentNode] != -1) {
                cout << "Iteration " << iteration << ":" << endl;
                iteration++;

                cout << "  Current Node: " << currentNode << endl;
                cout << "  Weight of edge added to MST: " << currentWeight << endl;

                cout << "  Visited Array: ";
                for (int i = 0; i < visited.size(); i++) {
                    cout << visited[i] << " ";
                }
                cout << endl;

                cout << "  Current MST Edges: ";
                for (int i = 1; i < graph.vertices; i++) {
                    if (parent[i] != -1) {
                        cout << "(" << parent[i] << " - " << i << ") ";
                    }
                }
                cout << endl;
            }

            for (int i = 0; i < graph.adjacencyList[currentNode].size(); i++) {
                int adjNode = graph.adjacencyList[currentNode][i].first;
                int weight = graph.adjacencyList[currentNode][i].second;

                if (!visited[adjNode] && weight < key[adjNode]) {
                    key[adjNode] = weight;
                    minHeap.addOrUpdate(adjNode, weight);
                    parent[adjNode] = currentNode;
                }
            }

            cout << "  MinHeap State: ";
            PriorityQueue tempHeap = minHeap;
            while (!tempHeap.isEmpty()) {
                pair<int, int> temp = tempHeap.poll();
                cout << "(" << temp.first << ", " << temp.second << ") ";
            }
            cout << endl << endl;
        }

        cout << "Final Edges in the Minimum Spanning Tree:" << endl;
        for (int i = 1; i < graph.vertices; i++) {
            cout << parent[i] << " - " << i << endl;
        }

        return mstWeightSum;
    }
};

int main() {
    int V;
    cout << "Enter the number of vertices: ";
    cin >> V;

    Graph graph(V);

    int E;
    cout << "Enter the number of edges: ";
    cin >> E;

    for (int i = 0; i < E; i++) {
        int src, dest, weight;
        cout << "Enter source node, destination node, and weight: ";
        cin >> src >> dest >> weight;
        graph.addEdge(src, dest, weight);
    }

    PrimUsingPQ primAlgorithm;
    int mstWeight = primAlgorithm.findMSTWeight(graph);
    cout << "The sum of all the edge weights in the Minimum Spanning Tree: " << mstWeight << endl;

    return 0;
}
