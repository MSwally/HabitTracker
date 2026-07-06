# Habit Tracker
#### Video Demo: https://youtu.be/sH7BNJ6jZ88
#### Description:
Habit Tracker is a desktop application built with Java and JavaFX that helps you track recurring habits in your daily life. This project solves that by letting you define each habit with its own frequency: daily, weekly, or monthly.

## How to run the application

**Prerequisites:**
- JDK 21 or higher
- Apache Maven

**Steps:**
1. Clone the repository

2. Run the application:
```bash
    mvn javafx:run
```
Maven will automatically download JavaFX and all other dependencies on the first run. No manual SDK setup is required.

## Project structure

The project follows the standard Maven directory layout. All source files are located under `src/main/java/com/habittracker/` and are organized into three packages:

**`model/`**
- `Habit.java` — represents a single habit with an auto-generated UUID, a name, and a frequency string. The UUID ensures that two habits with the same name can still be told apart.
- `Completion.java` — represents a single completion event, storing the habit's ID and the exact date and time it was completed as a `LocalDateTime`. Using `LocalDateTime` instead of just `LocalDate` was a deliberate design choice: it makes the data model future-proof for features like tracking habits multiple times per day or displaying completion times.
- `HabitStatus.java` — contains all logic for determining the traffic light status of a habit on a given day. This logic is kept entirely separate from the UI so that it can be reasoned about and tested independently. The class uses a `Status` enum with three values (`DONE`, `PENDING`, `MISSED`) rather than plain strings, which prevents typos and makes the code easier to follow.

**`data/`**
- `Storage.java` — handles reading and writing JSON files using the Gson library. Habits are stored in `data/habits.json` and completions in `data/completions.json`. All methods are static because `Storage` holds no state of its own — it is purely a utility class. Using JSON instead of a full SQLite database was a deliberate choice to keep the setup simple: no additional driver configuration is needed, and the files are human-readable, which makes debugging straightforward.

**`ui/`**
- `MainView.java` — builds and manages the entire user interface. It uses a `BorderPane` as the root layout, with the input toolbar at the top and the weekly grid in the center. The grid is built using a `GridPane` where each cell contains a colored circle. The habits list is stored as an `ObservableList`, which means the UI automatically reacts to additions and deletions without needing to manually trigger a full rebuild every time.

**`Main.java`**
- The entry point of the application. It extends JavaFX's `Application` class and passes the primary `Stage` to `MainView`.