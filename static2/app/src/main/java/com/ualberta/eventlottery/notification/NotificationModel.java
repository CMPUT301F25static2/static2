package com.ualberta.eventlottery.notification;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.ualberta.eventlottery.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Date;


public class NotificationModel {
    private String notificationId;
    private String title;
    private String body;
    private final Date createdAt;
    private final String eventId;
    private String senderId;
    private final List<String> recipientIdList;
    private boolean isRead;
    private static Context appContext;
    private static final String COLLECTION_NAME = "notifications";

    public NotificationModel(String title, String body, String eventId, List<String> recipientIdList) {
        this.title = title;
        this.body = body;
        this.createdAt = new Date();
        this.eventId = eventId;
        this.recipientIdList = recipientIdList;
        this.isRead = false;
        this.senderId =  null;
    }

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }
    public String getNotificationId() { return notificationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getEventId() { return eventId; }
    public Date getCreatedAt() { return createdAt; }
    public String getSenderId() { return senderId; }
    public List<String> getRecepientIdList() { return recipientIdList; }
    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean read) { isRead = read; }



    public void markAsRead() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME)
                .document(this.notificationId)
                .update("isRead", true)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Name successfully updated!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating name", e));
    }
    public void fetchSenderIdAndSave() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(this.eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        this.senderId = documentSnapshot.getString("organizerId");
                        save();
                    } else {
                        Log.e("Firestore", "Event not found for senderId");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching organizerId", e);
                    save(); // still save on error
                });
    }

    public void save() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_NAME)
                .add(this)
                .addOnSuccessListener(documentReference -> {
                    this.notificationId = documentReference.getId();
                    documentReference.update("notificationId", this.notificationId);
                    Log.d("NotificationModel", "Saved notification with ID: " + this.notificationId);
                })
                .addOnFailureListener(e ->
                        Log.e("NotificationModel", "Error saving notification", e));
    }
}
