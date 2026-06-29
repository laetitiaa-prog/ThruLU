package com.example.myapplication;

public class Event {
    String title, location, time, type;

    public Event(String title, String location, String time, String type) {
        this.title = title;
        this.location = location;
        this.time = time;
        this.type = type; // "academic" or "leisure"
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public String getType() { return type; }
}

