package graph.topo;

import graph.Graph;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests for TopologicalSort algorithm.
 */
public class TopologicalSortTest {
    private Graph dag;

    @Before
    public void setUp() {
        // Create a simple DAG
        dag = new Graph(5);
        // 0 -> 1 -> 3
        dag.addEdge(0, 1, 1);
        dag.addEdge(1, 3, 2);
        // 0 -> 2 -> 3
        dag.addEdge(0, 2, 1);
        dag.addEdge(2, 3, 1);
        // 3 -> 4
        dag.addEdge(3, 4, 1);
    }

    @Test
    public void testKahn() {
        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> order = topo.kahn();

        assertEquals(5, order.size());
        assertTrue(order.contains(0));
        assertTrue(order.contains(1));
        assertTrue(order.contains(2));
        assertTrue(order.contains(3));
        assertTrue(order.contains(4));

        // Verify topological property: for edge u->v, u appears before v
        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);
        int pos3 = order.indexOf(3);
        int pos4 = order.indexOf(4);

        assertTrue(pos0 < pos1);
        assertTrue(pos0 < pos2);
        assertTrue(pos1 < pos3);
        assertTrue(pos2 < pos3);
        assertTrue(pos3 < pos4);
    }

    @Test
    public void testDfsTopo() {
        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> order = topo.dfsTopo();

        assertEquals(5, order.size());
        
        // Verify all vertices are present
        for (int i = 0; i < 5; i++) {
            assertTrue(order.contains(i));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCycleDetection() {
        Graph cyclic = new Graph(3);
        cyclic.addEdge(0, 1, 1);
        cyclic.addEdge(1, 2, 1);
        cyclic.addEdge(2, 0, 1); // Creates cycle

        TopologicalSort topo = new TopologicalSort(cyclic);
        topo.kahn(); // Should throw exception
    }
}
