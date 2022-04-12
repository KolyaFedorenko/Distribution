package com.example.distribution;

public class Event {

    private String eventName;
    private String eventDescription;
    private String eventWorkers;
    private String eventDate;

    public Event(String eventName, String eventDescription, String eventWorkers, String eventDate) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventWorkers = eventWorkers;
        this.eventDate = eventDate;
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

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }


}
