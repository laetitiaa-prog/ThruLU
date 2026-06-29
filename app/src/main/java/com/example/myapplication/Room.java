package com.example.myapplication;
public class Room {
    public enum Type { CLASS, OFFICE, ADMIN, AMPHI, WC, ENTRANCE, STAIRS }

    public final String id;
    public final String label;
    // SVG coordinate space (viewBox 0 0 760 560)
    public final float svgX, svgY, svgW, svgH;
    public final Type type;

    public Room(String id, String label, float x, float y, float w, float h, Type type) {
        this.id    = id;
        this.label = label;
        this.svgX  = x; this.svgY = y;
        this.svgW  = w; this.svgH = h;
        this.type  = type;
    }

    /** Centre in SVG space */
    public float cx() { return svgX + svgW / 2f; }
    public float cy() { return svgY + svgH / 2f; }

    public boolean isClickable() {
        return type != Type.ENTRANCE && type != Type.STAIRS && type != Type.WC;
    }
}


