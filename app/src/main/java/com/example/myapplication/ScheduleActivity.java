package com.example.myapplication;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ScheduleActivity extends AppCompatActivity {

    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        boolean isAdmin = SessionManager.getInstance().isAdmin();

        RecyclerView recyclerSchedule = findViewById(R.id.recyclerSchedule);
        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);

        adapter = new ScheduleAdapter(
                AppRepository.getInstance().getScheduleEvents(),
                isAdmin,
                this::showEditDialog,
                event -> new MaterialAlertDialogBuilder(this)
                        .setTitle("Delete Event")
                        .setMessage("Delete \"" + event.getTitle() + "\"?")
                        .setPositiveButton("Delete", (d, w) -> {
                            AppRepository.getInstance().deleteScheduleEvent(event.getId());
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        recyclerSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerSchedule.setAdapter(adapter);

        if (isAdmin) {
            fabAddEvent.setVisibility(View.VISIBLE);
            fabAddEvent.setOnClickListener(v -> showEditDialog(null));
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }

        // Bottom nav
        findViewById(R.id.events).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.courses).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity1.class)));
        findViewById(R.id.campusmap).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity6.class)));
        findViewById(R.id.infoBot).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity2.class)));
        findViewById(R.id.reviews).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity3.class)));
    }

    private void showEditDialog(ScheduleEvent existing) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_schedule_event, null);

        android.widget.EditText etTitle       = dialogView.findViewById(R.id.etEventTitle);
        android.widget.EditText etDescription = dialogView.findViewById(R.id.etEventDescription);
        android.widget.EditText etStartDate   = dialogView.findViewById(R.id.etEventStartDate);
        android.widget.EditText etEndDate     = dialogView.findViewById(R.id.etEventEndDate);

        if (existing != null) {
            etTitle.setText(existing.getTitle());
            etDescription.setText(existing.getDescription());
            etStartDate.setText(existing.getStartDate());
            etEndDate.setText(existing.getEndDate());
        }

        EventType[] types = EventType.values();
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++) typeNames[i] = types[i].name();
        final int[] selectedIndex = {existing != null ? existing.getType().ordinal() : 0};

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(existing == null ? "Add Event" : "Edit Event")
                .setView(dialogView)
                .setNeutralButton("Type: " + typeNames[selectedIndex[0]], null)
                .setPositiveButton(existing == null ? "Add" : "Save", (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String start = etStartDate.getText().toString().trim();
                    if (title.isEmpty() || start.isEmpty()) return;

                    String end  = etEndDate.getText().toString().trim();
                    if (end.isEmpty()) end = start;
                    String desc = etDescription.getText().toString().trim();

                    if (existing == null) {
                        AppRepository.getInstance().addScheduleEvent(new ScheduleEvent(
                                AppRepository.getInstance().newId(),
                                title, desc, start, end, types[selectedIndex[0]]));
                    } else {
                        existing.setTitle(title);
                        existing.setDescription(desc);
                        existing.setStartDate(start);
                        existing.setEndDate(end);
                        existing.setType(types[selectedIndex[0]]);
                        AppRepository.getInstance().updateScheduleEvent(existing);
                    }
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Select Event Type")
                        .setSingleChoiceItems(typeNames, selectedIndex[0], (d2, which) -> {
                            selectedIndex[0] = which;
                            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                                    .setText("Type: " + typeNames[which]);
                            d2.dismiss();
                        })
                        .show()
        );
    }
}