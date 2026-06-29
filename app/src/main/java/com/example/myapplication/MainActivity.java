package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;


public class MainActivity extends AppCompatActivity {
    RecyclerView recycler;

    Spinner spinnerRole;
    Eventadapter adapter;
    List<Event> allEvents;
    List<Event> filteredEvents;

    Button academicBtn, leisureBtn,events, courses, campusmap,infobot,reviews;
    ExtendedFloatingActionButton fabAddEvent;
    private String activeCategory = "academic";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

                recycler = findViewById(R.id.recycler);
                recycler.setLayoutManager(new LinearLayoutManager(this));
                spinnerRole=findViewById(R.id.spinnerRole);
                academicBtn = findViewById(R.id.academicBtn);
                leisureBtn = findViewById(R.id.leisureBtn);
                campusmap=findViewById(R.id.campusmap);
                courses=findViewById(R.id.courses);
                events=findViewById(R.id.events);
                infobot=findViewById(R.id.infoBot);
                reviews=findViewById(R.id.reviews);
                academicBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F32B2B")));
                academicBtn.setTextColor(Color.WHITE);
                leisureBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                leisureBtn.setTextColor(Color.parseColor("#555555"));
                fabAddEvent = findViewById(R.id.fabAddEvent);





        allEvents = new ArrayList<>();

                allEvents.add(new Event("AI Workshop", "Amphi I", "9 AM", "academic"));
                allEvents.add(new Event("Basketball Team", "Hadath Campus", "5 PM", "leisure"));
                allEvents.add(new Event("Hackathon", "Lab 3", "10 AM", "academic"));
                allEvents.add(new Event("Music Night", "Campus Garden", "7 PM", "leisure"));

                filteredEvents = new ArrayList<>(allEvents);

                adapter = new Eventadapter(filteredEvents);
                recycler.setAdapter(adapter);


                courses.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,MainActivity1.class);
                        startActivity(intent);
                    }
                });
                infobot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                                startActivity(intent);
                    }
                });
                reviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,MainActivity3.class);
                        startActivity(intent);
                    }
                });
             campusmap.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MainActivity6.class);
                startActivity(intent);
            }
        });


        academicBtn.setOnClickListener(v -> filter("academic"));
        leisureBtn.setOnClickListener(v -> filter("leisure"));

        setupAdminFab();


    }
    private void filter(String type) {
        activeCategory = type;
        filteredEvents.clear();
        for (Event e : allEvents) {
            if (e.getType().equals(type)) {
                filteredEvents.add(e);
            }
        }
        adapter.notifyDataSetChanged();

        // Toggle button colors
        if (type.equals("academic")) {
            academicBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F32B2B")));
            academicBtn.setTextColor(Color.WHITE);
            leisureBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            leisureBtn.setTextColor(Color.parseColor("#555555"));
        } else {
            leisureBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F32B2B")));
            leisureBtn.setTextColor(Color.WHITE);
            academicBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            academicBtn.setTextColor(Color.parseColor("#555555"));
        }
    }
    private void setupAdminFab() {
        if (SessionManager.getInstance().isAdmin()) {
            fabAddEvent.setVisibility(View.VISIBLE);
            fabAddEvent.setOnClickListener(v -> showAddEventDialog());
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }
    }

    // ✅ ADDED ─────────────────────────────────────────────────────────────────
    private void showAddEventDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_schedule_event, null);

        TextInputEditText etTitle    = dialogView.findViewById(R.id.etEventTitle);
        TextInputEditText etLocation = dialogView.findViewById(R.id.etEventDescription);
        TextInputEditText etTime     = dialogView.findViewById(R.id.etEventStartDate);
        android.widget.Spinner spinnerType = dialogView.findViewById(R.id.spinnerEventType);

        List<String> types = new ArrayList<>();
        types.add("academic");
        types.add("leisure");
        spinnerType.setAdapter(new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, types));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Event")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String title    = etTitle.getText()    != null ? etTitle.getText().toString().trim()    : "";
                    String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
                    String time     = etTime.getText()     != null ? etTime.getText().toString().trim()     : "";
                    String type     = spinnerType.getSelectedItem() != null
                            ? spinnerType.getSelectedItem().toString() : "academic";

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (time.isEmpty()) {
                        Toast.makeText(this, "Time is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allEvents.add(new Event(title, location, time, type));
                    filter(type); // switches to correct tab and shows new event
                    Toast.makeText(this, "\"" + title + "\" added!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}









