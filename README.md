# Homework 1 – AI Algorithms

This repository contains implementations of two classic AI algorithms in Java:
1. **A* Search** – three modes (UCS, Euclidean, Manhattan)
2. **CSP (Graph Coloring)** – Backtracking + MRV + LCV + AC-3

---

## Folder Layout
- `AStar/` – A* search implementation
- `CSP/` – Graph coloring implementation

---

## Quick Start (Windows or IntelliJ)
Run from the **repository root**.

### **A* Search**
**Requirements:** Java 23+

1. Navigate to `AStar/` folder.
2. Ensure input files are present: `astar_small.txt` and `astar_medium.txt`.
3. Compile & run:
   ```bash
   javac -d ../target/classes AStar.java
   java -cp ../target/classes com.qravan.AStar
Output per mode includes:

Optimal cost

Path (S → … → D)

Expanded nodes

Pushes

Max frontier

Runtime (s)

CSP (Graph Coloring)
Requirements: Java 23+

Navigate to CSP/ folder.

Input files: csp_small.txt, csp_tight.txt.

Compile & run:

bash
Copy code
javac -d ../target/classes CSP.java
java -cp ../target/classes com.qravan.CSP csp_small.txt
java -cp ../target/classes com.qravan.CSP csp_tight.txt
Outputs either:

Variable → color assignment

Or failure if no valid coloring exists

Input Formats
A*
Vertex: <id>,<cell_id>

Edge: <u>,<v>,<w> (undirected, non-negative weight)

Start/goal: S,<id> and D,<id>

Example:

shell
Copy code
# vertices
1,11
2,12

# edges
1,2,7

# source & destination
S,1
D,2
CSP
Each line: <variable> <domain> or constraints (your CSP format)

Implementation Notes
A Search*
Single A* algorithm with pluggable heuristics: UCS (h=0), Euclidean, Manhattan

Tie-breaking by node_id for reproducibility

Tracks statistics: Expanded, Pushes, Max frontier, Runtime

Reconstructs path from parent map

CSP
Backtracking with MRV (Minimum Remaining Values), LCV (Least Constraining Value), and AC-3 consistency check

Detects unsolvable instances

Analysis
Optimality: Compare costs for UCS, Euclidean, Manhattan

Efficiency: Compare Expanded nodes and runtime

Heuristic Validity: Check if edge weights satisfy admissibility for heuristics

yaml
Copy code

CSP (Graph Coloring)

Requirements: Java 23+

Navigate to CSP/ folder.

Ensure input files: csp_small.txt, csp_tight.txt.

Compile & run:

javac -d ../target/classes CSP.java
java -cp ../target/classes com.qravan.CSP csp_small.txt
java -cp ../target/classes com.qravan.CSP csp_tight.txt


Outputs either:

Variable → color assignment

Or failure if no valid coloring exists

Input Formats
A*

Vertex: <id>,<cell_id>

Edge: <u>,<v>,<w> (undirected, non-negative weight)

Start/goal: S,<id> and D,<id>

Example:

# vertices
1,11
2,12

# edges
1,2,7

# source & destination
S,1
D,2

CSP

First line: colors=<k> (number of available colors)

Remaining lines: edges <u>,<v> (undirected)

Ignore blank lines and lines starting with #

Example (csp_small.txt):

colors=3
1,2
2,3
3,1
3,4


Example (csp_tight.txt):

colors=4
1,2
1,3
1,4
2,3
2,4
3,4

Implementation Notes
A Search*

Single A* algorithm with pluggable heuristics: UCS (h=0), Euclidean, Manhattan

Tie-breaking by node_id for reproducibility

Tracks statistics: Expanded, Pushes, Max frontier, Runtime

Reconstructs path from parent map

CSP

Backtracking with:

MRV (Minimum Remaining Values)

LCV (Least Constraining Value)

AC-3 consistency check

Detects unsolvable instances

Analysis

Optimality: Compare costs for UCS, Euclidean, Manhattan

Efficiency: Compare Expanded nodes and runtime

Heuristic Validity: Check if edge weights satisfy admissibility for heuristics

Submission

Include:

AStar/ folder with AStar.java + input files

CSP/ folder with CSP.java + input files

README.md (this file)

Ensure all code compiles with Java 23+






