package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity5 extends AppCompatActivity {

    Button  send,events,courses,campusmap,infobot,reviews,back;
    RecyclerView recyclerView;
    EditText editText;
    ArrayList<MessageModel> list;
    Adapterchat adapter;

    private final String user="user";
    private final String bot="bot";

    HashMap<String, String> qa=new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        back=findViewById(R.id.back);
        events=findViewById(R.id.events);
        courses=findViewById(R.id.courses);
        infobot=findViewById(R.id.infoBot);
        reviews=findViewById(R.id.reviews);
        campusmap=findViewById(R.id.campusmap);
        send=findViewById(R.id.send);
        recyclerView=findViewById(R.id.recyclerView);
        editText=findViewById(R.id.editText);
        list=new ArrayList<>();
        qa.put("exams", "Exams are held at the end of each semester");
        adapter=new Adapterchat(this,list);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity5.this,MainActivity4.class);
                startActivity(intent);
            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString().toLowerCase().trim();
                if (message.isEmpty()) return;
                list.add(new MessageModel(message, "user"));
                String reply = "I can't answer this question contact the head of department on 70 000 000.";
                for (String key : qa.keySet()) {
                    if (message.contains(key)) {
                        reply = qa.get(key);
                        break;
                    }
                }
                list.add(new MessageModel(reply, "bot"));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size() - 1);
                editText.setText("");
            }
        });


    }
}