package com.ualberta.eventlottery.notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ualberta.eventlottery.model.Event;

import java.util.Collections;
import java.util.List;

//entry point for fcm messages
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("TEST1", "Recieved Messgae");
        if (remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload is empty");
            return;
        }
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String eventId = remoteMessage.getData().get("eventId");
        NotificationController controller = new NotificationController(getApplicationContext());
        controller.displayNotification(title, body, eventId);
    }
    @Override
    public void onNewToken(String token) {
        // TODO: update token in firebase
        Log.d(TAG, "Refreshed token: " + token);
    }
}
