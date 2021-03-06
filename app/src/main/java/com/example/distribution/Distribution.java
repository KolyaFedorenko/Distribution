package com.example.distribution;

public class Distribution {

    private String taskName;
    private String taskDescription;
    private String taskExpirationDate;
    private String taskExpirationTime;
    private String taskWorker;

    public Distribution(String taskName, String taskDescription, String taskExpirationDate, String taskExpirationTime, String worker) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskExpirationDate = taskExpirationDate;
        this.taskExpirationTime = taskExpirationTime;
        this.taskWorker = worker;
    }

    public Distribution(){}

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskExpirationDate() {
        return taskExpirationDate;
    }

    public void setTaskExpirationDate(String taskExpirationDate) {
        this.taskExpirationDate = taskExpirationDate;
    }

    public String getTaskExpirationTime() {
        return taskExpirationTime;
    }

    public void setTaskExpirationTime(String taskExpirationTime) {
        this.taskExpirationTime = taskExpirationTime;
    }

    public String getTaskWorker() {
        return taskWorker;
    }

    public void setTaskWorker(String taskWorker) {
        this.taskWorker = taskWorker;
    }

}
