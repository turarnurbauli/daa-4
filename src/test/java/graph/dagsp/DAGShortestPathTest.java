package graph.dagsp;

import graph.Graph;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Unit tests for DAGShortestPath algorithm.
 */
public class DAGShortestPathTest {
    private Graph dag;

    @Before
    public void setUp() {
        // Create a DAG with known shortest paths
        dag = new Graph(6);
        // 0 -> 1 (weight 5)
        dag.addEdge(0, 1, 5);
        // 0 -> 2 (weight 3)
        dag.addEdge(0, 2, 3);
        // 1 -> 3 (weight 2)
        dag.addEdge(1, 3, 2);
        // 2 -> 3 (weight 1)
        dag.addEdge(2, 3, 1);
        // 2 -> 4 (weight 4)
        dag.addEdge(2, 4, 4);
        // 3 -> 5 (weight 2)
        dag.addEdge(3, 5, 2);
        // 4 -> 5 (weight 3)
        dag.addEdge(4, 5, 3);
    }

    @Test
    public void testShortestPaths() {
        DAGShortestPath sp = new DAGShortestPath(dag);
        int[] distances = sp.shortestPaths(0);

        assertEquals(0, distances[0]);
        assertEquals(5, distances[1]);
        assertEquals(3, distances[2]);
        assertEquals(4, distances[3]); // Path: 0->2->3 (3+1=4)
        assertEquals(7, distances[4]); // Path: 0->2->4 (3+4=7)
        assertEquals(6, distances[5]); // Path: 0->2->3->5 (3+1+2=6)
    }

    @Test
    public void testLongestPaths() {
        DAGShortestPath sp = new DAGShortestPath(dag);
        int[] distances = sp.longestPaths(0);

        assertEquals(0, distances[0]);
        // Longest path from 0 to 5: 0->1->3->5 = 5+2+2 = 9
        // Or 0->2->4->5 = 3+4+3 = 10
        assertTrue(distances[5] >= 9);
    }

    @Test
    public void testCriticalPath() {
        DAGShortestPath sp = new DAGShortestPath(dag);
        DAGShortestPath.CriticalPathResult result = sp.findCriticalPath(0);

        assertNotNull(result.path);
        assertTrue(result.length > 0);
        assertTrue(result.path.size() >= 2);
        assertEquals(0, (int) result.path.get(0)); // Should start at source
    }

    @Test
    public void testPathReconstruction() {
        DAGShortestPath sp = new DAGShortestPath(dag);
        sp.shortestPaths(0);
        List<Integer> path = sp.reconstructShortestPath(0, 5);

        assertNotNull(path);
        assertTrue(path.size() >= 2);
        assertEquals(0, (int) path.get(0));
        assertEquals(5, (int) path.get(path.size() - 1));
    }
}
