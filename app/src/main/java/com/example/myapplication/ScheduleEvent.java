package com.example.myapplication;

public class ScheduleEvent {
    private String    id;
    private String    title;
    private String    description;
    private String    startDate;   // "YYYY-MM-DD"
    private String    endDate;     // "YYYY-MM-DD"
    private EventType type;

    public ScheduleEvent(String id, String title, String description,
                         String startDate, String endDate, EventType type) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.type        = type;
    }

    public String    getId()          { return id; }
    public String    getTitle()       { return title; }
    public String    getDescription() { return description; }
    public String    getStartDate()   { return startDate; }
    public String    getEndDate()     { return endDate; }
    public EventType getType()        { return type; }

    // Setters used by admin edit
    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStartDate(String startDate)     { this.startDate = startDate; }
    public void setEndDate(String endDate)         { this.endDate = endDate; }
    public void setType(EventType type)            { this.type = type; }
}