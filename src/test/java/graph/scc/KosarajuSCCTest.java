package graph.scc;

import graph.Graph;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests for KosarajuSCC algorithm.
 */
public class KosarajuSCCTest {
    private Graph graph;

    @Before
    public void setUp() {
        // Create a test graph with known SCCs
        graph = new Graph(6);
        // Component 1: 0, 1, 2 (cycle)
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // Component 2: 3, 4 (cycle)
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        // Component 3: 5 (singleton)
        // DAG edges between components
        graph.addEdge(2, 3, 2);
        graph.addEdge(4, 5, 3);
    }

    @Test
    public void testFindSCCs() {
        KosarajuSCC scc = new KosarajuSCC(graph);
        List<List<Integer>> components = scc.findSCCs();

        assertEquals(3, components.size());
        
        // Verify component sizes
        boolean foundSize3 = false, foundSize2 = false, foundSize1 = false;
        for (List<Integer> comp : components) {
            if (comp.size() == 3) foundSize3 = true;
            if (comp.size() == 2) foundSize2 = true;
            if (comp.size() == 1) foundSize1 = true;
        }
        assertTrue("Should have a component of size 3", foundSize3);
        assertTrue("Should have a component of size 2", foundSize2);
        assertTrue("Should have a component of size 1", foundSize1);
    }

    @Test
    public void testComponentIds() {
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();

        // Vertices in the same cycle should have the same component ID
        int comp0 = scc.getComponentId(0);
        int comp1 = scc.getComponentId(1);
        int comp2 = scc.getComponentId(2);
        assertEquals(comp0, comp1);
        assertEquals(comp1, comp2);
    }

    @Test
    public void testCondensationGraph() {
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        KosarajuSCC.CondensationResult result = scc.buildCondensation();

        // Condensation should have 3 vertices (one per SCC)
        assertEquals(3, result.condensationGraph.getN());
    }
}
