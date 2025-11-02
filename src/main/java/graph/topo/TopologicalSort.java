package graph.topo;

import graph.Graph;
import graph.Metrics;
import java.util.*;

/**
 * Implementation of Kahn's algorithm for topological sorting.
 * Works on DAGs (should be used on condensation graph after SCC compression).
 */
public class TopologicalSort {
    private final Graph graph;
    private Metrics metrics;
    private List<Integer> topoOrder;

    public TopologicalSort(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Computes a topological order using Kahn's algorithm.
     * 
     * @return list of vertices in topological order
     * @throws IllegalStateException if graph contains cycles
     */
    public List<Integer> kahn() {
        metrics.reset();
        metrics.start();

        int n = graph.getN();
        int[] inDegree = new int[n];
        Queue<Integer> queue = new LinkedList<>();
        topoOrder = new ArrayList<>();

        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Graph.Edge e : graph.getAdjacent(u)) {
                inDegree[e.to]++;
            }
        }

        // Add all vertices with in-degree 0 to queue
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementQueuePushes();
            }
        }

        // Process vertices
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementQueuePops();
            topoOrder.add(u);

            for (Graph.Edge e : graph.getAdjacent(u)) {
                inDegree[e.to]--;
                if (inDegree[e.to] == 0) {
                    queue.offer(e.to);
                    metrics.incrementQueuePushes();
                }
            }
        }

        // Check if all vertices were processed (cycle detection)
        if (topoOrder.size() != n) {
            metrics.stop();
            throw new IllegalStateException("Graph contains cycles! Topological sort not possible.");
        }

        metrics.stop();
        return new ArrayList<>(topoOrder);
    }

    /**
     * Computes topological order using DFS-based approach.
     * 
     * @return list of vertices in topological order
     */
    public List<Integer> dfsTopo() {
        metrics.reset();
        metrics.start();

        int n = graph.getN();
        boolean[] visited = new boolean[n];
        boolean[] inStack = new boolean[n];
        topoOrder = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (!dfsTopoHelper(i, visited, inStack)) {
                    metrics.stop();
                    throw new IllegalStateException("Graph contains cycles!");
                }
            }
        }

        Collections.reverse(topoOrder);
        metrics.stop();
        return new ArrayList<>(topoOrder);
    }

    /**
     * Helper for DFS-based topological sort.
     * Returns false if cycle is detected.
     */
    private boolean dfsTopoHelper(int v, boolean[] visited, boolean[] inStack) {
        if (inStack[v]) {
            return false; // Cycle detected
        }
        if (visited[v]) {
            return true;
        }

        visited[v] = true;
        inStack[v] = true;
        metrics.incrementDfsVisits();

        for (Graph.Edge e : graph.getAdjacent(v)) {
            metrics.incrementEdgesTraversed();
            if (!dfsTopoHelper(e.to, visited, inStack)) {
                return false;
            }
        }

        inStack[v] = false;
        topoOrder.add(v);
        return true;
    }

    /**
     * Gets the metrics object.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Gets the last computed topological order.
     */
    public List<Integer> getTopoOrder() {
        return topoOrder != null ? new ArrayList<>(topoOrder) : null;
    }
}
