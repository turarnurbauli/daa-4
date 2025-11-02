package graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Generates test datasets for graph algorithms.
 */
public class DataGenerator {
    private static final Random random = new Random(42); // Fixed seed for reproducibility
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Represents the JSON structure for graph files.
     */
    public static class GraphJson {
        public boolean directed = true;
        public int n;
        public List<EdgeJson> edges = new ArrayList<>();
        public int source;
        public String weight_model = "edge";
    }

    public static class EdgeJson {
        public int u, v, w;

        public EdgeJson(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    /**
     * Generates a simple DAG (no cycles).
     */
    public static GraphJson generateDAG(int n, double density, int source) {
        GraphJson graph = new GraphJson();
        graph.n = n;
        graph.source = source;
        graph.directed = true;

        Set<String> edgeSet = new HashSet<>();
        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (random.nextDouble() < density) {
                    String edge = u + "," + v;
                    if (!edgeSet.contains(edge)) {
                        edgeSet.add(edge);
                        int weight = random.nextInt(10) + 1;
                        graph.edges.add(new EdgeJson(u, v, weight));
                    }
                }
            }
        }
        return graph;
    }

    /**
     * Generates a graph with cycles (for SCC testing).
     */
    public static GraphJson generateCyclic(int n, int numCycles, double additionalDensity, int source) {
        GraphJson graph = new GraphJson();
        graph.n = n;
        graph.source = source;
        graph.directed = true;

        Set<String> edgeSet = new HashSet<>();
        Random r = new Random(42);

        // Create cycles
        for (int cycle = 0; cycle < numCycles; cycle++) {
            int cycleSize = Math.max(2, n / (numCycles + 1));
            int start = (cycle * cycleSize) % (n - cycleSize);
            
            // Create a cycle
            for (int i = 0; i < cycleSize - 1; i++) {
                int u = start + i;
                int v = start + i + 1;
                int weight = r.nextInt(10) + 1;
                graph.edges.add(new EdgeJson(u, v, weight));
                edgeSet.add(u + "," + v);
            }
            // Close the cycle
            graph.edges.add(new EdgeJson(start + cycleSize - 1, start, r.nextInt(10) + 1));
            edgeSet.add((start + cycleSize - 1) + "," + start);
        }

        // Add additional edges
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u != v && random.nextDouble() < additionalDensity) {
                    String edge = u + "," + v;
                    if (!edgeSet.contains(edge)) {
                        edgeSet.add(edge);
                        int weight = random.nextInt(10) + 1;
                        graph.edges.add(new EdgeJson(u, v, weight));
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Generates a graph with multiple SCCs.
     */
    public static GraphJson generateMultipleSCCs(int n, int numSCCs, double interSCCDensity, int source) {
        GraphJson graph = new GraphJson();
        graph.n = n;
        graph.source = source;
        graph.directed = true;

        Set<String> edgeSet = new HashSet<>();
        int sccSize = n / numSCCs;

        // Create each SCC (strongly connected component)
        for (int scc = 0; scc < numSCCs; scc++) {
            int start = scc * sccSize;
            int end = Math.min(start + sccSize, n);

            // Create a cycle within SCC
            for (int i = start; i < end - 1; i++) {
                int u = i;
                int v = i + 1;
                graph.edges.add(new EdgeJson(u, v, random.nextInt(10) + 1));
                edgeSet.add(u + "," + v);
            }
            if (end - start > 1) {
                graph.edges.add(new EdgeJson(end - 1, start, random.nextInt(10) + 1));
                edgeSet.add((end - 1) + "," + start);
            }
        }

        // Add inter-SCC edges (creating a DAG structure between SCCs)
        for (int scc1 = 0; scc1 < numSCCs; scc1++) {
            for (int scc2 = scc1 + 1; scc2 < numSCCs; scc2++) {
                if (random.nextDouble() < interSCCDensity) {
                    int u = scc1 * sccSize + random.nextInt(sccSize);
                    int v = scc2 * sccSize + random.nextInt(sccSize);
                    if (u < n && v < n && !edgeSet.contains(u + "," + v)) {
                        graph.edges.add(new EdgeJson(u, v, random.nextInt(10) + 1));
                        edgeSet.add(u + "," + v);
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Writes a graph to a JSON file.
     */
    public static void writeToFile(GraphJson graph, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(graph, writer);
        }
    }

    /**
     * Main method to generate all datasets.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Generating datasets...");

        // Small datasets (6-10 nodes)
        GraphJson small1 = generateDAG(8, 0.3, 0);
        writeToFile(small1, "data/small1.json");
        System.out.println("Generated data/small1.json");

        GraphJson small2 = generateCyclic(8, 1, 0.2, 2);
        writeToFile(small2, "data/small2.json");
        System.out.println("Generated data/small2.json");

        GraphJson small3 = generateMultipleSCCs(10, 2, 0.3, 0);
        writeToFile(small3, "data/small3.json");
        System.out.println("Generated data/small3.json");

        // Medium datasets (10-20 nodes)
        GraphJson medium1 = generateDAG(15, 0.25, 0);
        writeToFile(medium1, "data/medium1.json");
        System.out.println("Generated data/medium1.json");

        GraphJson medium2 = generateCyclic(18, 3, 0.15, 5);
        writeToFile(medium2, "data/medium2.json");
        System.out.println("Generated data/medium2.json");

        GraphJson medium3 = generateMultipleSCCs(20, 4, 0.2, 0);
        writeToFile(medium3, "data/medium3.json");
        System.out.println("Generated data/medium3.json");

        // Large datasets (20-50 nodes)
        GraphJson large1 = generateDAG(30, 0.2, 0);
        writeToFile(large1, "data/large1.json");
        System.out.println("Generated data/large1.json");

        GraphJson large2 = generateCyclic(40, 5, 0.1, 10);
        writeToFile(large2, "data/large2.json");
        System.out.println("Generated data/large2.json");

        GraphJson large3 = generateMultipleSCCs(50, 6, 0.15, 0);
        writeToFile(large3, "data/large3.json");
        System.out.println("Generated data/large3.json");

        // Also copy the provided tasks (1).json
        System.out.println("\nAll datasets generated successfully!");
    }
}
