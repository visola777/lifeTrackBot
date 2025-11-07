package org.example;

import java.util.Date;

public class Task {
    private final int id;           // Har user uchun alohida
    private final String text;
    private boolean done;
    private final Date created;

    public Task(int id, String text) {
        this.id = id;
        this.text = text;
        this.done = false;
        this.created = new Date();
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public Date getCreated() { return created; }
}

