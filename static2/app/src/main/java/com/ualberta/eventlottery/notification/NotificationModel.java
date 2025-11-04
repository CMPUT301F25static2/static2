package com.ualberta.eventlottery.notification;

import android.content.Context;

import com.ualberta.eventlottery.model.Event;

import java.util.ArrayList;
import java.util.List;

public class NotificationModel {
    private final long id;
    private String title;
    private String body;
    private long timestamp;
    private final Event event;
    private final String organizerId;
    private final String entrantId;
    private boolean isRead;
    private static Context appContext;

    public NotificationModel(long id,String title, String body, long timestamp, Event event, String entrantID) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.event = event;
        this.organizerId =  event.getOrganizerId();
        this.entrantId = entrantID;
        this.isRead = false;
    }



    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    public long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Event getEvent() { return event; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getSender() { return organizerId; }
    public String getReceiver() { return entrantId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }


    // TODO: implement markAsRead
    public static void markAsRead(long id) {

    }

    // TODO: implement save
    public static void save(NotificationModel notification) {

    }
}
