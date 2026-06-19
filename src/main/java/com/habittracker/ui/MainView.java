package com.habittracker.ui;

import com.habittracker.data.Storage;
import com.habittracker.model.Habit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainView {
    
    private Stage stage;

    // observeable list to update ListView automatically when data changes
    private ObservableList<Habit> habits;

    public MainView(Stage stage) {
        this.stage = stage;

        // Get habits from json and convert to observable list for javafx
        habits = FXCollections.observableArrayList(Storage.loadHabits());
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Input area on top
        root.setTop(buildInputArea());

        // Show habit list in the center
        root.setCenter(buildHabitList());

        // content of the window
        Scene scene = new Scene(root, 800, 600);

        // configure stage (window)
        stage.setTitle("Habit Tracker");
        stage.setScene(scene);
        stage.show();
    }

    // Define the input area for adding new habits
    private VBox buildInputArea() {
        // text field for habit name
        TextField nameField = new TextField();
        nameField.setPromptText("Enter habit name");

        // dropdown for habit frequency
        ComboBox<String> frequencyBox = new ComboBox<>();
        frequencyBox.getItems().addAll("daily", "weekly", "monthly");
        frequencyBox.setValue("daily");

        // button to add habit
        Button addButton = new Button("+ Add Habit");
        addButton.setOnAction(e -> addHabit(nameField, frequencyBox));

        // horizontal layout to show elements in a row
        HBox inputRow = new HBox(10, nameField, frequencyBox, addButton);
        inputRow.setPadding(new Insets(0, 0, 10, 0));

        return new VBox(inputRow);
    }

    // define habit list
    private ListView<Habit> buildHabitList() {
        ListView<Habit> listView = new ListView<>(habits);

        // define how each item in the list should look
        listView.setCellFactory(lv -> new ListCell<Habit>() {
            @Override
            protected void updateItem(Habit habit, boolean empty) {
                super.updateItem(habit, empty);

                if (empty || habit == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // label to show habit name and frequency
                    Label nameLabel = new Label(habit.getName() + " (" + habit.getFrequency() + ")");

                    // delete button to remove habit
                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(e -> deleteHabit(habit));

                    HBox row = new HBox(nameLabel);
                    row.setSpacing(10);
                    HBox.setHgrow(nameLabel, Priority.ALWAYS);
                    row.getChildren().add(deleteButton);

                    setGraphic(row);
                }
            }
        });

        return listView;
    }

    // actions to add and delete habits
    private void addHabit(TextField nameField, ComboBox<String> frequencyBox) {
        String name = nameField.getText().trim();

        // ignore empty names
        if (name.isEmpty()) return;

        // create new habit and add to observable list
        Habit habit = new Habit(name, frequencyBox.getValue());
        habits.add(habit);

        // save updated habits to json
        Storage.saveHabits(habits);

        // empty text field for next input
        nameField.clear();
    }

    private void deleteHabit(Habit habit) {
        // remove habit from observable list
        habits.remove(habit);

        // save updated habits to json
        Storage.saveHabits(habits);
    }
}
