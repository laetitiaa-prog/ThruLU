package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Eventadapter extends RecyclerView.Adapter<Eventadapter.ViewHolder> {

    List<Event> eventList;

    public Eventadapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, time;
        Button registerButton; // ← ADDED

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            location = itemView.findViewById(R.id.eventLocation);
            time = itemView.findViewById(R.id.eventTime);
            registerButton = itemView.findViewById(R.id.register); // ← ADDED
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event e = eventList.get(position);

        holder.title.setText(e.getTitle());
        holder.location.setText("• " + e.getLocation());
        holder.time.setText("• " + e.getTime());

        // ← ADDED
        holder.registerButton.setOnClickListener(v -> {
            showRegistrationDialog(v.getContext(), e.getTitle(), e.getTitle());
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void showRegistrationDialog(Context context, String eventId, String eventName) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_register, null);
        EditText etName = dialogView.findViewById(R.id.etName); // local variable, not field

        new AlertDialog.Builder(context)
                .setTitle("Register for " + eventName)
                .setView(dialogView)
                .setPositiveButton("Register", (dialog, which) -> {
                    String name = etName.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (RegistrationManager.isRegistered(context, eventId, name)) {
                        Toast.makeText(context,
                                "\"" + name + "\" is already registered for this event!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    RegistrationManager.register(context, eventId, name);
                    Toast.makeText(context,
                            "Welcome, " + name + "! You're registered for " + eventName + ".",
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}