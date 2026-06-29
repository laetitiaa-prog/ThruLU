package com.example.myapplication;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;

public class MainActivity6 extends AppCompatActivity {

    private IsometricFloorView floorView;
    private TextView tvRoomName, tvRoomDesc, tvDistance;

    private List<Room>              rooms;
    private Map<String, GraphNode>  graph;
    private Pathfinder              pathfinder;

    private String  selectedEntrance = "cs_entrance";
    private Room    selectedRoom     = null;

    // Entrance button references
    private Button btnCS, btnPHY, btnStL, btnStR, events , course, map,bot,reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campus_map);
        // Check login state
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        LinearLayout bottomNavBar = findViewById(R.id.bottomNavBar);

        if (isLoggedIn) {
            bottomNavBar.setVisibility(View.VISIBLE);
        } else {
            bottomNavBar.setVisibility(View.GONE);
        }

        // Build data
        rooms      = FloorData.buildRooms();
        graph      = FloorData.buildGraph(rooms);
        pathfinder = new Pathfinder(graph);
        events=findViewById(R.id.events);
        course=findViewById(R.id.course);
        map=findViewById(R.id.map);
        bot=findViewById(R.id.bot);
        reviews=findViewById(R.id.reviews);


        // Views
        floorView  = findViewById(R.id.floorView);
        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomDesc = findViewById(R.id.tvRoomDesc);
        tvDistance = findViewById(R.id.tvDistance);

        btnCS  = findViewById(R.id.btnCS);
        btnPHY = findViewById(R.id.btnPHY);
        btnStL = findViewById(R.id.btnStairsLeft);
        btnStR = findViewById(R.id.btnStairsRight);

        floorView.setData(rooms, graph);
        floorView.setOnRoomClickListener(this::onRoomClicked);

        // Entrance buttons
        btnCS .setOnClickListener(v -> selectEntrance("cs_entrance",  btnCS));
        btnPHY.setOnClickListener(v -> selectEntrance("phy_entrance", btnPHY));
        btnStL.setOnClickListener(v -> selectEntrance("stairs_left",  btnStL));
        btnStR.setOnClickListener(v -> selectEntrance("stairs_right", btnStR));


        highlightEntranceButton(btnCS);
        map.setOnClickListener(v->{
            Intent intent = new Intent (MainActivity6.this, MainActivity6.class);
            startActivity(intent);
        });

        // InfoBot Page
        bot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity6.this, MainActivity2.class);
            startActivity(intent);
        });

        // Reviews Page
        reviews.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity6.this, MainActivity3.class);
            startActivity(intent);
        });
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity6.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void selectEntrance(String id, Button btn) {
        selectedEntrance = id;
        highlightEntranceButton(btn);
        if (selectedRoom != null) runPath(selectedRoom);
    }

    private void onRoomClicked(Room room) {
        selectedRoom = room;
        runPath(room);
    }

    private void runPath(Room room) {
        List<String> path = pathfinder.findPath(selectedEntrance, room.id);
        floorView.setPath(path, room);

        tvRoomName.setText(room.label);
        if (path != null) {
            tvRoomDesc.setText("From: " + entranceName(selectedEntrance) + "\nWaypoints: " + path.size());
            // Approximate distance: count hops × average hallway unit
            tvDistance.setText("~" + Math.round(pathCost(path)) + " units");
        } else {
            tvRoomDesc.setText("No route found.");
            tvDistance.setText("");
        }
    }

    private float pathCost(List<String> path) {
        float total = 0;
        for (int i = 0; i < path.size()-1; i++) {
            GraphNode a = graph.get(path.get(i));
            GraphNode b = graph.get(path.get(i+1));
            if (a!=null && b!=null)
                total += (float)Math.hypot(a.x-b.x, a.y-b.y);
        }
        return total;
    }

    private void highlightEntranceButton(Button active) {
        Button[] all = {btnCS, btnPHY, btnStL, btnStR};
        for (Button b : all) {
            b.setBackgroundResource(b == active
                    ? R.drawable.btn_entrance_active
                    : R.drawable.btn_entrance_normal);
            b.setTextColor(ContextCompat.getColor(this,
                    b == active ? R.color.entranceActive : R.color.textMuted));
        }
    }

    private String entranceName(String id) {
        switch (id) {
            case "cs_entrance":  return "CS Dept Entrance";
            case "phy_entrance": return "Physics Entrance";
            case "stairs_left":  return "Stairs Left (CS)";
            case "stairs_right": return "Stairs Right (PHY)";
            default: return id;
        }
    }



}
