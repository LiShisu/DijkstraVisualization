import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AdjacencyMatrixGraph implements Graph_interface {
    private int V;
    private int[][] adjMatrix;

    public AdjacencyMatrixGraph(int V) {
        this.V = V;
        adjMatrix = new int[V][V];
        for (int i = 0; i < V; i++) {
            Arrays.fill(adjMatrix[i], Integer.MAX_VALUE);
            adjMatrix[i][i] = 0;
        }
    }

    public AdjacencyMatrixGraph(int V, int[][] matrix){
        this.V = V;
        this.adjMatrix = matrix;
    }

    @Override
    public void addEdge(int src, int dest, int weight) {
        adjMatrix[src][dest] = weight;
    }

    @Override
    public void removeEdge(int src, int dest) {
        adjMatrix[src][dest] = Integer.MAX_VALUE;
    }

    @Override
    public int getV() { return V; }

    @Override
    public int[][] getAdjMatrix() { return adjMatrix; }

    @Override
    public List<List<Graph_interface.Edge>> getAdjList() {
        List<List<Graph_interface.Edge>> adjList = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            List<Graph_interface.Edge> edges = new ArrayList<>();
            for (int j = 0; j < V; j++) {
                if (adjMatrix[i][j] != Integer.MAX_VALUE && i != j) {
                    edges.add(new Graph_interface.Edge(j, adjMatrix[i][j]));
                }
            }
            adjList.add(edges);
        }
        return adjList;
    }

    @Override
    public Graph_interface refresh(int v) {
        return new AdjacencyMatrixGraph(v);
    }

    @Override
    public void generateRandomGraph(int V, int edgeCount) {
        if (edgeCount <= 0)
            return;

        for (int i = 0; i < V; i++) {
            Arrays.fill(adjMatrix[i], Integer.MAX_VALUE);
            adjMatrix[i][i] = 0;
        }
        Random rand = new Random();
        int edgesAdded = 0;
        while (edgesAdded < edgeCount) {
            int src = rand.nextInt(V);
            int dest = rand.nextInt(V);
            if (src != dest && getAdjMatrix()[src][dest] == Integer.MAX_VALUE) {
                // && getAdjMatrix()[dest][src] == Integer.MAX_VALUE
                int weight = 1 + rand.nextInt(10);
                addEdge(src, dest, weight);
                edgesAdded++;
            }
        }
    }

    @Override
    public Graph_interface changeStructure(Graph_interface g) {
        return new AdjacencyListGraph(g.getV(), g.getAdjList());
    }

    @Override
    public int checkE(int src, int dest) {
        if (adjMatrix[src][dest] != Integer.MAX_VALUE)
            return 1;
        if (adjMatrix[dest][src]  != Integer.MAX_VALUE)
            return -1;
        else
            return 0;
    }
}
