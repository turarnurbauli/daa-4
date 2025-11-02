import graph.*;
import graph.scc.KosarajuSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import java.io.IOException;
import java.util.*;

/**
 * Main driver class for Smart City Scheduling assignment.
 * Demonstrates SCC detection, topological sorting, and shortest/longest paths in DAGs.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main <json-file>");
            System.out.println("Example: java Main data/small1.json");
            return;
        }

        String filename = args[0];
        try {
            System.out.println("=== Smart City Scheduling Analysis ===\n");
            System.out.println("Loading graph from: " + filename + "\n");

            Graph graph = GraphLoader.loadFromJson(filename);
            int source = GraphLoader.getSourceFromJson(filename);

            // 1. Find SCCs
            System.out.println("--- Step 1: Strongly Connected Components ---");
            KosarajuSCC scc = new KosarajuSCC(graph);
            List<List<Integer>> components = scc.findSCCs();
            
            System.out.println("Found " + components.size() + " strongly connected components:");
            for (int i = 0; i < components.size(); i++) {
                System.out.println("  Component " + i + ": " + components.get(i) + " (size: " + components.get(i).size() + ")");
            }
            System.out.println("Metrics: " + scc.getMetrics() + "\n");

            // 2. Build condensation graph
            System.out.println("--- Step 2: Condensation Graph (DAG) ---");
            KosarajuSCC.CondensationResult condensation = scc.buildCondensation();
            Graph dag = condensation.condensationGraph;
            System.out.println("Condensation graph has " + dag.getN() + " vertices (components)\n");

            // 3. Topological sort on condensation
            System.out.println("--- Step 3: Topological Sort ---");
            TopologicalSort topo = new TopologicalSort(dag);
            List<Integer> topoOrder = topo.kahn();
            System.out.println("Topological order of components: " + topoOrder);
            System.out.println("Metrics: " + topo.getMetrics());
            
            // Derived order of original tasks after SCC compression
            System.out.println("\nDerived order of original tasks:");
            List<Integer> originalOrder = new ArrayList<>();
            for (int compId : topoOrder) {
                originalOrder.addAll(components.get(compId));
            }
            System.out.println("  " + originalOrder);
            System.out.println();

            // 4. Shortest paths in DAG
            System.out.println("--- Step 4: Shortest Paths in DAG ---");
            int sourceComponent = scc.getComponentId(source);
            DAGShortestPath sp = new DAGShortestPath(dag);
            int[] shortest = sp.shortestPaths(sourceComponent);
            
            System.out.println("Shortest distances from component " + sourceComponent + " (source vertex " + source + "):");
            for (int i = 0; i < shortest.length; i++) {
                if (shortest[i] == Integer.MAX_VALUE / 2) {
                    System.out.println("  Component " + i + ": unreachable");
                } else {
                    System.out.println("  Component " + i + ": " + shortest[i]);
                }
            }
            System.out.println("Metrics: " + sp.getMetrics() + "\n");

            // 5. Longest path (critical path)
            System.out.println("--- Step 5: Critical Path (Longest Path) ---");
            DAGShortestPath lp = new DAGShortestPath(dag);
            DAGShortestPath.CriticalPathResult critical = lp.findCriticalPath(sourceComponent);
            System.out.println("Critical path: " + critical.path);
            System.out.println("Critical path length: " + critical.length);
            System.out.println("Metrics: " + lp.getMetrics() + "\n");

            // Reconstruct one optimal path
            if (shortest.length > 0) {
                System.out.println("--- Sample Path Reconstruction ---");
                for (int i = 0; i < Math.min(shortest.length, 3); i++) {
                    if (shortest[i] != Integer.MAX_VALUE / 2 && i != sourceComponent) {
                        List<Integer> path = sp.reconstructShortestPath(sourceComponent, i);
                        System.out.println("Path from component " + sourceComponent + " to " + i + ": " + path);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
