import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public enum StepType { NODE_SELECTED, DISTANCE_UPDATED , EDGE_SELECTED}
//每个Step对象包含相应的数据。例如，当类型是NODE_SELECTED时，保存节点u；当类型是DISTANCE_UPDATED时，保存u、v和新距离。

    public static class Step {
        StepType type;
        int u;
        int v;
        int newDist;
        int preNode;

        public Step(int u, StepType type) {
            this.type = type;
            this.u = u;
        }

        public Step(int u, int v, StepType type){
            this.type = type;
            this.u = u;
            this.v = v;
        }

        public Step(int u, int v, int newDist, int prev, StepType type) {
            this.type = type;
            this.u = u;
            this.v = v;
            this.newDist = newDist;
            this.preNode = prev;
        }

        // Getters
        public StepType getType() { return type; }
        public int getU() { return u; }
        public int getV() { return v; }
        public int getNewDistance() { return newDist; }
        public int getPreNode() { return preNode; }
    }

    private Graph_interface graph;
    private int[] dist;
    private boolean[] visited;
    private int[] prev;
    private int source;
    private List<Step> steps = new ArrayList<>();

    public Dijkstra(Graph_interface graph, int source) {
        this.graph = graph;
        this.source = source;
        int V = graph.getV();
        dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        visited = new boolean[V];
        prev = new int[V];
        Arrays.fill(prev, -1);
    }

    public void execute() {
        if (graph instanceof AdjacencyListGraph) {
            executeForAdjList();
        } else {
            executeForMatrix();
        }
    }

    private void executeForMatrix() {
        int V = graph.getV();
        for (int i = 0; i < V; i++) {
            int u = findMinDistance();
            if (u == -1) break;
            visited[u] = true;
            steps.add(new Step(u, StepType.NODE_SELECTED));

            for (int v = 0; v < V; v++) {
                int weight = graph.getAdjMatrix()[u][v];
                if (!visited[v] && weight != Integer.MAX_VALUE && dist[u] != Integer.MAX_VALUE) {
                    steps.add(new Step(u , v ,StepType.EDGE_SELECTED));
                    int newDist = dist[u] + weight;
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u;
                        steps.add(new Step(u, v, newDist, u, StepType.DISTANCE_UPDATED));
                    }
                }
            }
        }
    }
    private void executeForAdjList() {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(source, 0));
        dist[source] = 0;

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int u = node.vertex;
            if (visited[u]) continue;
            visited[u] = true;
            steps.add(new Step(u, StepType.NODE_SELECTED));

            for (Graph_interface.Edge edge : graph.getAdjList().get(u)) {
                int v = edge.dest;
                int weight = edge.weight;
                if (!visited[v] && dist[u] != Integer.MAX_VALUE) {
                    steps.add(new Step(u , v ,StepType.EDGE_SELECTED));
                    int newDist = dist[u] + weight;
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u;
                        pq.add(new Node(v, newDist));
                        steps.add(new Step(u, v, newDist, u, StepType.DISTANCE_UPDATED));
                    }
                }
            }
        }
    }

    public String getPath(int end){
        if (end < 0)
            return "";
        StringBuilder path = new StringBuilder();
        if (prev[end] == -1){
            path.append("源点").append(source).append("与终点").append(end).append("不连通!");
            return path.toString();
        }
        int i=end;
        while ( i != source){
            path.insert(0,"->"+i);
            i=prev[i];
        }
        path.insert(0,source);
        path.insert(0,"源点" + source + "到终点" + end + "的最短路径为");
        return path.toString();
    }

    private int findMinDistance() {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] && dist[i] < min) {
                min = dist[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int[] getDistances() {
        return dist;
    }

    public int[] getPrevious() {
        return prev;
    }

    private static class Node implements Comparable<Node> {
        int vertex;
        int distance;

        public Node(int vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }
}