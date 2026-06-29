package com.example.myapplication;




import java.util.*;

/**
 * Single source of truth for all room definitions and the hallway graph.
 *
 * Coordinate system: SVG viewBox 0 0 760 560
 *
 * Hallway centrelines (used as graph spine):
 *   Left vertical  (CS dept)   x = 58
 *   Main horizontal corridor   y = 105
 *   CS inner vertical          x = 118   ← stairs are here
 *   PHY inner vertical         x = 322   ← stairs are here
 *   Physics vertical           x = 440
 */
public class FloorData {

    // ── Rooms ──────────────────────────────────────────────────────────────
    public static List<Room> buildRooms() {
        List<Room> list = new ArrayList<>();
        // Left outer column  x=8 w=40
        list.add(new Room("M4-15",      "M4-15",       8,  8,  40, 26, Room.Type.CLASS));
        list.add(new Room("M4-13",      "M4-13",       8,  38, 40, 26, Room.Type.CLASS));
        list.add(new Room("M4-11",      "M4-11",       8,  68, 40, 26, Room.Type.CLASS));
        list.add(new Room("M4-9",       "M4-9",        8, 126, 40, 26, Room.Type.CLASS));
        list.add(new Room("StaffRoom_L","StaffRoom",   8, 172, 40, 26, Room.Type.OFFICE));
        list.add(new Room("Secretary",  "Secretary",   8, 202, 40, 26, Room.Type.ADMIN));
        list.add(new Room("ChairPsn",   "ChairPsn",    8, 232, 40, 26, Room.Type.ADMIN));
        list.add(new Room("M4-3",       "M4-3",        8, 278, 40, 26, Room.Type.CLASS));
        list.add(new Room("M4-1",       "M4-1",        8, 308, 40, 26, Room.Type.CLASS));
        // Left inner column  x=68 w=40
        list.add(new Room("M4-14",      "M4-14",      68,  8, 40, 26, Room.Type.CLASS));
        list.add(new Room("M4-12",      "M4-12",      68, 38, 40, 26, Room.Type.CLASS));
        list.add(new Room("M4-10",      "M4-10",      68, 68, 40, 26, Room.Type.CLASS));
        list.add(new Room("M4-2",       "M4-2",       68,340, 40, 26, Room.Type.CLASS));
        // Centre top
        list.add(new Room("PM4-3",      "PM4-3",     132,  8, 84, 88, Room.Type.OFFICE));
        list.add(new Room("PM4-1",      "PM4-1",     230,  8, 84, 88, Room.Type.OFFICE));
        list.add(new Room("WC",         "WC",        320,  8, 52, 52, Room.Type.WC));
        // Amphi
        list.add(new Room("AMPHI_I",    "AMPHI I",   130,114,182,130, Room.Type.AMPHI));
        // Right left-column  x=390 w=40
        list.add(new Room("P4-19",      "P4-19",     390,  8, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-17",      "P4-17",     390, 38, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-15",      "P4-15",     390, 68, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-13",      "P4-13",     390,126, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-11",      "P4-11",     390,156, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-7",       "P4-7",      390,186, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-5",       "P4-5",      390,216, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-3",       "P4-3",      390,262, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-1",       "P4-1",      390,292, 40, 26, Room.Type.CLASS));
        // Right right-column  x=450 w=40
        list.add(new Room("P4-20",      "P4-20",     450,  8, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-18",      "P4-18",     450, 38, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-16",      "P4-16",     450, 68, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-14",      "P4-14",     450,126, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-12",      "P4-12",     450,156, 40, 26, Room.Type.CLASS));
        list.add(new Room("HeadOfLab",  "Head of Lab",450,188, 40, 26, Room.Type.ADMIN));
        list.add(new Room("Admissions", "Admissions",450,218, 40, 26, Room.Type.ADMIN));
        list.add(new Room("StaffRoom_R","StaffRoom",  450,248, 40, 26, Room.Type.OFFICE));
        list.add(new Room("P4-8",       "P4-8",      450,294, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-6",       "P4-6",      450,324, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-4",       "P4-4",      450,354, 40, 26, Room.Type.CLASS));
        list.add(new Room("P4-2",       "P4-2",      450,384, 40, 26, Room.Type.CLASS));
        // Entrances & stairs (rendered, not selectable as destinations)
        list.add(new Room("cs_entrance",  "CS Entrance",  42,536, 32, 20, Room.Type.ENTRANCE));
        list.add(new Room("phy_entrance", "PHY Entrance", 424,536, 32, 20, Room.Type.ENTRANCE));
        list.add(new Room("stairs_left",  "Stairs L1",   111,130, 13, 22, Room.Type.STAIRS));
        list.add(new Room("stairs_left2", "Stairs L2",   111,158, 13, 22, Room.Type.STAIRS));
        list.add(new Room("stairs_right", "Stairs R1",   315,130, 13, 22, Room.Type.STAIRS));
        list.add(new Room("stairs_right2","Stairs R2",   315,158, 13, 22, Room.Type.STAIRS));
        return list;
    }

    // ── Graph ──────────────────────────────────────────────────────────────
    public static Map<String, GraphNode> buildGraph(List<Room> rooms) {
        Map<String, GraphNode> g = new HashMap<>();

        // Add every room as a node (using its centre)
        for (Room r : rooms) g.put(r.id, new GraphNode(r.id, r.cx(), r.cy()));

        // Waypoints – hallway junction/intermediate nodes
        String[][] wps = {
                // Left vertical CS dept  x=58
                {"v_cs_y546","58","546"}, {"v_cs_y410","58","410"},
                {"v_cs_y321","58","321"}, {"v_cs_y245","58","245"},
                {"v_cs_y215","58","215"}, {"v_cs_y185","58","185"},
                {"v_cs_y152","58","152"}, {"v_cs_y105","58","105"},
                {"v_cs_y51", "58","51" }, {"v_cs_y21", "58","21" },
                // Main horizontal corridor  y=105
                {"h_x58","58","105"},  {"h_x118","118","105"}, {"h_x174","174","105"},
                {"h_x222","222","105"},{"h_x272","272","105"}, {"h_x322","322","105"},
                {"h_x346","346","105"},{"h_x410","410","105"}, {"h_x440","440","105"},
                {"h_x470","470","105"},
                // CS inner vertical  x=118  (stairs live here)
                {"v_cs2_y130","118","130"}, {"v_cs2_y158","118","158"},
                {"v_cs2_y200","118","200"}, {"v_cs2_y334","118","334"},
                // PHY inner vertical  x=322  (stairs live here)
                {"v_phy_y130","322","130"}, {"v_phy_y158","322","158"},
                {"v_phy_y200","322","200"}, {"v_phy_y334","322","334"},
                // Physics vertical  x=440
                {"v_phy2_y141","440","141"}, {"v_phy2_y200","440","200"},
                {"v_phy2_y261","440","261"}, {"v_phy2_y290","440","290"},
                {"v_phy2_y334","440","334"}, {"v_phy2_y400","440","400"},
                {"v_phy2_y546","440","546"},
        };
        for (String[] wp : wps)
            g.put(wp[0], new GraphNode(wp[0], Float.parseFloat(wp[1]), Float.parseFloat(wp[2])));

        // ── Edges (bidirectional) ───────────────────────────────────────
        // Left vertical
        link(g,"cs_entrance","v_cs_y546"); link(g,"v_cs_y546","v_cs_y410");
        link(g,"v_cs_y410","v_cs_y321");  link(g,"v_cs_y321","v_cs_y245");
        link(g,"v_cs_y245","v_cs_y215");  link(g,"v_cs_y215","v_cs_y185");
        link(g,"v_cs_y185","v_cs_y152");  link(g,"v_cs_y152","v_cs_y105");
        link(g,"v_cs_y105","h_x58");
        link(g,"h_x58","v_cs_y51");       link(g,"v_cs_y51","v_cs_y21");

        // Rooms → left vertical
        link(g,"M4-1","v_cs_y321");   link(g,"M4-3","v_cs_y245");
        link(g,"ChairPsn","v_cs_y245"); link(g,"Secretary","v_cs_y215");
        link(g,"StaffRoom_L","v_cs_y185"); link(g,"M4-9","v_cs_y152");
        link(g,"M4-11","v_cs_y105");  link(g,"M4-13","v_cs_y51");
        link(g,"M4-15","v_cs_y21");   link(g,"M4-14","v_cs_y21");
        link(g,"M4-12","v_cs_y51");   link(g,"M4-10","v_cs_y105");
        link(g,"M4-2","v_cs_y321");

        // Main horizontal corridor
        link(g,"h_x58","h_x118");   link(g,"h_x118","h_x174");
        link(g,"h_x174","h_x222");  link(g,"h_x222","h_x272");
        link(g,"h_x272","h_x322");  link(g,"h_x322","h_x346");
        link(g,"h_x346","h_x410");  link(g,"h_x410","h_x440");
        link(g,"h_x440","h_x470");

        link(g,"PM4-3","h_x174");   link(g,"PM4-1","h_x272");
        link(g,"WC","h_x346");
        link(g,"AMPHI_I","h_x118"); link(g,"AMPHI_I","h_x174");
        link(g,"P4-19","h_x410");   link(g,"P4-17","h_x410");
        link(g,"P4-15","h_x410");
        link(g,"P4-20","h_x470");   link(g,"P4-18","h_x470");
        link(g,"P4-16","h_x470");

        // CS inner vertical  (stairs are in this hallway)
        link(g,"h_x118","v_cs2_y130");
        link(g,"v_cs2_y130","v_cs2_y158"); link(g,"v_cs2_y158","v_cs2_y200");
        link(g,"v_cs2_y200","v_cs2_y334");
        link(g,"stairs_left","h_x118");    link(g,"stairs_left","v_cs2_y130");
        link(g,"stairs_left2","v_cs2_y130");link(g,"stairs_left2","v_cs2_y158");

        // PHY inner vertical
        link(g,"h_x322","v_phy_y130");
        link(g,"v_phy_y130","v_phy_y158"); link(g,"v_phy_y158","v_phy_y200");
        link(g,"v_phy_y200","v_phy_y334");
        link(g,"stairs_right","h_x322");   link(g,"stairs_right","v_phy_y130");
        link(g,"stairs_right2","v_phy_y130");link(g,"stairs_right2","v_phy_y158");

        // Physics vertical
        link(g,"h_x440","v_phy2_y141");
        link(g,"v_phy2_y141","v_phy2_y200"); link(g,"v_phy2_y200","v_phy2_y261");
        link(g,"v_phy2_y261","v_phy2_y290"); link(g,"v_phy2_y290","v_phy2_y334");
        link(g,"v_phy2_y334","v_phy2_y400"); link(g,"v_phy2_y400","v_phy2_y546");
        link(g,"v_phy2_y546","phy_entrance");

        link(g,"P4-13","v_phy2_y141");  link(g,"P4-11","v_phy2_y141");
        link(g,"P4-14","v_phy2_y141");  link(g,"P4-12","v_phy2_y141");
        link(g,"P4-7","v_phy2_y200");   link(g,"P4-5","v_phy2_y200");
        link(g,"HeadOfLab","v_phy2_y200"); link(g,"Admissions","v_phy2_y200");
        link(g,"StaffRoom_R","v_phy2_y261"); link(g,"P4-3","v_phy2_y261");
        link(g,"P4-1","v_phy2_y290");   link(g,"P4-8","v_phy2_y290");
        link(g,"P4-6","v_phy2_y334");
        link(g,"P4-4","v_phy2_y400");   link(g,"P4-2","v_phy2_y400");

        return g;
    }

    private static void link(Map<String, GraphNode> g, String a, String b) {
        GraphNode na = g.get(a), nb = g.get(b);
        if (na == null || nb == null) return;
        float d = (float) Math.hypot(na.x - nb.x, na.y - nb.y);
        na.edges.add(new GraphNode.Edge(b, d));
        nb.edges.add(new GraphNode.Edge(a, d));
    }
}
