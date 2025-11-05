package com.ualberta.eventlottery.model;

import java.util.Date;

public class Registration {
    private String id;
    private String eventId;
    private String entrantId;
    private EntrantRegistrationStatus status;
    private Date registeredAt;
    private Date respondedAt;
    private Date cancelledAt;



    public Registration(String id, String eventId, String entrantId) {
        this.id = id;
        this.eventId = eventId;
        this.entrantId = entrantId;
        this.status = EntrantRegistrationStatus.WAITING;
        this.registeredAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEntrantId() { return entrantId; }
    public void setEntrantId(String entrantId) { this.entrantId = entrantId; }

    public EntrantRegistrationStatus getStatus() { return status; }
    public void setStatus(EntrantRegistrationStatus status) { this.status = status; }

    public Date getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Date registeredAt) { this.registeredAt = registeredAt; }

    public Date getRespondedAt() { return respondedAt; }
    public void setRespondedAt(Date respondedAt) { this.respondedAt = respondedAt; }

    public Date getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Date cancelledAt) { this.cancelledAt = cancelledAt; }



    // Business methods
    public void acceptInvitation() {
        this.status = EntrantRegistrationStatus.CONFIRMED;
        this.respondedAt = new Date();
    }

    public void declineInvitation() {
        this.status = EntrantRegistrationStatus.DECLINED;
        this.respondedAt = new Date();
    }

    public boolean isExpired() {
        if (status == EntrantRegistrationStatus.SELECTED) {
            // Check if more than 48 hours passed without response
            long hoursSinceSelected = (System.currentTimeMillis() - registeredAt.getTime()) / (1000 * 60 * 60);
            return hoursSinceSelected > 48;
        }
        return false;
    }
}