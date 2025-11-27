package com.ualberta.eventlottery.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.time.LocalTime;

public class Event {
    private String id;
    private String organizerId;
    private String title;
    private String description;
    private String category;
    private double price;
    private LocalTime dailyStartTime;
    private LocalTime dailyEndTime;
    private int sessionDuration;
    private Date createdAt;
    private Date startTime;
    private Date endTime;
    private Date registrationStart;
    private Date registrationEnd;
    private String location;
    private boolean locationRequired;
    private String posterUrl;
    private String qrCodeUrl;
    private String locationUrl;
    private EventStatus eventStatus;
    private EventRegistrationStatus eventRegistrationStatus;

    private int maxAttendees;
    private int maxWaitListSize;
    private int currentWaitListSize;
    private int confirmedAttendees;

    // Constructors
    public Event() {
        this.createdAt = new Date();
        this.eventRegistrationStatus = EventRegistrationStatus.REGISTRATION_OPEN;
        this.currentWaitListSize = 0;
        this.confirmedAttendees = 0;
    }

    public Event(String id, String organizerId, String title, String description) {
        this();
        this.id = id;
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalTime getDailyStartTime() { return dailyStartTime; }
    public void setDailyStartTime(LocalTime dailyStartTime) { this.dailyStartTime = dailyStartTime; }
    public void setDailyStartTime(int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.dailyStartTime = LocalTime.of(hour, minute);
        }
    };
    public LocalTime getDailyEndTime() { return dailyEndTime; }

    public void setDailyEndTime(LocalTime dailyEndTime) { this.dailyEndTime = dailyEndTime; }

    public void setDailyEndTime(int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.dailyEndTime = LocalTime.of(hour, minute);
        }
    }

    public int getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(int sessionDuration) { this.sessionDuration = sessionDuration; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Date getRegistrationStart() { return registrationStart; }
    public void setRegistrationStart(Date registrationStart) { this.registrationStart = registrationStart; }

    public Date getRegistrationEnd() { return registrationEnd; }
    public void setRegistrationEnd(Date registrationEnd) { this.registrationEnd = registrationEnd; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isLocationRequired() { return locationRequired; }
    public void setLocationRequired(boolean locationRequired) { this.locationRequired = locationRequired; }

    public int getConfirmedAttendees() {
        return confirmedAttendees;
    }

    public void setConfirmedAttendees(int confirmedAttendees) {
        this.confirmedAttendees = confirmedAttendees;
    }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public EventStatus getEventStatus() { return eventStatus; }

    public EventRegistrationStatus getRegistrationStatus() {
        return eventRegistrationStatus;
    }

    public void setRegistrationStatus(EventRegistrationStatus eventRegistrationStatus) {
        this.eventRegistrationStatus = eventRegistrationStatus;
    }

    public void setEventStatus(EventStatus eventStatus) { this.eventStatus = eventStatus; }

    public int getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }

    public int getMaxWaitListSize() { return maxWaitListSize; }
    public void setMaxWaitListSize(int maxWaitListSize) { this.maxWaitListSize = maxWaitListSize; }

    public int getCurrentWaitListSize() { return currentWaitListSize; }
    public void setCurrentWaitListSize(int currentWaitListSize) { this.currentWaitListSize = currentWaitListSize; }

    // Business Logic Methods

    /**
     * Checks if registration is currently open based on current time
     * @return true if registration period is active
     */
    public boolean isRegistrationOpen() {
        Date now = new Date();
        return now.after(registrationStart) && now.before(registrationEnd) &&
                eventRegistrationStatus == EventRegistrationStatus.REGISTRATION_OPEN;
    }

    /**
     * Updates the registration status based on the current time and deadline
     * This method should be called to ensure the registration status reflects the deadline
     */
    public void updateRegistrationStatusBasedOnDeadline() {
        Date now = new Date();
        if (getRegistrationEnd() != null && now.after(getRegistrationEnd())) {
            setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        }
    }

    /**
     * Checks if the waiting list has reached its maximum capacity
     * @return true if waiting list is full
     */
    public boolean isWaitingListFull() {
        return maxWaitListSize > 0 && currentWaitListSize >= maxWaitListSize;
    }

    /**
     * Checks if the event has reached maximum attendance capacity
     * @return true if event is full
     */
    public boolean isEventFull() {
        return confirmedAttendees >= maxAttendees;
    }
}
