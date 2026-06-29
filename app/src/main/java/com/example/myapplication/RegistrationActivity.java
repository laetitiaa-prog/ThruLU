package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

    public class RegistrationActivity extends AppCompatActivity {
        Button back;


        @SuppressLint("SuspiciousIndentation")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.registration_main);
            back=findViewById(R.id.back);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RegistrationActivity.this,MainActivity4.class);
                    startActivity(intent);

                }
            });
        }
    }
