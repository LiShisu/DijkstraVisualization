import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private Graph_interface graph;
    private int source;
    private int[] currentDist;
    private int[] preNode;
    private boolean[] visited;
    private Map<Integer, Point> nodePositions;//key=顶点序号，value=坐标
    private int currentNode = -1;
    private int destNode = -1;
    private boolean ifDrawWeight = false;


    public void setGraph(Graph_interface graph, int source) {

        this.graph = graph;
        this.source = source;
        this.currentDist = new int[graph.getV()];
        for (int i=0; i<graph.getV(); i++){
            currentDist[i] = Integer.MAX_VALUE;
        }
        if (source >= 0)
            currentDist[source] = 0;
        this.preNode = new int[graph.getV()];
        for (int i=0; i<graph.getV(); i++){
            preNode[i] = -1;
        }
        this.visited = new boolean[graph.getV()];
        this.currentNode = -1;
        this.destNode = -1;
        calculateNodePositions();

        repaint();
    }

    public void setCurrentNode(int currentNode){
        this.currentNode = currentNode;
    }
    public void setDestNode(int destNode){
        this.destNode = destNode;
    }
    public void setIfDrawWeight(boolean ifDrawWeight){
        this.ifDrawWeight = ifDrawWeight;
    }
    public void setVisited(){
        visited = new boolean[graph.getV()];
    }
    public void setVisited(int index) { visited[index] = false; }
    public void setCurrentDist(){
        for (int i=0; i<graph.getV(); i++){
            currentDist[i] = Integer.MAX_VALUE;
        }
        if (source >= 0)
            currentDist[source] = 0;
        setPreNode();
    }
    public void setCurrentDist(int index){
        currentDist[index] = Integer.MAX_VALUE;
    }
    public void setPreNode(){
        for (int i=0; i<graph.getV(); i++){
            preNode[i] = -1;
        }
    }
    public void setPreNode(int index){
        preNode[index] = -1;
    }

    public int[] getPreNode(){
        return preNode;
    }

    //计算顶点坐标
    private void calculateNodePositions() {
        int V = graph.getV();//顶点数量
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        nodePositions = new HashMap<>();
        int radius = Math.min(centerX, centerY) - 50;//半径
        for (int i = 0; i < V; i++) {
            double angle = 2 * Math.PI * i / V;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            nodePositions.put(i, new Point(x, y));
        }
    }

    //绘制原图
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //设置图形渲染的提示（Rendering Hint）。这些提示可以影响图形渲染的质量和性能，例如抗锯齿、文本渲染、图像插值等

        // Draw edges
        drawEdges(g2d);

        g2d.setStroke(new BasicStroke(1));

        // Draw nodes
        for (int i = 0; i < graph.getV(); i++) {
            drawNode(g2d, i);
        }

        // Draw distance labels
        if (source >= 0 && ifDrawWeight)
            drawDistanceLabels(g2d);
    }

    private void AM_draw(Graphics2D g2d) {
        for (int u = 0; u < graph.getV(); u++) {
            for (int v = 0; v < graph.getV(); v++) {
                int weight = graph.getAdjMatrix()[u][v];
                if (weight != Integer.MAX_VALUE && u != v) {
                    drawEdge(g2d, u, v);
                    drawWeight(g2d, u, v, weight);
                }
            }
        }
    }

    private void AL_draw(Graphics2D g2d) {
        List<List<Graph_interface.Edge>> adjList = graph.getAdjList();
        for (int u = 0; u < adjList.size(); u++) {
            for (Graph_interface.Edge edge : adjList.get(u)) {
                drawEdge(g2d, u, edge.dest);
                drawWeight(g2d, u, edge.dest, edge.weight);
            }
        }
    }

    private void drawEdges(Graphics2D g2d) {
        if (graph instanceof AdjacencyListGraph) {
            AL_draw(g2d);
        } else {
            AM_draw(g2d);
        }
    }

    private void drawEdge(Graphics2D g2d, int u, int v) {
        Color color = Color.gray;
        BasicStroke basicStroke = new BasicStroke(1);

        if (u == currentNode && v == destNode) {
            color = Color.red;
            basicStroke = new BasicStroke(6);
        }

        Point p1 = nodePositions.get(u);
        Point p2 = nodePositions.get(v);
        g2d.setColor(color);
        g2d.setStroke(basicStroke);
        drawArrow(g2d, p1.x, p1.y, p2.x, p2.y, 20);
    }

    private void drawWeight(Graphics2D g2d, int u, int v, int weight) {
        Point p1 = nodePositions.get(u);
        Point p2 = nodePositions.get(v);

        g2d.setColor(Color.black);

        Font font = new Font("宋体", Font.ITALIC | Font.BOLD, 20);
        g2d.setFont(font);

        String label = String.valueOf(weight);

        int mX = p1.x + (p2.x - p1.x) / 3;
        int mY = p1.y + (p2.y - p1.y) / 3;
        g2d.drawString(label, mX, mY);
    }

    private void drawNode(Graphics2D g2d, int node) {
        Point p = nodePositions.get(node);
        int radius = 20;
        Color color = Color.WHITE;

        if (node == source) {
            color = new Color(144, 163, 239);
        } else if (visited[node]) {
            color = Color.GRAY;
        }

        if (node == currentNode) {
            color = new Color(236, 138, 138);
        }

        g2d.setColor(color);
        g2d.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
        g2d.setColor(Color.BLACK);
        Font font = new Font("宋体", Font.PLAIN, 16);
        g2d.setFont(font);
        g2d.drawOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
        g2d.drawString(String.valueOf(node), p.x - 5, p.y + 5);
    }

    private void drawDistanceLabels(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        Font font = new Font("宋体", Font.BOLD, 20);
        g2d.setFont(font);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (int i = 0; i < graph.getV(); i++) {
            Point p = nodePositions.get(i);
            String dist = currentDist[i] == Integer.MAX_VALUE ? "∞" : String.valueOf(currentDist[i]);

            if (p.y > centerY) {
                g2d.drawString(dist, p.x - 5, p.y + 40);//下面的顶点
            } else if (p.y == centerY && p.x < centerX) {
                g2d.drawString(dist, p.x - 40, p.y + 5);//中左的顶点
            } else if (p.y == centerY && p.x > centerX) {
                g2d.drawString(dist, p.x + 30, p.y + 5);//中右的顶点
            } else {
                g2d.drawString(dist, p.x - 5, p.y - 30);//上面的顶点
            }
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int nodeRadius) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) return;

        double ratio = nodeRadius / dist;
        int startX = (int) (x1 + dx * ratio);
        int startY = (int) (y1 + dy * ratio);
        int endX = (int) (x2 - dx * ratio);
        int endY = (int) (y2 - dy * ratio);

        //Draw line
        g2d.draw(new Line2D.Double(startX, startY, endX, endY));

        // Draw arrow head
        double angle = Math.atan2(dy, dx);
        int arrowLength = 15;
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(endX, endY);
        arrowHead.addPoint((int) (endX - arrowLength * Math.cos(angle - Math.PI / 6)),
                (int) (endY - arrowLength * Math.sin(angle - Math.PI / 6)));
        arrowHead.addPoint((int) (endX - arrowLength * Math.cos(angle + Math.PI / 6)),
                (int) (endY - arrowLength * Math.sin(angle + Math.PI / 6)));
        g2d.fill(arrowHead);
    }

    public void applyStep(Dijkstra.Step step) {
        switch (step.getType()) {
            case NODE_SELECTED:
                destNode = -1;
                currentNode = step.getU();
                visited[currentNode] = true;
//                System.out.println("NODE_SELECTED");
                break;
            case DISTANCE_UPDATED:
                currentDist[step.getV()] = step.getNewDistance();
                preNode[step.getV()] = step.getPreNode();
                destNode = step.getV();
                currentNode = step.getU();
//                System.out.println("DISTANCE_UPDATED");
                break;
            case EDGE_SELECTED:
                currentNode = step.getU();
                destNode = step.getV();
//                System.out.println("EDGE_SELECTED");
                break;
        }
        repaint();
    }
}