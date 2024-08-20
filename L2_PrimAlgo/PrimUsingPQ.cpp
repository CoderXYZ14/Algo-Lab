#include <bits/stdc++.h>
using namespace std;

class PriorityQueue {
    vector<pair<int, int>> heap;
    unordered_map<int, int> pos;

public:
    bool isEmpty() const {
        return heap.empty();
    }

    void add(pair<int, int> p) {
        heap.push_back(p);
        int i = heap.size() - 1;
        pos[p.second] = i;
        heapifyUp(i);
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

    void decreaseKey(int node, int newWeight) {
        int i = pos[node];
        heap[i].first = newWeight;
        heapifyUp(i);
    }

private:
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
        vector<pair<int, int>> mstEdges;
        vector<int> parent(graph.vertices, -1);

        int mstWeightSum = 0;
        int iteration = 1;

        minHeap.add({0, 0});

        while (!minHeap.isEmpty()) {
            auto [currentWeight, currentNode] = minHeap.poll();

            if (visited[currentNode]) continue;

            visited[currentNode] = true;
            mstWeightSum += currentWeight;

            if (parent[currentNode] != -1) {
                mstEdges.push_back({parent[currentNode], currentNode});
            }

            cout << "Iteration " << iteration << ":" << endl;
            iteration++;

            cout << "  Current Node: " << currentNode << endl;
            cout << "  Weight of edge added to MST: " << currentWeight << endl;

            cout << "  Visited Array: ";
            for (bool v : visited) {
                cout << v << " ";
            }
            cout << endl;

            cout << "  Current MST Edges: ";
            for (const auto& [src, dest] : mstEdges) {
                cout << "(" << src << " - " << dest << ") ";
            }
            cout << endl;

            for (auto& edge : graph.adjacencyList[currentNode]) {
                if (!visited[edge.first]) {
                    minHeap.add({edge.second, edge.first});
                    parent[edge.first] = currentNode;
                }
            }

            cout << "  MinHeap State: ";
            PriorityQueue tempHeap = minHeap;
            while (!tempHeap.isEmpty()) {
                auto [weight, node] = tempHeap.poll();
                cout << "(" << weight << ", " << node << ") ";
            }
            cout << endl << endl;
        }

        cout << "Final Edges in the Minimum Spanning Tree:" << endl;
        for (const auto& [src, dest] : mstEdges) {
            cout << src << " - " << dest << endl;
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
