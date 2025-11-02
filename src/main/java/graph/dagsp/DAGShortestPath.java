package graph.dagsp;

import graph.Graph;
import graph.Metrics;
import graph.topo.TopologicalSort;
import java.util.*;

/**
 * Implementation of shortest and longest paths in a Directed Acyclic Graph (DAG).
 * Uses topological sort + dynamic programming approach.
 */
public class DAGShortestPath {
    private final Graph graph;
    private Metrics metrics;
    private int[] distances;
    private int[] parent;
    private static final int INF = Integer.MAX_VALUE / 2;

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Computes single-source shortest paths using topological sort.
     * 
     * @param source source vertex
     * @return array of shortest distances from source to all vertices
     */
    public int[] shortestPaths(int source) {
        metrics.reset();
        metrics.start();

        int n = graph.getN();
        distances = new int[n];
        parent = new int[n];
        Arrays.fill(distances, INF);
        Arrays.fill(parent, -1);
        distances[source] = 0;

        // Get topological order
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.kahn();

        // Relax edges in topological order
        for (int u : order) {
            for (Graph.Edge e : graph.getAdjacent(u)) {
                metrics.incrementRelaxations();
                if (distances[u] != INF && distances[e.to] > distances[u] + e.weight) {
                    distances[e.to] = distances[u] + e.weight;
                    parent[e.to] = u;
                }
            }
        }

        metrics.stop();
        return distances.clone();
    }

    /**
     * Computes longest path (critical path) in the DAG.
     * Uses sign inversion: multiply weights by -1 and find shortest path.
     * 
     * @param source source vertex
     * @return array of longest distances from source to all vertices
     */
    public int[] longestPaths(int source) {
        metrics.reset();
        metrics.start();

        int n = graph.getN();
        distances = new int[n];
        parent = new int[n];
        Arrays.fill(distances, -INF);
        Arrays.fill(parent, -1);
        distances[source] = 0;

        // Get topological order
        TopologicalSort topo = new TopologicalSort(graph);
        List<Integer> order = topo.kahn();

        // Relax edges in topological order (maximizing instead of minimizing)
        for (int u : order) {
            if (distances[u] != -INF) {
                for (Graph.Edge e : graph.getAdjacent(u)) {
                    metrics.incrementRelaxations();
                    if (distances[e.to] < distances[u] + e.weight) {
                        distances[e.to] = distances[u] + e.weight;
                        parent[e.to] = u;
                    }
                }
            }
        }

        metrics.stop();
        return distances.clone();
    }

    /**
     * Finds the critical path (longest path) and its length.
     * 
     * @param source source vertex
     * @return CriticalPathResult containing path and length
     */
    public CriticalPathResult findCriticalPath(int source) {
        int[] longest = longestPaths(source);
        
        // Find the vertex with maximum distance
        int maxDist = -INF;
        int maxVertex = source;
        for (int i = 0; i < longest.length; i++) {
            if (longest[i] > maxDist && longest[i] != -INF) {
                maxDist = longest[i];
                maxVertex = i;
            }
        }

        // Reconstruct path
        List<Integer> path = reconstructPath(source, maxVertex);
        
        return new CriticalPathResult(path, maxDist);
    }

    /**
     * Reconstructs a path from source to target.
     * 
     * @param source source vertex
     * @param target target vertex
     * @return list of vertices forming the path
     */
    public List<Integer> reconstructPath(int source, int target) {
        List<Integer> path = new ArrayList<>();
        int current = target;
        
        // Backtrack from target to source
        while (current != -1) {
            path.add(current);
            if (current == source) break;
            current = parent[current];
        }
        
        Collections.reverse(path);
        return path;
    }

    /**
     * Reconstructs optimal path from source to target using shortest paths.
     */
    public List<Integer> reconstructShortestPath(int source, int target) {
        shortestPaths(source);
        return reconstructPath(source, target);
    }

    /**
     * Gets the metrics object.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Gets the last computed distances.
     */
    public int[] getDistances() {
        return distances != null ? distances.clone() : null;
    }

    /**
     * Result containing critical path information.
     */
    public static class CriticalPathResult {
        public final List<Integer> path;
        public final int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }
    }
}
