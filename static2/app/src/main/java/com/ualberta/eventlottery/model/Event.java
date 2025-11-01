package com.ualberta.eventlottery.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalTime;

public class Event {
    private String id;
    private String organizerId;  // Fixed typo from "orgarserId"
    private String title;
    private String description;
    private String category;
    private double price;
    private LocalTime dailyStartTime;  // For recurring events
    private int sessionDuration;        // Duration in minutes
    private Date createdAt;
    private Date startTime;
    private Date endTime;
    private Date registrationStart;
    private Date registrationEnd;
    private String location;
    private boolean locationRequired;
    private String posterUrl;
    private String qrCodeUrl;
    private EventStatus status;
    private int maxAttendees;
    private int maxWaitListSize;
    private int currentWaitListSize;

    // User ID lists - Consider separating these into a repository
    private List<String> registeredUserIds;      // All users who registered interest
    private List<String> waitListUserIds;        // Users in waiting list
    private List<String> confirmedUserIds;       // Users who confirmed attendance
    private List<String> attendeeIds;            // Users who actually attended

    // Constructors
    public Event() {
        this.registeredUserIds = new ArrayList<>();
        this.waitListUserIds = new ArrayList<>();
        this.confirmedUserIds = new ArrayList<>();
        this.attendeeIds = new ArrayList<>();
        this.createdAt = new Date();
        this.status = EventStatus.REGISTRATION_OPEN;
        this.currentWaitListSize = 0;
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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalTime getDailyStartTime() { return dailyStartTime; }
    public void setDailyStartTime(LocalTime dailyStartTime) { this.dailyStartTime = dailyStartTime; }

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

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public int getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }

    public int getMaxWaitListSize() { return maxWaitListSize; }
    public void setMaxWaitListSize(int maxWaitListSize) { this.maxWaitListSize = maxWaitListSize; }

    public int getCurrentWaitListSize() { return currentWaitListSize; }
    public void setCurrentWaitListSize(int currentWaitListSize) { this.currentWaitListSize = currentWaitListSize; }

    // List getters (return copies to prevent external modification)
    public List<String> getRegisteredUserIds() { return new ArrayList<>(registeredUserIds); }
    public List<String> getWaitListUserIds() { return new ArrayList<>(waitListUserIds); }
    public List<String> getConfirmedUserIds() { return new ArrayList<>(confirmedUserIds); }
    public List<String> getAttendeeIds() { return new ArrayList<>(attendeeIds); }

    // Business Logic Methods

    /**
     * Checks if registration is currently open based on current time
     * @return true if registration period is active
     */
    public boolean isRegistrationOpen() {
        Date now = new Date();
        return now.after(registrationStart) && now.before(registrationEnd) &&
                status == EventStatus.REGISTRATION_OPEN;
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
        return confirmedUserIds.size() >= maxAttendees;
    }

    /**
     * Adds a user to the waiting list
     * @param userId The ID of the user to add
     * @return true if user was added successfully
     */
    public boolean addToWaitingList(String userId) {
        if (userId == null || userId.trim().isEmpty() ||
                waitListUserIds.contains(userId) ||
                !isRegistrationOpen() ||
                isWaitingListFull()) {
            return false;
        }

        boolean added = waitListUserIds.add(userId.trim());
        if (added) {
            currentWaitListSize++;
            // Also add to registered users if not already there
            if (!registeredUserIds.contains(userId)) {
                registeredUserIds.add(userId);
            }
        }
        return added;
    }

    /**
     * Removes a user from the waiting list
     * @param userId The ID of the user to remove
     * @return true if user was removed successfully
     */
    public boolean removeFromWaitingList(String userId) {
        boolean removed = waitListUserIds.remove(userId);
        if (removed) {
            currentWaitListSize = Math.max(0, currentWaitListSize - 1);
        }
        return removed;
    }

    /**
     * Randomly draws participants from waiting list to confirmed list
     * Note: This is a simplified version - actual implementation would be more complex
     */
    public void drawParticipants() {
        if (!isRegistrationOpen() || waitListUserIds.isEmpty()) {
            return;
        }

        int availableSpots = maxAttendees - confirmedUserIds.size();
        int participantsToDraw = Math.min(availableSpots, waitListUserIds.size());

        // Simplified random selection - in reality, you'd use proper randomization
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < participantsToDraw && i < waitListUserIds.size(); i++) {
            selected.add(waitListUserIds.get(i));
        }

        // Move selected users to confirmed list and remove from waitlist
        confirmedUserIds.addAll(selected);
        waitListUserIds.removeAll(selected);
        currentWaitListSize = waitListUserIds.size();
    }

    /**
     * Confirms a user's attendance (when they accept the invitation)
     * @param userId The ID of the user to confirm
     * @return true if user was confirmed successfully
     */
    public boolean confirmUser(String userId) {
        if (userId == null || userId.trim().isEmpty() ||
                !waitListUserIds.contains(userId) ||
                isEventFull()) {
            return false;
        }

        // Move from waitlist to confirmed
        boolean removedFromWaitlist = waitListUserIds.remove(userId);
        if (removedFromWaitlist) {
            currentWaitListSize--;
            return confirmedUserIds.add(userId);
        }
        return false;
    }

    /**
     * Marks a user as attended the event
     * @param userId The ID of the user who attended
     * @return true if user was marked as attended
     */
    public boolean addAttendance(String userId) {
        if (userId == null || userId.trim().isEmpty() ||
                !confirmedUserIds.contains(userId) ||
                attendeeIds.contains(userId)) {
            return false;
        }
        return attendeeIds.add(userId.trim());
    }

    /**
     * Removes a user's attendance record
     * @param userId The ID of the user to remove
     * @return true if attendance record was removed
     */
    public boolean removeAttendance(String userId) {
        return attendeeIds.remove(userId);
    }

    // Utility Methods
    public int getAvailableSpots() {
        return maxAttendees - confirmedUserIds.size();
    }

    public int getWaitListCount() {
        return waitListUserIds.size();
    }

    public int getConfirmedCount() {
        return confirmedUserIds.size();
    }

    public int getAttendanceCount() {
        return attendeeIds.size();
    }
}