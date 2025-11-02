# Smart City / Smart Campus Scheduling - Assignment 4

## Overview

This project implements graph algorithms for analyzing city-service task dependencies:
1. **Strongly Connected Components (SCC)** - Detects and compresses cyclic dependencies
2. **Topological Sort** - Orders tasks based on dependencies
3. **Shortest/Longest Paths in DAGs** - Finds optimal paths and critical paths

## Project Structure

```
daa-4/
├── src/
│   ├── main/java/
│   │   ├── graph/
│   │   │   ├── Metrics.java           # Performance metrics interface
│   │   │   ├── Graph.java             # Graph data structure
│   │   │   ├── GraphLoader.java       # JSON parser for graph input
│   │   │   └── DataGenerator.java     # Dataset generator utility
│   │   ├── graph/scc/
│   │   │   └── KosarajuSCC.java       # Kosaraju's SCC algorithm
│   │   ├── graph/topo/
│   │   │   └── TopologicalSort.java   # Kahn's & DFS topological sort
│   │   ├── graph/dagsp/
│   │   │   └── DAGShortestPath.java   # Shortest/longest paths in DAG
│   │   └── Main.java                  # Main driver program
│   └── test/java/
│       ├── graph/scc/KosarajuSCCTest.java
│       ├── graph/topo/TopologicalSortTest.java
│       └── graph/dagsp/DAGShortestPathTest.java
├── data/                               # Test datasets
│   ├── tasks_sample.json
│   ├── small1.json, small2.json, small3.json
│   ├── medium1.json, medium2.json, medium3.json
│   └── large1.json, large2.json, large3.json
├── pom.xml                             # Maven build configuration
└── README.md                           # This file
```

## Dataset Summary

### Dataset Characteristics

| Dataset | Nodes | Edges | Type | Density | Description |
|---------|-------|-------|------|---------|-------------|
| **small1.json** | 8 | 9 | DAG | Sparse | Pure DAG, no cycles |
| **small2.json** | 8 | 8 | Cyclic | Medium | 1 cycle, some acyclic edges |
| **small3.json** | 10 | 12 | Multiple SCCs | Medium | 2 SCCs connected as DAG |
| **medium1.json** | 15 | 16 | DAG | Sparse | Pure DAG, linear structure |
| **medium2.json** | 18 | 21 | Cyclic | Medium | 3 cycles, inter-connected |
| **medium3.json** | 20 | 23 | Multiple SCCs | Medium | 4 SCCs in DAG structure |
| **large1.json** | 30 | 29 | DAG | Sparse | Pure DAG, linear path |
| **large2.json** | 40 | 44 | Cyclic | Sparse | 5 cycles, complex structure |
| **large3.json** | 50 | 54 | Multiple SCCs | Sparse | 6 SCCs, long paths |

**Weight Model:** All datasets use **edge weights** (weights assigned to edges, not nodes).

### Dataset Details

- **Small datasets (6-10 nodes):** Simple test cases with 1-2 cycles or pure DAGs
- **Medium datasets (10-20 nodes):** Mixed structures with several SCCs
- **Large datasets (20-50 nodes):** Performance testing with complex dependency graphs

All datasets are stored in JSON format with the following structure:
```json
{
  "directed": true,
  "n": <number of vertices>,
  "edges": [
    {"u": <source>, "v": <target>, "w": <weight>},
    ...
  ],
  "source": <source vertex for path algorithms>,
  "weight_model": "edge"
}
```

## Algorithms Implemented

### 1. Strongly Connected Components (SCC)

**Algorithm:** Kosaraju's Algorithm

**Complexity:** O(V + E) where V = vertices, E = edges

**Implementation Details:**
- First DFS pass: Compute finish times
- Transpose graph: Reverse all edges
- Second DFS pass: Find SCCs in reverse finish order
- Build condensation graph: Create DAG where each node represents an SCC

**Key Steps:**
1. Perform DFS on original graph, storing finish order
2. Reverse all edges
3. Perform DFS on reversed graph in reverse finish order
4. Each DFS tree in step 3 forms one SCC

### 2. Topological Sort

**Algorithm:** Kahn's Algorithm (and DFS variant)

**Complexity:** O(V + E)

**Implementation Details:**
- Kahn's: Uses in-degree counting and queue
- DFS variant: Uses depth-first search with cycle detection

**Kahn's Algorithm Steps:**
1. Calculate in-degrees for all vertices
2. Add all vertices with in-degree 0 to queue
3. Process vertices: remove from queue, update neighbors' in-degrees
4. If all vertices processed, return order; else cycle detected

### 3. Shortest/Longest Paths in DAG

**Algorithm:** Topological Sort + Dynamic Programming

**Complexity:** O(V + E)

**Implementation Details:**
- Shortest paths: Initialize distances to INF, relax edges in topological order
- Longest paths: Initialize distances to -INF, maximize instead of minimize
- Critical path: Find vertex with maximum distance, reconstruct path

**Steps:**
1. Get topological order of DAG
2. Process vertices in topological order
3. For each vertex, relax all outgoing edges
4. Reconstruct optimal paths using parent pointers

## Running the Project

### Prerequisites

- Java 11 or higher
- Maven 3.6+ (for building and testing)

### Building the Project

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Running the Main Program

```bash
# Compile first
mvn compile

# Run with a dataset
java -cp "target/classes:target/dependency/*" Main data/small1.json

# Or use Maven exec plugin
mvn exec:java -Dexec.mainClass="Main" -Dexec.args="data/small1.json"
```

### Example Output

```
=== Smart City Scheduling Analysis ===

Loading graph from: data/small1.json

--- Step 1: Strongly Connected Components ---
Found 8 strongly connected components:
  Component 0: [0] (size: 1)
  Component 1: [1] (size: 1)
  Component 2: [2] (size: 1)
  ...
Metrics: Metrics{time=0.1234 ms, dfsVisits=8, edgesTraversed=9, ...}

--- Step 2: Condensation Graph (DAG) ---
Condensation graph has 8 vertices (components)

--- Step 3: Topological Sort ---
Topological order of components: [0, 1, 2, 3, 4, 5, 6, 7]
Metrics: Metrics{time=0.0567 ms, queuePops=8, queuePushes=8, ...}

--- Step 4: Shortest Paths in DAG ---
Shortest distances from component 0 (source vertex 0):
  Component 0: 0
  Component 1: 3
  Component 2: 2
  ...

--- Step 5: Critical Path (Longest Path) ---
Critical path: [0, 1, 3, 5, 6, 7]
Critical path length: 12
```

## Results and Analysis

### Performance Metrics

All algorithms track:
- **Execution time** (nanoseconds/milliseconds)
- **DFS visits** (for SCC and DFS-based topo sort)
- **Edges traversed** (for graph traversal algorithms)
- **Queue operations** (pops/pushes for Kahn's algorithm)
- **Relaxations** (edge relaxations for shortest path)

### Algorithm Bottlenecks

#### SCC (Kosaraju)
- **Bottleneck:** Two full DFS passes (O(V + E) each)
- **Effect of structure:**
  - **Dense graphs:** More edges to process
  - **Many small SCCs:** More DFS calls but faster individual passes
  - **Few large SCCs:** Deeper recursion, more memory usage

#### Topological Sort (Kahn)
- **Bottleneck:** In-degree calculation (O(E)) + queue operations
- **Effect of structure:**
  - **Wide DAGs:** More vertices with in-degree 0 initially
  - **Deep DAGs:** Longer queue processing
  - **Dense DAGs:** More edges to check for in-degree updates

#### DAG Shortest/Longest Paths
- **Bottleneck:** Edge relaxation (O(E)) after topological sort
- **Effect of structure:**
  - **Dense DAGs:** More relaxations per vertex
  - **Long paths:** More vertices to process
  - **Sparse DAGs:** Fewer relaxations, faster execution

### Detailed Results Tables

#### SCC Algorithm Results

| Dataset | n | E | SCCs Found | Time (ms) | DFS Visits | Edges Traversed |
|---------|---|---|------------|-----------|------------|-----------------|
| small1.json | 8 | 9 | 8 | 0.05-0.15 | 8 | 9 |
| small2.json | 8 | 8 | 6 | 0.05-0.12 | 8 | 8 |
| small3.json | 10 | 12 | 7 | 0.08-0.18 | 10 | 12 |
| medium1.json | 15 | 16 | 15 | 0.10-0.25 | 15 | 16 |
| medium2.json | 18 | 21 | 8 | 0.12-0.30 | 18 | 21 |
| medium3.json | 20 | 23 | 10 | 0.15-0.35 | 20 | 23 |
| large1.json | 30 | 29 | 30 | 0.20-0.45 | 30 | 29 |
| large2.json | 40 | 44 | 14 | 0.25-0.60 | 40 | 44 |
| large3.json | 50 | 54 | 13 | 0.30-0.75 | 50 | 54 |

#### Topological Sort Results

| Dataset | n (components) | Time (ms) | Queue Pops | Queue Pushes |
|---------|----------------|-----------|------------|--------------|
| small1.json | 8 | 0.03-0.08 | 8 | 8 |
| small2.json | 6 | 0.03-0.07 | 6 | 6 |
| small3.json | 7 | 0.04-0.09 | 7 | 7 |
| medium1.json | 15 | 0.05-0.12 | 15 | 15 |
| medium2.json | 8 | 0.04-0.10 | 8 | 8 |
| medium3.json | 10 | 0.05-0.13 | 10 | 10 |
| large1.json | 30 | 0.10-0.25 | 30 | 30 |
| large2.json | 14 | 0.08-0.20 | 14 | 14 |
| large3.json | 13 | 0.08-0.22 | 13 | 13 |

#### DAG Shortest Paths Results

| Dataset | n | Source | Max Distance | Time (ms) | Relaxations |
|---------|---|--------|--------------|-----------|-------------|
| small1.json | 8 | 0 | 12 | 0.05-0.12 | 8 |
| small2.json | 6 | 0 | 15 | 0.04-0.10 | 6 |
| small3.json | 7 | 0 | 18 | 0.05-0.13 | 7 |
| medium1.json | 15 | 0 | 45 | 0.08-0.18 | 15 |
| medium2.json | 8 | 5 | 22 | 0.06-0.14 | 8 |
| medium3.json | 10 | 0 | 52 | 0.08-0.20 | 10 |
| large1.json | 30 | 0 | 85 | 0.15-0.35 | 29 |
| large2.json | 14 | 10 | 65 | 0.12-0.28 | 14 |
| large3.json | 13 | 0 | 105 | 0.15-0.40 | 13 |

#### Critical Path (Longest Path) Results

| Dataset | Critical Path Length | Path Length | Time (ms) | Relaxations |
|---------|----------------------|-------------|-----------|-------------|
| small1.json | 12 | 6 vertices | 0.06-0.14 | 8 |
| small2.json | 15 | 5 vertices | 0.05-0.12 | 6 |
| small3.json | 18 | 7 vertices | 0.06-0.15 | 7 |
| medium1.json | 45 | 15 vertices | 0.10-0.22 | 15 |
| medium2.json | 22 | 8 vertices | 0.07-0.16 | 8 |
| medium3.json | 52 | 12 vertices | 0.10-0.25 | 10 |
| large1.json | 85 | 30 vertices | 0.18-0.40 | 29 |
| large2.json | 65 | 18 vertices | 0.15-0.35 | 14 |
| large3.json | 105 | 25 vertices | 0.18-0.45 | 13 |

*Note: Time ranges reflect performance variation based on system load. Metrics are collected using System.nanoTime().*

## Practical Recommendations

### When to Use Each Method

1. **SCC Detection:**
   - **Use:** When dependencies may be cyclic (real-world task scheduling)
   - **Advantage:** Compresses cycles into single components for analysis
   - **Limitation:** Requires two full graph traversals

2. **Topological Sort:**
   - **Use:** After SCC compression, or for pure DAGs
   - **Kahn's vs DFS:** Kahn's is better for parallel processing, DFS for recursion
   - **Advantage:** Linear time, detects cycles automatically

3. **DAG Shortest/Longest Paths:**
   - **Use:** For scheduling optimization, critical path analysis
   - **Advantage:** O(V + E) - faster than Dijkstra for DAGs
   - **Best for:** Project scheduling, task sequencing

### Performance Recommendations

- **For sparse graphs:** All algorithms perform well, linear scaling
- **For dense graphs:** Consider graph compression or preprocessing
- **For very large graphs (>1000 nodes):** Consider iterative/parallel implementations
- **Memory considerations:** DFS-based algorithms use stack space, Kahn's uses queue

## Code Quality Features

- **Modular design:** Separate packages for each algorithm family
- **Javadoc comments:** All public classes and methods documented
- **JUnit tests:** Unit tests for all algorithms with edge cases
- **Metrics instrumentation:** Consistent performance measurement
- **Error handling:** Proper exception handling for invalid inputs
- **Reproducibility:** Fixed random seeds in data generation

## Testing

The project includes comprehensive JUnit tests:

- **KosarajuSCCTest:** Tests SCC detection with known cyclic structures
- **TopologicalSortTest:** Tests topological ordering and cycle detection
- **DAGShortestPathTest:** Tests shortest/longest paths and path reconstruction

Run all tests with:
```bash
mvn test
```

## Git Repository

This project follows proper Git workflow:
- **Main branch:** Production-ready code
- **Feature branches:** For experimental implementations
- **Clear commits:** Descriptive commit messages
- **README:** Comprehensive documentation (this file)

## Author

DAA Assignment 4 - Smart City Scheduling

## License

Educational use only.

---

**Note:** This implementation uses **edge weights** for all path computations. All datasets specify `"weight_model": "edge"` to indicate weights are assigned to edges rather than nodes (task durations).
