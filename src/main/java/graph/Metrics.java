package graph;

/**
 * Metrics interface for tracking algorithm performance.
 * Records operation counters and execution time.
 */
public class Metrics {
    private long dfsVisits = 0;
    private long edgesTraversed = 0;
    private long queuePops = 0;
    private long queuePushes = 0;
    private long relaxations = 0;
    private long startTime = 0;
    private long endTime = 0;

    /**
     * Start timing the algorithm.
     */
    public void start() {
        startTime = System.nanoTime();
    }

    /**
     * Stop timing the algorithm.
     */
    public void stop() {
        endTime = System.nanoTime();
    }

    /**
     * Get execution time in nanoseconds.
     */
    public long getTimeNanos() {
        return endTime - startTime;
    }

    /**
     * Get execution time in milliseconds.
     */
    public double getTimeMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public void incrementDfsVisits() {
        dfsVisits++;
    }

    public void incrementEdgesTraversed() {
        edgesTraversed++;
    }

    public void incrementQueuePops() {
        queuePops++;
    }

    public void incrementQueuePushes() {
        queuePushes++;
    }

    public void incrementRelaxations() {
        relaxations++;
    }

    public long getDfsVisits() {
        return dfsVisits;
    }

    public long getEdgesTraversed() {
        return edgesTraversed;
    }

    public long getQueuePops() {
        return queuePops;
    }

    public long getQueuePushes() {
        return queuePushes;
    }

    public long getRelaxations() {
        return relaxations;
    }

    /**
     * Reset all counters and timers.
     */
    public void reset() {
        dfsVisits = 0;
        edgesTraversed = 0;
        queuePops = 0;
        queuePushes = 0;
        relaxations = 0;
        startTime = 0;
        endTime = 0;
    }

    @Override
    public String toString() {
        return String.format(
            "Metrics{time=%.4f ms, dfsVisits=%d, edgesTraversed=%d, queuePops=%d, queuePushes=%d, relaxations=%d}",
            getTimeMillis(), dfsVisits, edgesTraversed, queuePops, queuePushes, relaxations
        );
    }
}
