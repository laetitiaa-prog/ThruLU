package com.example.myapplication;

import com.example.myapplication.User;
import com.example.myapplication.UserRole;

/**
 * CHANGED CLASS: SessionManager
 *
 * Singleton holding the currently authenticated user.
 * Every fragment queries this to gate UI (submit button, moderation buttons, etc.)
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(User user)  { this.currentUser = user; }
    public void logout()          { this.currentUser = null; }

    public User    getCurrentUser() { return currentUser; }
    public boolean isLoggedIn()     { return currentUser != null; }
    public boolean isAdmin()        { return isLoggedIn() && currentUser.getRole() == UserRole.ADMIN; }
    public boolean isTeacher()      { return isLoggedIn() && currentUser.getRole() == UserRole.TEACHER; }

    /** True when the logged-in teacher is the subject of the given review */
    public boolean isReviewAboutMe(String teacherName) {
        return isTeacher() &&
                currentUser.getTeacherName().equalsIgnoreCase(teacherName);
    }
}