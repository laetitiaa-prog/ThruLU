package com.example.myapplication;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventType;
import com.example.myapplication.ScheduleEvent;
import com.example.myapplication.databinding.ItemScheduleEventBinding;

import java.util.List;

/**
 * CHANGED CLASS: ScheduleAdapter  (NEW)
 *
 * - Colour-codes each row by EventType.
 * - Shows Edit / Delete buttons only when isAdmin == true.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.EventViewHolder> {

    public interface OnEventAction { void onAction(ScheduleEvent event); }

    private final List<ScheduleEvent> events;
    private final boolean             isAdmin;
    private final OnEventAction       onEdit;
    private final OnEventAction       onDelete;

    public ScheduleAdapter(List<ScheduleEvent> events, boolean isAdmin,
                           OnEventAction onEdit, OnEventAction onDelete) {
        this.events   = events;
        this.isAdmin  = isAdmin;
        this.onEdit   = onEdit;
        this.onDelete = onDelete;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduleEventBinding b =
                ItemScheduleEventBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() { return events.size(); }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final ItemScheduleEventBinding b;

        EventViewHolder(ItemScheduleEventBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(ScheduleEvent event) {
            b.tvEventTitle.setText(event.getTitle());

            String dates = event.getStartDate().equals(event.getEndDate()) || event.getEndDate().isEmpty()
                    ? event.getStartDate()
                    : event.getStartDate() + "  →  " + event.getEndDate();
            b.tvEventDates.setText(dates);

            if (event.getDescription().isEmpty()) {
                b.tvEventDescription.setVisibility(View.GONE);
            } else {
                b.tvEventDescription.setVisibility(View.VISIBLE);
                b.tvEventDescription.setText(event.getDescription());
            }

            b.chipEventType.setText(event.getType().name());
            b.chipEventType.setChipBackgroundColor(
                    ColorStateList.valueOf(colorForType(event.getType())));

            if (isAdmin) {
                b.btnEditEvent.setVisibility(View.VISIBLE);
                b.btnDeleteEvent.setVisibility(View.VISIBLE);
                b.btnEditEvent.setOnClickListener(v -> onEdit.onAction(event));
                b.btnDeleteEvent.setOnClickListener(v -> onDelete.onAction(event));
            } else {
                b.btnEditEvent.setVisibility(View.GONE);
                b.btnDeleteEvent.setVisibility(View.GONE);
            }
        }
    }

    private int colorForType(EventType type) {
        switch (type) {
            case HOLIDAY:      return Color.parseColor("#4CAF50");
            case MIDTERM:      return Color.parseColor("#FF9800");
            case FINAL:        return Color.parseColor("#F44336");
            case EXAM:         return Color.parseColor("#E91E63");
            case BREAK:        return Color.parseColor("#2196F3");
            case REGISTRATION: return Color.parseColor("#9C27B0");
            default:           return Color.parseColor("#607D8B");
        }
    }
}