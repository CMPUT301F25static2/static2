package com.ualberta.eventlottery.notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ualberta.eventlottery.model.Event;

//entry point for fcm messages
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload is empty");
            return;
        }
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String eventID = remoteMessage.getData().get("eventID");
        String entrantId = remoteMessage.getData().get("entrantId");
        // TODO: use eventID to get event from db
        Event event = new Event();
        event.setOrganizerId("test123");


        NotificationController controller = new NotificationController(getApplicationContext());
        controller.receiveNotification(title, body, event, entrantId);
    }
    @Override
    public void onNewToken(String token) {
        // TODO: update token in firebase
        Log.d(TAG, "Refreshed token: " + token);
    }
}
