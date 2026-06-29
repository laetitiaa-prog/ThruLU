package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {


    public interface OnCourseClick {
        void onClick(Course course);
    }

    private List<Course> courseList;
    private final OnCourseClick listener;

    public CourseAdapter(List<Course> courseList, OnCourseClick listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, doctor, credits;

        public ViewHolder(View itemView) {
            super(itemView);
            name    = itemView.findViewById(R.id.courseName);
            doctor  = itemView.findViewById(R.id.courseDoctor);
            credits = itemView.findViewById(R.id.courseCredits);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course c = courseList.get(position);

        holder.name.setText(c.getId());
        holder.doctor.setText("Dr " + c.getDoctor());
        holder.credits.setText(c.getCredits() + " credits");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
}