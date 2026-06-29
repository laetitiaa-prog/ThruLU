package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity4 extends AppCompatActivity {

    CardView registration, campus, infobot, reviews;
    Button logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);

        registration = findViewById(R.id.registration);
        campus = findViewById(R.id.campus);
        infobot = findViewById(R.id.infobot);
        reviews = findViewById(R.id.reviews);
        logIn = findViewById(R.id.logIn);

        // Registration Papers Page
        registration.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, RegistrationActivity.class);
            startActivity(intent);
        });

        campus.setOnClickListener(v->{
            Intent intent = new Intent (MainActivity4.this, MainActivity6.class);
            startActivity(intent);
        });

        // InfoBot Page
        infobot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, MainActivity5.class);
            startActivity(intent);
        });

        // Reviews Page
        reviews.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, MainActivity3.class);
            startActivity(intent);
        });

        // Login Page
        logIn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, MainActiviy8.class);
            startActivity(intent);
        });
    }
}