package com.habittracker.model;

import java.util.UUID;

public class Habit {
    private String id;
    private String name;
    private String frequency;

    // Constructors - called when creating a new instance of the Habit class
    public Habit(String name, String frequency) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.frequency = frequency;
    }

    // Empty Constructor for Gson
    public Habit() {}

    // Getters for external reading
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFrequency() {
        return frequency;
    }

    // Setters for external writing
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
