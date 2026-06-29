package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {
    Button campusmap , events, infobot,courses,reviews,btnSchedule,open;


    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.course_main);
        RecyclerView recyclerView;
        CourseAdapter adapter;
        List<Course> courseList;
        campusmap = findViewById(R.id.campusmap);
        events = findViewById(R.id.events);
        infobot = findViewById(R.id.infoBot);
        courses = findViewById(R.id.courses);
        reviews = findViewById(R.id.reviews);
        btnSchedule=findViewById(R.id.BtnSchedule);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        campusmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, MainActivity6.class); // or whichever is your map activity
                startActivity(intent);
            }
        });
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, MainActivity.class);
                startActivity(intent);
            }
        });
        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, MainActivity1.class);
                startActivity(intent);
            }
        });
        infobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, MainActivity2.class);
                startActivity(intent);

            }
        });
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity1.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        btnSchedule.setOnClickListener(v -> {
            btnSchedule.setBackgroundColor(0xFFC62828);
            btnSchedule.setTextColor(0xFFFFFFFF);
            startActivity(new Intent(MainActivity1.this, ScheduleActivity.class));
        });
        courseList.add(new Course("M1100", "Aline Hosri", 6, "wed", "2:00pm"));
        courseList.add(new Course("M1101", "Ghada Khoury", 6, "mon", "3:00pm"));
        courseList.add(new Course("P1100", "Antoine Choueiry", 6, "tue", "1:00pm"));
        courseList.add(new Course("P1101", "Elias Rizk", 6, "mon", "6:00pm"));
        courseList.add(new Course("S1100", "Khalil Bittar", 3, "fri", "12:00pm"));
            adapter = new CourseAdapter(courseList, course -> {
                CourseDetailDialog.show(MainActivity1.this, course);
            });
        recyclerView.setAdapter(adapter);
    }


}
