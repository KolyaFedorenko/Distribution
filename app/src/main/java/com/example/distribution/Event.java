package com.example.distribution;

public class Event {

    private String eventName;
    private String eventDescription;
    private String eventWorkers;

    public Event(String eventName, String eventDescription, String eventWorkers) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventWorkers = eventWorkers;
    }

    public Event() { }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventWorkers() {
        return eventWorkers;
    }

    public void setEventWorkers(String eventWorkers) {
        this.eventWorkers = eventWorkers;
    }

}
