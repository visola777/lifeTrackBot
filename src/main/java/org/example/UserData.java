package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserData {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Note> notes = new ArrayList<>();
    private int nextTaskId = 1; // Har user uchun alohida ID

    public void addTask(String text) {
        tasks.add(new Task(nextTaskId++, text.trim()));
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public boolean markTaskDone(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id && !t.isDone())
                .findFirst()
                .map(t -> { t.setDone(true); return true; })
                .orElse(false);
    }

    public void addNote(String text) {
        notes.add(new Note(text));
    }

    public List<Note> getNotes() {
        return new ArrayList<>(notes);
    }

    public List<Task> getDoneTasks() {
        return tasks.stream().filter(Task::isDone).collect(Collectors.toList());
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
}
