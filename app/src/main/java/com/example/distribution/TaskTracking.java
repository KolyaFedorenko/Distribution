package com.example.distribution;

public class TaskTracking {

    public TaskTracking(int issued, int seen, int completed) {
        this.issued = issued;
        this.seen = seen;
        this.completed = completed;
    }

    public int issued;
    public int seen;
    public int completed;

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
