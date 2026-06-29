package com.example.myapplication;

public class Course {

    private String id;
    private String doctor;
    private int credits;
    private String days;
    private String time;

    public Course(String id, String doctor, int credits,String days,String time) {
        this.id = id;
        this.doctor = doctor;
        this.credits = credits;
        this.days=days;
        this.time=time;
    }

    public String getId() {
        return id;
    }

    public String getDoctor() {
        return doctor;
    }
    public String getDays(){
        return days;
    }
    public String getTime(){
        return time;
    }

    public int getCredits() {
        return credits;
    }
}