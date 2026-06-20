package com.habittracker.model;

import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class HabitStatus {
    
    // HabitStatus can have three states
    public enum Status {
        DONE,
        PENDING,
        MISSED
    }

    // check if habit was completed on a specific day
    public static boolean isCompletedOnDay(Habit habit, LocalDate day, List<Completion> completions) {
        return completions.stream()
                .filter(c -> c.getHabitId().equals(habit.getId()))
                .anyMatch(c -> c.getDateTime().toLocalDate().equals(day));
    }

    // check status for a specific day
    public static Status getStatus(Habit habit, LocalDate day, List<Completion> completions, LocalDate today) {
        return switch (habit.getFrequency()) {
            case "daily" -> getDailyStatus(habit, day, completions, today);
            case "weekly" -> getWeeklyStatus(habit, day, completions, today);
            case "monthly" -> getMonthlyStatus(habit, day, completions, today);
            default -> Status.PENDING;
        };
    }

    // Get daily status
    private static Status getDailyStatus(Habit habit, LocalDate day, List<Completion> completions, LocalDate today) {
        boolean done = isCompletedOnDay(habit, day, completions);;

        if (done) return Status.DONE;
        if (day.isBefore(today)) return Status.MISSED;
        return Status.PENDING;
    }

    // Get weekly status
    private static Status getWeeklyStatus(Habit habit, LocalDate day, List<Completion> completions, LocalDate today) {
        // evaluate the week of the year for the given day
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int dayWeek = day.get(weekFields.weekOfWeekBasedYear());
        int dayYear = day.getYear();

        // evaluate if habit was completed in the same week
        boolean doneThisWeek = completions.stream()
                .filter(c -> c.getHabitId().equals(habit.getId()))
                .anyMatch(c -> {
                    LocalDate cDate = c.getDateTime().toLocalDate();
                    return cDate.get(weekFields.weekOfWeekBasedYear()) == dayWeek && cDate.getYear() == dayYear;
                });
        if (doneThisWeek) return Status.DONE;

        // check if week is already over
        LocalDate endOfWeek = day.with(weekFields.dayOfWeek(), 7);
        if (endOfWeek.isBefore(today)) return Status.MISSED;
        else return Status.PENDING;
    }

    // Get monthly status
    private static Status getMonthlyStatus(Habit habit, LocalDate day, List<Completion> completions, LocalDate today) {
        // evaluate if habit was completed in the same month
        boolean doneThisMonth = completions.stream()
                .filter(c -> c.getHabitId().equals(habit.getId()))
                .anyMatch(c -> {
                    LocalDate cDate = c.getDateTime().toLocalDate();
                    return cDate.getMonth() == day.getMonth() && cDate.getYear() == day.getYear();
                });

        if (doneThisMonth) return Status.DONE;

        // check if month is already over
        if (day.getMonth() != today.getMonth() || day.getYear() != today.getYear()) {
            return Status.MISSED;
        } else {
            return Status.PENDING;
        }
    }
}
