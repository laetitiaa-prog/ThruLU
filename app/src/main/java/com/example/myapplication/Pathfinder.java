package com.example.myapplication;
import java.util.*;

public class Pathfinder {

    private final Map<String, GraphNode> graph;

    public Pathfinder(Map<String, GraphNode> graph) {
        this.graph = graph;
    }

    /** Returns ordered list of node IDs from start to end, or null if unreachable. */
    public List<String> findPath(String startId, String endId) {
        Map<String, Float> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String id : graph.keySet()) dist.put(id, Float.MAX_VALUE);
        dist.put(startId, 0f);

        // Min-heap by distance
        PriorityQueue<String> pq = new PriorityQueue<>(
                Comparator.comparingDouble(id -> dist.getOrDefault(id, Float.MAX_VALUE)));
        pq.add(startId);

        while (!pq.isEmpty()) {
            String cur = pq.poll();
            if (visited.contains(cur)) continue;
            visited.add(cur);
            if (cur.equals(endId)) break;

            GraphNode node = graph.get(cur);
            if (node == null) continue;
            float curDist = dist.getOrDefault(cur, Float.MAX_VALUE);

            for (GraphNode.Edge edge : node.edges) {
                float nd = curDist + edge.cost;
                if (nd < dist.getOrDefault(edge.to, Float.MAX_VALUE)) {
                    dist.put(edge.to, nd);
                    prev.put(edge.to, cur);
                    pq.add(edge.to);
                }
            }
        }

        if (dist.getOrDefault(endId, Float.MAX_VALUE) == Float.MAX_VALUE) return null;

        List<String> path = new ArrayList<>();
        String cur = endId;
        while (cur != null) { path.add(0, cur); cur = prev.get(cur); }
        return path;
    }
}

