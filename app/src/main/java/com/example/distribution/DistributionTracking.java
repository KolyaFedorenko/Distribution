package com.example.distribution;

public class DistributionTracking {

    public String taskTo;
    public String taskStatus;
    public String taskName;

    public DistributionTracking(String taskTo, String taskStatus, String taskName) {
        this.taskTo = taskTo;
        this.taskStatus = taskStatus;
        this.taskName = taskName;
    }

    public DistributionTracking() {}

    public String getTaskTo() {
        return taskTo;
    }

    public void setTaskTo(String taskTo) {
        this.taskTo = taskTo;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
