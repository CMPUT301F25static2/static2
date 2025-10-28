package com.example.eventlotteryproject;

public class Event {
    private String title;
    private String eventDetails;
    private String timeFrame;
    private String eventStatus;

    public Event(String title, String eventDetails, String timeFrame, String eventStatus){
        this.title = title;
        this.eventDetails = eventDetails;
        this.timeFrame = timeFrame;
        this.eventStatus = eventStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }
}
