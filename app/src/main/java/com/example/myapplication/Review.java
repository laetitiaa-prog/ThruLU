package com.example.myapplication;

public class Review {

    private String id;
    private String authorId;
    private String authorName;
    private String teacherName;
    private String courseName;
    private float rating;
    private String comment;
    private long timestamp;
    private boolean hidden;
    private String category;

    // Empty constructor required for Firebase
    public Review() {
    }

    // Main constructor
    public Review(String teacherName,
                  String courseName,
                  float rating,
                  String comment,
                  String category) {

        this.teacherName = teacherName;
        this.courseName = courseName;
        this.rating = rating;
        this.comment = comment;
        this.category = category;

        this.timestamp = System.currentTimeMillis();

        this.authorId = "";
        this.authorName = "";
        this.hidden = false;
    }

    // Full constructor (optional)
    public Review(String id,
                  String authorId,
                  String authorName,
                  String teacherName,
                  String courseName,
                  float rating,
                  String comment,
                  boolean hidden,
                  String category) {

        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.teacherName = teacherName;
        this.courseName = courseName;
        this.rating = rating;
        this.comment = comment;
        this.hidden = hidden;
        this.category = category;

        this.timestamp = System.currentTimeMillis();
    }

    // ─── Getters ─────────────────────────────

    public String getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getCategory() {
        return category;
    }

    // ─── Setters ─────────────────────────────

    public void setId(String id) {
        this.id = id;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}