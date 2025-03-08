import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AdjacencyListGraph implements Graph_interface {
    private int V;
    private List<List<Graph_interface.Edge>> adjList;

    public AdjacencyListGraph(int V) {
        this.V = V;
        adjList = new ArrayList<>(V);
        for (int i = 0; i < V; i++)
            adjList.add(new ArrayList<>());
    }

    public AdjacencyListGraph(int V, List<List<Graph_interface.Edge>> adjList){
        this.V = V;
        this.adjList = adjList;
    }

    private boolean check(List<Graph_interface.Edge> list, int v){
        for (Graph_interface.Edge edge : list) {
            if (edge.dest == v)
                return true;
        }
        return false;
    }

    @Override
    public void addEdge(int src, int dest, int weight) {
        adjList.get(src).add(new Graph_interface.Edge(dest, weight));
    }

    @Override
    public void removeEdge(int src, int dest) {
        adjList.get(src).removeIf(edge -> edge.dest == dest);
    }

    @Override
    public int getV() { return V; }

    @Override
    public int[][] getAdjMatrix() {
        int[][] matrix = new int[V][V];
        for (int i = 0; i < V; i++) {
            Arrays.fill(matrix[i], Integer.MAX_VALUE);
            matrix[i][i] = 0;
            for (Graph_interface.Edge edge : adjList.get(i)) {
                matrix[i][edge.dest] = edge.weight;
            }
        }
        return matrix;
    }

    @Override
    public List<List<Graph_interface.Edge>> getAdjList() { return adjList; }

    @Override
    public Graph_interface refresh(int v) {
        return new AdjacencyListGraph(v);
    }

    @Override
    public void generateRandomGraph(int V, int edgeCount) {
        if (edgeCount <= 0)
            return;

        adjList.clear();
        adjList = new ArrayList<>(V);
        for (int i = 0; i < V; i++)
            adjList.add(new ArrayList<>());
        Random rand = new Random();
        int edgesAdded = 0;
        while (edgesAdded < edgeCount) {
            int src = rand.nextInt(V);
            int dest = rand.nextInt(V);
            if (src != dest && !check(adjList.get(src),dest)) {
                // && !check(adjList.get(dest),src)
                int weight = 1 + rand.nextInt(10);
                addEdge(src, dest, weight);
                edgesAdded++;
            }
        }
    }

    @Override
    public Graph_interface changeStructure(Graph_interface g) {
        return new AdjacencyMatrixGraph(g.getV(), g.getAdjMatrix());
    }

    @Override
    public int checkE(int src, int dest) {
        if (check(adjList.get(src), dest))
            return 1;
        if (check(adjList.get(dest), src))
            return -1;
        else
            return 0;
    }
}
