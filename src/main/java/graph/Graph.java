package graph;

import java.util.*;

/**
 * Represents a directed graph with edge weights.
 */
public class Graph {
    private final int n;  // number of vertices
    private final List<List<Edge>> adjList;
    private final boolean weighted;

    /**
     * Represents a weighted edge.
     */
    public static class Edge {
        public final int to;
        public final int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Constructs a graph with n vertices.
     */
    public Graph(int n) {
        this.n = n;
        this.adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }
        this.weighted = true;
    }

    /**
     * Adds a directed edge from u to v with weight w.
     */
    public void addEdge(int u, int v, int w) {
        adjList.get(u).add(new Edge(v, w));
    }

    /**
     * Gets the adjacency list for vertex u.
     */
    public List<Edge> getAdjacent(int u) {
        return adjList.get(u);
    }

    /**
     * Gets the number of vertices.
     */
    public int getN() {
        return n;
    }

    /**
     * Gets the transpose of this graph (all edges reversed).
     */
    public Graph transpose() {
        Graph transposed = new Graph(n);
        for (int u = 0; u < n; u++) {
            for (Edge e : adjList.get(u)) {
                transposed.addEdge(e.to, u, e.weight);
            }
        }
        return transposed;
    }

    /**
     * Gets all edges in the graph.
     */
    public List<EdgeData> getAllEdges() {
        List<EdgeData> edges = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (Edge e : adjList.get(u)) {
                edges.add(new EdgeData(u, e.to, e.weight));
            }
        }
        return edges;
    }

    /**
     * Represents an edge with source, destination, and weight.
     */
    public static class EdgeData {
        public final int u;
        public final int v;
        public final int w;

        public EdgeData(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }
}
