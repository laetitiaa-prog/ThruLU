package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActiviy8 extends AppCompatActivity {

    EditText user, password;
    Spinner year, major, spinnerRole;   // ✅ added spinnerRole
    Button login, back;
    FirebaseAuth auth;
    FirebaseFirestore db;

    private String selectedRole = "Student"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);


        // ── Existing bindings ─────────────────────────────────────────────────
        user     = findViewById(R.id.user);
        password = findViewById(R.id.password);
        login    = findViewById(R.id.register);
        major    = findViewById(R.id.major);
        back     = findViewById(R.id.back);
        auth     = FirebaseAuth.getInstance();
        db       = FirebaseFirestore.getInstance();

        // ── Role spinner ✅ ───────────────────────────────────────────────────
        spinnerRole = findViewById(R.id.spinnerRole);
        ArrayList<String> roles = new ArrayList<>();
        roles.add("Student");
        roles.add("Teacher");
        roles.add("Administrator");
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ── Existing major spinner (unchanged) ───────────────────────────────
        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Majors");
        arrayList.add("Computer Science");
        arrayList.add("Maths");
        arrayList.add("Physics");
        arrayList.add("Biology");
        arrayList.add("Chemistry");
        ArrayAdapter<String> Adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, arrayList);
        Adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        major.setAdapter(Adapter);

        // ── Login button ──────────────────────────────────────────────────────
        login.setOnClickListener(v -> {
            String username = user.getText().toString().trim();
            String pass     = password.getText().toString().trim();

            if (!username.endsWith("@ul.edu.lb")) {
                Toast.makeText(this, "Use your university email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(username, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();
                            switch (selectedRole) {
                                case "Student":       loginAsStudent(uid, username);       break;
                                case "Teacher":       loginAsTeacher(uid, username);       break;
                                case "Administrator": loginAsAdmin(uid, username);         break;
                            }
                        } else {
                            Exception e = task.getException();
                            Toast.makeText(this,
                                    "Login failed: " + (e != null ? e.getMessage() : "unknown error"),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // ── Back button (unchanged) ───────────────────────────────────────────
        back.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
    }

    // ── Student login (original logic, unchanged) ─────────────────────────────
    private void loginAsStudent(String uid, String username) {
        db.collection("students").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Boolean isActive = document.getBoolean("isActive");
                        if (isActive != null && isActive) {
                            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
                            SessionManager.getInstance().login(new User(
                                    uid,
                                    document.getString("name") != null
                                            ? document.getString("name") : username,
                                    username,
                                    UserRole.STUDENT,
                                    ""
                            ));
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Not an active student", Toast.LENGTH_LONG).show();
                            auth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Student data not found", Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }
                });
    }

    // ── Teacher login ✅ ──────────────────────────────────────────────────────
    private void loginAsTeacher(String uid, String username) {
        db.collection("teacher").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Boolean isActive = document.getBoolean("isActive");
                        if (isActive != null && isActive) {
                            Toast.makeText(this, "Welcome, Teacher!", Toast.LENGTH_SHORT).show();
                            SessionManager.getInstance().login(new User(
                                    uid,
                                    document.getString("name") != null
                                            ? document.getString("name") : username,
                                    username,
                                    UserRole.TEACHER,
                                    document.getString("teacherName") != null
                                            ? document.getString("teacherName") : ""
                            ));
                            startActivity(new Intent(this,MainActivity3.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Not an active teacher", Toast.LENGTH_LONG).show();
                            auth.signOut();
                        }
                    } else {
                        Toast.makeText(this, "Teacher data not found", Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }
                });
    }

    // ── Admin login ✅ ────────────────────────────────────────────────────────
    private void loginAsAdmin(String uid, String username) {
        db.collection("administrator").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
                        SessionManager.getInstance().login(new User(
                                uid,
                                document.getString("name") != null
                                        ? document.getString("name") : username,
                                username,
                                UserRole.ADMIN,
                                ""
                        ));
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Admin data not found", Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }
                });
    }
}