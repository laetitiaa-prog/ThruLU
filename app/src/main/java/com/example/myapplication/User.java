package com.example.myapplication;

public class User {
    private String id;
    private String name;
    private String email;
    private UserRole role;
    /** For teachers: their display name used to match reviews */
    private String teacherName;

    public User(String id, String name, String email, UserRole role, String teacherName) {
        this.id          = id;
        this.name        = name;
        this.email       = email;
        this.role        = role;
        this.teacherName = teacherName;
    }

    public User(String id, String name, String email, UserRole role) {
        this(id, name, email, role, "");
    }

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public UserRole getRole()      { return role; }
    public String getTeacherName() { return teacherName; }
}