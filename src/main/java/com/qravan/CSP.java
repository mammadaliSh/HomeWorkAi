package com.qravan;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CSP {

    static class Graph {
        Map<Integer, Set<Integer>> adj = new HashMap<>();
        int colors;
    }

    // ---------- READING INPUT ----------
    static Graph readInput(String filename) throws IOException {
        Graph g = new Graph();
        List<String> lines = Files.readAllLines(new File(filename).toPath());
        g.colors = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.startsWith("colors=")) {
                g.colors = Integer.parseInt(line.split("=")[1]);
            } else {
                String[] parts = line.split(",");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                g.adj.putIfAbsent(u, new HashSet<>());
                g.adj.putIfAbsent(v, new HashSet<>());
                g.adj.get(u).add(v);
                g.adj.get(v).add(u);
            }
        }

        return g;
    }

    // ---------- INIT DOMAINS ----------
    static Map<Integer, List<Integer>> initDomains(Graph g) {
        Map<Integer, List<Integer>> domains = new HashMap<>();
        for (int node : g.adj.keySet()) {
            domains.put(node, new ArrayList<>());
            for (int i = 1; i <= g.colors; i++) domains.get(node).add(i);
        }
        return domains;
    }

    // ---------- MRV SELECT ----------
    static int selectUnassignedVar(Map<Integer, List<Integer>> domains, Map<Integer,Integer> assignment) {
        return domains.keySet().stream()
                .filter(n -> !assignment.containsKey(n))
                .min(Comparator.comparingInt(n -> domains.get(n).size()))
                .orElseThrow();
    }

    // ---------- LCV ORDER ----------
    static List<Integer> orderValues(int var, Map<Integer,List<Integer>> domains, Graph g) {
        Map<Integer,Integer> conflictCount = new HashMap<>();
        for (int val : domains.get(var)) {
            int count = 0;
            for (int nb : g.adj.get(var)) {
                if (domains.containsKey(nb) && domains.get(nb).contains(val)) count++;
            }
            conflictCount.put(val, count);
        }
        return domains.get(var).stream()
                .sorted(Comparator.comparingInt(conflictCount::get))
                .collect(Collectors.toList());
    }

    // ---------- AC-3 ----------
    static boolean ac3(Graph g, Map<Integer,List<Integer>> domains) {
        Queue<int[]> queue = new LinkedList<>();
        for (int x : g.adj.keySet()) {
            for (int y : g.adj.get(x)) queue.add(new int[]{x,y});
        }

        while (!queue.isEmpty()) {
            int[] arc = queue.poll();
            int x = arc[0], y = arc[1];
            if (revise(domains, x, y)) {
                if (domains.get(x).isEmpty()) return false;
                for (int z : g.adj.get(x)) {
                    if (z != y) queue.add(new int[]{z, x});
                }
            }
        }
        return true;
    }

    static boolean revise(Map<Integer,List<Integer>> domains, int x, int y) {
        boolean revised = false;
        List<Integer> xDomain = new ArrayList<>(domains.get(x));
        for (int val : xDomain) {
            boolean allSame = domains.get(y).stream().allMatch(v -> v == val);
            if (allSame) {
                domains.get(x).remove((Integer)val);
                revised = true;
            }
        }
        return revised;
    }

    // ---------- BACKTRACKING ----------
    static Map<Integer,Integer> backtrack(Map<Integer,Integer> assignment, Graph g, Map<Integer,List<Integer>> domains) {
        if (assignment.size() == g.adj.size()) return assignment;

        int var = selectUnassignedVar(domains, assignment);
        for (int value : orderValues(var, domains, g)) {
            boolean consistent = g.adj.get(var).stream().allMatch(nb -> !assignment.containsKey(nb) || assignment.get(nb) != value);
            if (consistent) {
                Map<Integer,List<Integer>> localDomains = deepCopyDomains(domains);
                assignment.put(var, value);
                if (ac3(g, localDomains)) {
                    Map<Integer,Integer> result = backtrack(assignment, g, localDomains);
                    if (result != null) return result;
                }
                assignment.remove(var);
            }
        }

        return null;
    }

    static Map<Integer,List<Integer>> deepCopyDomains(Map<Integer,List<Integer>> domains) {
        Map<Integer,List<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer,List<Integer>> e : domains.entrySet()) {
            copy.put(e.getKey(), new ArrayList<>(e.getValue()));
        }
        return copy;
    }

    // ---------- SOLVE ----------
    static void solveCSP(String filename) throws IOException {
        Graph g = readInput(filename);
        Map<Integer,List<Integer>> domains = initDomains(g);
        Map<Integer,Integer> assignment = backtrack(new HashMap<>(), g, domains);

        if (assignment != null) {
            System.out.println("SOLUTION:");
            assignment.keySet().stream().sorted().forEach(k ->
                    System.out.println("Var " + k + " -> Color " + assignment.get(k))
            );
        } else {
            System.out.println("failure");
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java CSP <input_file>");
            return;
        }
        solveCSP(args[0]);
    }
}
