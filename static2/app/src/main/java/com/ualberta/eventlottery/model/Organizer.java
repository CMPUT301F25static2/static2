package com.ualberta.eventlottery.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Organizer extends User {
    private List<String> createdEventIds;
    private List<String> joinedEventIds;
    private String organizationName;
    private String contactInfo;

    public Organizer(String userId, String name, String email, String organizationName) {
        super(userId, name, email);
        this.organizationName = organizationName;
        this.createdEventIds = new ArrayList<>();
        this.joinedEventIds = new ArrayList<>();
        this.contactInfo = "";
    }

    // Getters
    public List<String> getCreatedEventIds() { return new ArrayList<>(createdEventIds); }
    public List<String> getJoinedEventIds() { return new ArrayList<>(joinedEventIds); }
    public String getOrganizationName() { return organizationName; }
    public String getContactInfo() { return contactInfo; }

    // Setters
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Adds an event to the organizer's created events list
     * @param eventId The ID of the event to add
     * @return true if added successfully, false if already exists
     */
    public boolean addCreatedEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty() || createdEventIds.contains(eventId)) {
            return false;
        }
        return createdEventIds.add(eventId.trim());
    }

    /**
     * Removes an event from the organizer's created events list
     * @param eventId The ID of the event to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeCreatedEvent(String eventId) {
        return createdEventIds.remove(eventId);
    }

    /**
     * Adds an event to the organizer's joined events list
     * @param eventId The ID of the event to add
     * @return true if added successfully, false if already exists
     */
    public boolean addJoinedEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty() || joinedEventIds.contains(eventId)) {
            return false;
        }
        return joinedEventIds.add(eventId.trim());
    }

    /**
     * Removes an event from the organizer's joined events list
     * @param eventId The ID of the event to remove
     * @return true if removed successfully, false if not found
     */
    public boolean removeJoinedEvent(String eventId) {
        return joinedEventIds.remove(eventId);
    }

    /**
     * Checks if the organizer is the creator of a specific event
     * @param eventId The event ID to check
     * @return true if organizer created this event, false otherwise
     */
    public boolean isEventOrganizer(String eventId) {
        return createdEventIds.contains(eventId);
    }

    /**
     * Creates a new event (this would typically interact with a service class)
     * @param title Event title
     * @param description Event description
     * @param startTime Event start time
     * @param endTime Event end time
     * @param registrationStart Registration period start
     * @param registrationEnd Registration period end
     * @param maxAttendees Maximum number of maxAttendees for the event
     * @param price Event price
     * @return The created Event object (simplified - would need Event class)
     */
    public Event createEvent(String title, String description, Date startTime, Date endTime,
                             Date registrationStart, Date registrationEnd, int maxAttendees, double price) {
        // This would typically call an EventService to create the event
        // For now, returns a simplified Event object
        Event newEvent = new Event();
        newEvent.setTitle(title);
        newEvent.setDescription(description);
        newEvent.setOrganizerId(this.getUserId());
        newEvent.setStartTime(startTime);
        newEvent.setEndTime(endTime);
        newEvent.setRegistrationStart(registrationStart);
        newEvent.setRegistrationEnd(registrationEnd);
        newEvent.setMaxAttendees(maxAttendees);
        newEvent.setPrice(price);

        // TODO: Add this event to organizer's created events
        // TODO: We would need the actual event ID after saving to database
        // this.addCreatedEvent(newEvent.getEventId());

        return newEvent;
    }

    /**
     * Gets the number of events created by this organizer
     * @return Count of created events
     */
    public int getCreatedEventsCount() {
        return createdEventIds.size();
    }

    /**
     * Gets the number of events joined by this organizer
     * @return Count of joined events
     */
    public int getJoinedEventsCount() {
        return joinedEventIds.size();
    }
}
