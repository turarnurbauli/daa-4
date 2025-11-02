package graph.scc;

import graph.Graph;
import graph.Metrics;
import java.util.*;

/**
 * Implementation of Kosaraju's algorithm for finding Strongly Connected Components (SCC).
 * Also builds the condensation graph (DAG of components).
 */
public class KosarajuSCC {
    private final Graph graph;
    private Metrics metrics;
    private boolean[] visited;
    private int[] componentId;
    private int componentCount;
    private List<List<Integer>> components;
    private List<Integer> finishOrder;

    public KosarajuSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new Metrics();
    }

    /**
     * Finds all strongly connected components.
     * 
     * @return list of SCCs, each SCC is a list of vertex IDs
     */
    public List<List<Integer>> findSCCs() {
        metrics.reset();
        metrics.start();

        int n = graph.getN();
        visited = new boolean[n];
        componentId = new int[n];
        componentCount = 0;
        components = new ArrayList<>();
        finishOrder = new ArrayList<>();

        // Step 1: First DFS to determine finish order
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs1(i);
            }
        }

        // Step 2: Reverse graph
        Graph transposed = graph.transpose();

        // Step 3: Second DFS in reverse finish order
        Arrays.fill(visited, false);
        Collections.reverse(finishOrder);

        for (int v : finishOrder) {
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                dfs2(transposed, v, component);
                components.add(component);
                for (int u : component) {
                    componentId[u] = componentCount;
                }
                componentCount++;
            }
        }

        metrics.stop();
        return new ArrayList<>(components);
    }

    /**
     * First DFS pass to compute finish times.
     */
    private void dfs1(int v) {
        visited[v] = true;
        metrics.incrementDfsVisits();

        for (Graph.Edge e : graph.getAdjacent(v)) {
            metrics.incrementEdgesTraversed();
            if (!visited[e.to]) {
                dfs1(e.to);
            }
        }

        finishOrder.add(v);
    }

    /**
     * Second DFS pass on transposed graph to find components.
     */
    private void dfs2(Graph transposed, int v, List<Integer> component) {
        visited[v] = true;
        metrics.incrementDfsVisits();
        component.add(v);

        for (Graph.Edge e : transposed.getAdjacent(v)) {
            metrics.incrementEdgesTraversed();
            if (!visited[e.to]) {
                dfs2(transposed, e.to, component);
            }
        }
    }

    /**
     * Builds the condensation graph (DAG where each node is an SCC).
     * 
     * @return condensation graph and mapping from original vertices to component IDs
     */
    public CondensationResult buildCondensation() {
        if (components == null) {
            findSCCs();
        }

        // Create mapping: component ID -> new vertex ID in condensation graph
        Map<Integer, Integer> componentToVertex = new HashMap<>();
        for (int i = 0; i < componentCount; i++) {
            componentToVertex.put(i, i);
        }

        // Build condensation graph
        Graph condensation = new Graph(componentCount);
        Set<String> seenEdges = new HashSet<>();

        for (int u = 0; u < graph.getN(); u++) {
            int compU = componentId[u];
            for (Graph.Edge e : graph.getAdjacent(u)) {
                int compV = componentId[e.to];
                if (compU != compV) {
                    String edgeKey = compU + "->" + compV;
                    if (!seenEdges.contains(edgeKey)) {
                        seenEdges.add(edgeKey);
                        // Use minimum weight edge between components (or could use max/average)
                        condensation.addEdge(compU, compV, e.weight);
                    }
                }
            }
        }

        return new CondensationResult(condensation, componentId);
    }

    /**
     * Gets the component ID for a vertex.
     */
    public int getComponentId(int vertex) {
        if (componentId == null) {
            findSCCs();
        }
        return componentId[vertex];
    }

    /**
     * Gets the number of components.
     */
    public int getComponentCount() {
        if (components == null) {
            findSCCs();
        }
        return componentCount;
    }

    /**
     * Gets the metrics object.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Result of condensation graph building.
     */
    public static class CondensationResult {
        public final Graph condensationGraph;
        public final int[] vertexToComponent;

        public CondensationResult(Graph condensationGraph, int[] vertexToComponent) {
            this.condensationGraph = condensationGraph;
            this.vertexToComponent = vertexToComponent;
        }
    }
}
