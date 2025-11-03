package com.ualberta.eventlottery.notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ualberta.eventlottery.model.Event;

/**
 * Entry point for Firebase Cloud Messaging.
 * Receives FCM messages and hands them off to NotificationController.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "FCM message received from: " + remoteMessage.getFrom());

        String title = "Event Update";
        String message = "You have a new notification!";
        String entrantId = "unknown";
        Event event = null; // In real code, parse event details from data payload

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        if (!remoteMessage.getData().isEmpty()) {
            // Extract custom data (optional)
            entrantId = remoteMessage.getData().get("entrantId");
            // Parse event object if needed
        }

        NotificationController controller = new NotificationController(getApplicationContext());
        controller.handleIncomingNotification(title, message, event, entrantId);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed FCM token: " + token);
        // TODO: send token to backend
    }
}
