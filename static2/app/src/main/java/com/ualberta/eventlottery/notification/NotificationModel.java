package com.ualberta.eventlottery.notification;

import android.content.Context;

import com.ualberta.eventlottery.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single notification AND
 * provides static methods to manage all notifications.
 * (Repository logic is combined here.)
 */
public class NotificationModel {

    // === Instance fields (per-notification data) ===
    private final long id;
    private String message;
    private long timestamp;
    private final Event event;
    private final String organizerId;
    private final String entrantId;
    private boolean isRead;

    // === Static fields (for managing multiple notifications) ===
    private static final List<NotificationModel> notificationCache = new ArrayList<>();
    private static Context appContext;

    // === Constructors ===
    public NotificationModel(long id, String message, long timestamp, Event event, String entrantID) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.event = event;
        this.organizerId = event.getOrganizerId();
        this.entrantId = entrantID;
        this.isRead = false;
    }

    public NotificationModel(String message, Event event, String entrantID) {
        this.id = System.currentTimeMillis();
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.event = event;
        this.organizerId = event.getOrganizerId();
        this.entrantId = entrantID;
        this.isRead = false;
    }

    // === Set up context (needed later for DB operations) ===
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    // === Instance methods ===
    public long getId() { return id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Event getEvent() { return event; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getSender() { return organizerId; }
    public String getReceiver() { return entrantId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    // === Combined repository-like methods ===

    /** Save this notification instance (stub — DB logic goes here) */
    public void save() {
        // TODO: Replace with actual database save logic
        notificationCache.add(this);
    }

    /** Retrieve all stored notifications */
    public static List<NotificationModel> getAll() {
        // TODO: Replace with actual DB query
        return new ArrayList<>(notificationCache);
    }

    /** Mark a notification as read by ID */
    public static void markAsRead(long id) {
        // TODO: Update database entry when implemented
        for (NotificationModel n : notificationCache) {
            if (n.getId() == id) {
                n.setRead(true);
                break;
            }
        }
    }

    /** Delete all notifications */
    public static void clearAll() {
        // TODO: Replace with DB deletion
        notificationCache.clear();
    }
}
