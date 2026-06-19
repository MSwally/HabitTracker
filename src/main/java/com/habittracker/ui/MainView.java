package com.habittracker.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainView {
    
    private Stage stage;

    public MainView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Placeholder content
        Label placeholder = new Label("Habit Tracker Main View");
        root.setCenter(placeholder);

        // content of the window
        Scene scene = new Scene(root, 800, 600);

        // configure stage (window)
        stage.setTitle("Habit Tracker");
        stage.setScene(scene);
        stage.show();
    }
}
