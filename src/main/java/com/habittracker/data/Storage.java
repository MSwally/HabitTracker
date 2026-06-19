package com.habittracker.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.habittracker.model.Habit;
import com.habittracker.model.Completion;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    
    // Paths to json files
    private static final String DATA_DIR = "data";
    private static final String HABITS_FILE = DATA_DIR + "/habits.json";
    private static final String COMPLETIONS_FILE = DATA_DIR + "/completions.json";

    // Gson instance for JSON serialization/deserialization
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // HABITS
    public static List<Habit> loadHabits() {
        return loadFromFile(HABITS_FILE, new TypeToken<List<Habit>>(){}.getType());
    }

    public static void saveHabits(List<Habit> habits) {
        saveToFile(HABITS_FILE, habits);
    }

    // COMPLETIONS
    public static List<Completion> loadCompletions() {
        return loadFromFile(COMPLETIONS_FILE, new TypeToken<List<Completion>>(){}.getType());
    }

    public static void saveCompletions(List<Completion> completions) {
        saveToFile(COMPLETIONS_FILE, completions);
    }

    // Helper functions for loading and saving files
    private static <T> List<T> loadFromFile(String filePath, Type type) {
        try {
            Path path = Paths.get(filePath);

            // file doesn't exist yet, return empty list
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }

            // read file and convert to java object
            Reader reader = new FileReader(filePath);
            List<T> result = gson.fromJson(reader, type);
            reader.close();

            // ensure file is not empty
            return result != null ? result : new ArrayList<>();

        } catch (IOException e) {
            System.err.println("Error loading data from " + filePath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static void saveToFile(String filePath, Object data) {
        try {
            // ensure data directory exists
            Files.createDirectories(Paths.get(DATA_DIR));

            // convert java object to json and write to file
            Writer writer = new FileWriter(filePath);
            gson.toJson(data, writer);
            writer.close();

        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath);
            e.printStackTrace();
        }
    }
}
