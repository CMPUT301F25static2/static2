package com.ualberta.eventlottery.model;

import com.ualberta.eventlottery.notification.NotificationModel;

/**
 * Extended notification model for displaying notification logs to admins.
 * US 03.08.01: As an administrator, I want to review logs of all notifications sent to entrants by organizers.
 */
public class NotificationLog extends NotificationModel {
    private String organizerName;
    private String eventTitle;
    private int recipientCount;

    public NotificationLog() {
        super();
    }

    public NotificationLog(NotificationModel notification) {
        this.setNotificationId(notification.getNotificationId());
        this.setTitle(notification.getTitle());
        this.setBody(notification.getBody());
        this.setIsRead(notification.getIsRead());
        this.recipientCount = notification.getRecipientIdList() != null
            ? notification.getRecipientIdList().size() : 0;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }
}

