package com.example.myapplication;


import android.content.Context;
import android.content.SharedPreferences;

public class RegistrationManager {

    private static final String PREFS_NAME = "event_registrations";

    // Returns true if registered successfully, false if name already registered for this event
    public static boolean register(Context context, String eventId, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = eventId + "_" + name.toLowerCase().trim();

        if (prefs.contains(key)) {
            return false; // Already registered
        }

        prefs.edit().putBoolean(key, true).apply();
        return true;
    }

    public static boolean isRegistered(Context context, String eventId, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = eventId + "_" + name.toLowerCase().trim();
        return prefs.contains(key);
    }
}