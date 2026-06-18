package com.habittracker.model;

import java.time.LocalDateTime;

public class Completion {
    private String habitId;
    private String dateTime;

    // Constructors - called when creating a new instance of the Completion class
    public Completion(String habitId, LocalDateTime dateTime) {
        this.habitId = habitId;
        this.dateTime = dateTime.toString();
    }

    // Empty Constructor for Gson
    public Completion() {}

    // Getters for external reading
    public String getHabitId() {
        return habitId;
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.parse(dateTime);
    }

    // Setters for external writing
    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
