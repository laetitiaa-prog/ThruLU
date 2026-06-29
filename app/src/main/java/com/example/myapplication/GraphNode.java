package com.example.myapplication;


import java.util.ArrayList;
import java.util.List;

public class GraphNode {
    public final String id;
    public final float x, y;           // SVG-space position (centre)
    public final List<Edge> edges = new ArrayList<>();

    public GraphNode(String id, float x, float y) {
        this.id = id; this.x = x; this.y = y;
    }

    public static class Edge {
        public final String to;
        public final float cost;
        Edge(String to, float cost) { this.to = to; this.cost = cost; }
    }
}
