package com.example.distribution;

public class Distribution {

    public String taskName;
    public String taskDescription;
    public String taskExpirationDate;
    public String taskExpirationTime;
    public String taskWorker;

    public Distribution(String taskName, String taskDescription, String taskExpirationDate, String taskExpirationTime, String worker) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskExpirationDate = taskExpirationDate;
        this.taskExpirationTime = taskExpirationTime;
        this.taskWorker = worker;
    }
}
