package com.habittracker.ui;

import com.habittracker.data.Storage;
import com.habittracker.model.Completion;
import com.habittracker.model.Habit;
import com.habittracker.model.HabitStatus;
import com.habittracker.model.HabitStatus.Status;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class MainView {
    
    private Stage stage;
    private ObservableList<Habit> habits; // observable list to update ListView automatically when data changes
    private List<Completion> completions;
    private GridPane weekGrid;
    private DatePicker datePicker;

    public MainView(Stage stage) {
        this.stage = stage;

        // Get habits from json and convert to observable list for javafx
        habits = FXCollections.observableArrayList(Storage.loadHabits());

        completions = Storage.loadCompletions();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Input area on top
        root.setTop(buildInputArea());

        // Show week grid in the center
        root.setCenter(buildWeekGrid());

        // Rerender grid when habits change
        habits.addListener((ListChangeListener<Habit>) change -> refreshGrid());

        // content of the window
        Scene scene = new Scene(root, 900, 500);

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

        // spacer to fill space between input fields, buttons and the date picker on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // DatePicker to simulate specific dates for testing
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPromptText("Select a date");
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> refreshGrid());
        Label testLabel = new Label("Test Date:");

        // horizontal layout to show elements in a row
        HBox inputRow = new HBox(10, nameField, frequencyBox, addButton, spacer, testLabel, datePicker);
        inputRow.setPadding(new Insets(0, 0, 15, 0));

        return new VBox(inputRow);
    }

    // define week grid
    private GridPane buildWeekGrid() {
        weekGrid = new GridPane();
        weekGrid.setHgap(8);
        weekGrid.setVgap(8);

        fillGrid();
        return weekGrid;
    }

    private void fillGrid() {
        weekGrid.getChildren().clear();

        LocalDate today = getToday();

        // calculate monday
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        // create columns for each day of the week while column 0 is reserved for habit names
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);

            // get names of the days
            String dayName = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());

            // get date of the day
            String dateStr = String.format("%02d.%02d", day.getDayOfMonth(), day.getMonthValue());

            // design label for each column header
            Label dayLabel = new Label(dayName + "\n" + dateStr);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setStyle("-fx-font-weith: bold;");

            // highlight today's column
            if (day.equals(today)) {
                dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3;");
            }

            weekGrid.add(dayLabel, i + 1, 0);
        }

        // create habit rows
        for (int row = 0; row < habits.size(); row++) {
            Habit habit = habits.get(row);

            Label nameLabel = new Label(habit.getName());
            nameLabel.setMinWidth(120);

            // add delete button for each habit
            Button deleteButton = new Button("🗑");
            deleteButton.setOnAction(e -> deleteHabit(habit));

            HBox nameCell = new HBox(8, nameLabel, deleteButton);
            nameCell.setAlignment(Pos.CENTER_LEFT);
            weekGrid.add(nameCell, 0, row + 1);

            // colored status points for each day
            for (int col = 0; col < 7; col++) {
                LocalDate day = monday.plusDays(col);
                Status status = HabitStatus.getStatus(habit, day, completions, today);

                Circle circle = buildStatusCircle(status, habit, day, today);

                StackPane cell = new StackPane(circle);
                cell.setAlignment(Pos.CENTER);

                weekGrid.add(cell, col + 1, row + 1);
            }
        }
    }

    // build colored circles for habit status
    private Circle buildStatusCircle(Status status, Habit habit, LocalDate day, LocalDate today) {
        Circle circle = new Circle(18);

        // color depends on status
        circle.setFill(switch (status) {
            case DONE -> Color.web("#4CAF50");
            case PENDING -> Color.web("#FFC107");
            case MISSED -> Color.web("#F44336");
        });

        // ensure only missed and pending habits can be clicked to mark them as done
        if (!day.isAfter(today)) {
            circle.setStyle("-fx-cursor: hand;");
            circle.setOnMouseClicked(e -> toggleCompletion(habit, day));
        } else {
            circle.setFill(Color.web("#E0E0E0")); // future days are greyed out
        }

        return circle;
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
        // remove habit from completions list
        completions.removeIf(c -> c.getHabitId().equals(habit.getId()));
        Storage.saveCompletions(completions);

        // remove habit from observable list
        habits.remove(habit);

        // save updated habits to json
        Storage.saveHabits(habits);
    }

    // toggle completion status for a habit
    private void toggleCompletion(Habit habit, LocalDate day) {
        boolean alreadyDone = HabitStatus.isCompletedOnDay(habit, day, completions);

        if (alreadyDone) {
            // remove completion
            completions.removeIf( c ->
                    c.getHabitId().equals(habit.getId()) &&
                    c.getDateTime().toLocalDate().equals(day)
            );
        } else {
            // add new completion
            LocalDateTime dateTime = day.equals(getToday()) ? LocalDateTime.now() : day.atTime(12,0);
            completions.add(new Completion(habit.getId(), dateTime));
        }

        Storage.saveCompletions(completions);
        refreshGrid();
    }

    private void refreshGrid() {
        fillGrid();
    }

    private LocalDate getToday() {
        LocalDate selectedDate = datePicker.getValue();
        return selectedDate != null ? selectedDate : LocalDate.now();
    }
}
