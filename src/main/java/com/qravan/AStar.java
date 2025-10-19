package com.qravan;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.time.*;

public class AStar {

    static class Edge {
        int to, weight;
        Edge(int t, int w) { to = t; weight = w; }
    }

    static class Node implements Comparable<Node> {
        int id;
        double fScore;
        Node(int id, double fScore) {
            this.id = id;
            this.fScore = fScore;
        }
        public int compareTo(Node o) {
            return Double.compare(this.fScore, o.fScore);
        }
    }

    static class Result {
        boolean found;
        List<Integer> path;
        Double cost;
        int expanded;
        int pushes;
        int maxFrontier;
        double runtime;
    }

    static class Graph {
        Map<Integer, List<Edge>> edges = new HashMap<>();
        Map<Integer, Integer> vertices = new HashMap<>();
        int start, goal;
    }

    // ---------- READING GRAPH ----------
    static Graph readGraph(String fileName) throws IOException {
        Graph g = new Graph();
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.startsWith("S,")) {
                g.start = Integer.parseInt(line.split(",")[1]);
            } else if (line.startsWith("D,")) {
                g.goal = Integer.parseInt(line.split(",")[1]);
            } else {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[0]);
                    int cell = Integer.parseInt(parts[1]);
                    g.vertices.put(id, cell);
                    g.edges.putIfAbsent(id, new ArrayList<>());
                } else if (parts.length == 3) {
                    int u = Integer.parseInt(parts[0]);
                    int v = Integer.parseInt(parts[1]);
                    int w = Integer.parseInt(parts[2]);
                    g.edges.putIfAbsent(u, new ArrayList<>());
                    g.edges.putIfAbsent(v, new ArrayList<>());
                    g.edges.get(u).add(new Edge(v, w));
                    g.edges.get(v).add(new Edge(u, w));
                }
            }
        }
        return g;
    }

    // ---------- COORDS ----------
    static Map<Integer, int[]> computeCoords(Map<Integer, Integer> vertices) {
        Map<Integer, int[]> coords = new HashMap<>();
        for (var e : vertices.entrySet()) {
            int cell = e.getValue();
            int x = cell / 10;
            int y = cell % 10;
            coords.put(e.getKey(), new int[]{x, y});
        }
        return coords;
    }

    // ---------- HEURISTICS ----------
    static double hZero(int n, int g, Map<Integer, int[]> coords) { return 0; }

    static double hEuclidean(int n, int g, Map<Integer, int[]> coords) {
        int[] a = coords.get(n), b = coords.get(g);
        return Math.sqrt(Math.pow(a[0]-b[0],2) + Math.pow(a[1]-b[1],2));
    }

    static double hManhattan(int n, int g, Map<Integer, int[]> coords) {
        int[] a = coords.get(n), b = coords.get(g);
        return Math.abs(a[0]-b[0]) + Math.abs(a[1]-b[1]);
    }

    // ---------- A* SEARCH ----------
    static Result aStarSearch(Graph graph, Map<Integer, int[]> coords,
                              int start, int goal,
                              Heuristic heuristic) {
        Map<Integer, Double> gCost = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        gCost.put(start, 0.0);
        parent.put(start, null);
        pq.add(new Node(start, 0));

        int expanded = 0, pushes = 0, maxFrontier = 1;
        Instant t0 = Instant.now();

        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            expanded++;

            if (cur.id == goal) {
                Result res = makeResult(true, parent, goal, gCost.get(goal),
                        expanded, pushes, maxFrontier,
                        Duration.between(t0, Instant.now()).toMillis()/1000.0);
                return res;
            }

            for (Edge e : graph.edges.getOrDefault(cur.id, List.of())) {
                double newG = gCost.get(cur.id) + e.weight;
                if (newG < gCost.getOrDefault(e.to, Double.POSITIVE_INFINITY)) {
                    gCost.put(e.to, newG);
                    parent.put(e.to, cur.id);
                    double f = newG + heuristic.eval(e.to, goal, coords);
                    pq.add(new Node(e.to, f));
                    pushes++;
                }
            }
            maxFrontier = Math.max(maxFrontier, pq.size());
        }

        Result res = makeResult(false, parent, goal, null,
                expanded, pushes, maxFrontier,
                Duration.between(t0, Instant.now()).toMillis()/1000.0);
        return res;
    }

    static Result makeResult(boolean found, Map<Integer,Integer> parent, int goal,
                             Double cost, int expanded, int pushes, int frontier, double runtime) {
        Result r = new Result();
        r.found = found;
        r.cost = cost;
        r.expanded = expanded;
        r.pushes = pushes;
        r.maxFrontier = frontier;
        r.runtime = runtime;

        if (found) {
            List<Integer> path = new ArrayList<>();
            Integer cur = goal;
            while (cur != null) {
                path.add(cur);
                cur = parent.get(cur);
            }
            Collections.reverse(path);
            r.path = path;
        } else {
            r.path = null;
        }
        return r;
    }

    static void showResult(Result data, String mode) {
        System.out.println("\nMODE: " + mode);
        System.out.println("Optimal cost: " + (data.found ? data.cost : "NO PATH"));
        if (data.found)
            System.out.println("Path: " + data.path);
        System.out.println("Expanded: " + data.expanded);
        System.out.println("Pushes: " + data.pushes);
        System.out.println("Max frontier: " + data.maxFrontier);
        System.out.printf("Runtime (s): %.6f\n", data.runtime);
    }

    @FunctionalInterface
    interface Heuristic {
        double eval(int n, int g, Map<Integer, int[]> coords);
    }

    // ---------- MAIN ----------
    public static void main(String[] args) throws Exception {
        System.out.println("============== ASTAR SMALL ==============");
        run("astar_small.txt");

        System.out.println("\n========================================");
        System.out.println("============== ASTAR MEDIUM ==============");
        run("astar_medium.txt");
    }

    static void run(String file) throws Exception {
        Graph g = readGraph(file);
        Map<Integer, int[]> coords = computeCoords(g.vertices);

        List<Object[]> modes = List.of(
                new Object[]{(Heuristic)AStar::hZero, "UCS (h=0)"},
                new Object[]{(Heuristic)AStar::hEuclidean, "A* Euclidean"},
                new Object[]{(Heuristic)AStar::hManhattan, "A* Manhattan"}
        );

        for (Object[] m : modes) {
            Heuristic h = (Heuristic) m[0];
            String name = (String) m[1];
            Result res = aStarSearch(g, coords, g.start, g.goal, h);
            showResult(res, name);
        }
    }
}
