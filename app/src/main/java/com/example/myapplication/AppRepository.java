package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppRepository {

    private static AppRepository instance;

    private final List<Course> courses = new ArrayList<>();
    private final List<ScheduleEvent> scheduleEvents = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    private AppRepository() {
        seedCourses();
        seedSchedule();
        seedReviews();
    }

    public static AppRepository getInstance() {
        if (instance == null) {
            instance = new AppRepository();
        }
        return instance;
    }

    // ─────────────────────────────────────────────
    // COURSES
    // ─────────────────────────────────────────────

    private void seedCourses() {

        courses.add(new Course("M1100", "Sarah Hassan", 3,"Mon/Wed","10:00AM"));
        courses.add(new Course("M1101", "Omar Khalil", 3,"Tue/Thu","1:00pm"));
        courses.add(new Course("P1100", "Lina Nassar", 3,"Mon/Fri","12:00AM"));
        courses.add(new Course("P1101", "Rami Azar", 3,"Wed/Thu","3:00pm"));
        courses.add(new Course("S2250", "Sarah Hassan", 3,"Wed/Thu","3:00pm"));
        courses.add(new Course("I1100", "Omar Khalil", 3,"Wed/Thu","3:00pm"));
        courses.add(new Course("CS350", "Lina Nassar", 3,"Wed/Thu","3:00pm"));
        courses.add(new Course("CS460", "Rami Azar", 3,"Wed/Thu","3:00pm"));
    }

    // ─────────────────────────────────────────────
    // SCHEDULE
    // ─────────────────────────────────────────────

    private void seedSchedule() {

        scheduleEvents.add(new ScheduleEvent(
                "e1",
                "Fall Semester Begins",
                "",
                "2024-09-02",
                "2024-09-02",
                EventType.OTHER));

        scheduleEvents.add(new ScheduleEvent(
                "e2",
                "Last Day to Add/Drop",
                "",
                "2024-09-13",
                "2024-09-13",
                EventType.REGISTRATION));

        scheduleEvents.add(new ScheduleEvent(
                "e3",
                "Independence Day Holiday",
                "",
                "2024-11-22",
                "2024-11-22",
                EventType.HOLIDAY));

        scheduleEvents.add(new ScheduleEvent(
                "e4",
                "Fall Midterm Exams",
                "Week-long midterm period",
                "2024-10-21",
                "2024-10-25",
                EventType.MIDTERM));

        scheduleEvents.add(new ScheduleEvent(
                "e5",
                "Christmas Break",
                "",
                "2024-12-23",
                "2025-01-03",
                EventType.BREAK));
    }

    // ─────────────────────────────────────────────
    // REVIEWS
    // ─────────────────────────────────────────────

    private void seedReviews() {
        reviews.add(new Review(
                newId(), "u1", "Alice Smith",
                "Dr. Sarah Hassan", "CS101",
                4.5f, "Very clear explanations. Highly recommend!",
                false, "Academic"
        ));
        reviews.add(new Review(
                newId(), "u2", "Bob Jones",
                "Dr. Omar Khalil", "CS201",
                5f, "Best professor I've had. Makes complex topics easy.",
                false, "Academic"
        ));
        reviews.add(new Review(
                newId(), "u3", "Carol Davis",
                "Prof. Lina Nassar", "MATH101",
                3.5f, "Good course but the pace is a bit fast.",
                false, "Academic"
        ));
        reviews.add(new Review(
                newId(), "u4", "David Lee",
                "Dr. Rami Azar", "EE210",
                4f, "Interesting material, fair grading.",
                false, "Academic"
        ));
    }
    // ─────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────

    public List<Course> getCourses() {
        return courses;
    }

    public List<ScheduleEvent> getScheduleEvents() {
        return scheduleEvents;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    // ─────────────────────────────────────────────
    // SCHEDULE CRUD
    // ─────────────────────────────────────────────

    public void addScheduleEvent(ScheduleEvent event) {
        scheduleEvents.add(event);
    }

    public void updateScheduleEvent(ScheduleEvent updated) {

        for (int i = 0; i < scheduleEvents.size(); i++) {

            if (scheduleEvents.get(i).getId().equals(updated.getId())) {

                scheduleEvents.set(i, updated);
                return;
            }
        }
    }

    public void deleteScheduleEvent(String id) {

        scheduleEvents.removeIf(
                e -> e.getId().equals(id)
        );
    }

    // ─────────────────────────────────────────────
    // REVIEW CRUD
    // ─────────────────────────────────────────────

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void setReviewHidden(String id, boolean hidden) {

        for (Review r : reviews) {

            if (r.getId() != null &&
                    r.getId().equals(id)) {

                r.setHidden(hidden);
                return;
            }
        }
    }

    public void deleteReview(String id) {

        reviews.removeIf(
                r -> r.getId() != null &&
                        r.getId().equals(id)
        );
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────

    public String newId() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }
}