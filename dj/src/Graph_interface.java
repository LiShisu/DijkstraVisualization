import java.util.*;

public interface Graph_interface {
        void addEdge(int src, int dest, int weight);
        void removeEdge(int src, int dest); // 新增删除边方法
        int getV();
        int[][] getAdjMatrix();
        List<List<Edge>> getAdjList();
        Graph_interface refresh(int v);
        void generateRandomGraph(int V, int edgeCount);
        Graph_interface changeStructure(Graph_interface g);
        int checkE(int src, int dest);

        class Edge {
            int dest;//指向点
            int weight;//权重
            public Edge(int dest, int weight) {
                this.dest = dest;
                this.weight = weight;
            }}
}