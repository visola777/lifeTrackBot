package org.example;

public class Task {
    private String text;
    private boolean done;
    private long createdAt;

    public Task(String text) {
        this.text = text;
        this.done = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return (done ? "✅ " : "❌ ") + text;
    }
}