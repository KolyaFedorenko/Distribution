package com.example.distribution;

public class TaskTracking {

    private int issued;
    private int seen;
    private int completed;

    public TaskTracking(int issued, int seen, int completed) {
        this.issued = issued;
        this.seen = seen;
        this.completed = completed;
    }

    public TaskTracking() {}

    public int getIssued() {
        return issued;
    }

    public void setIssued(int issued) {
        this.issued = issued;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

}
