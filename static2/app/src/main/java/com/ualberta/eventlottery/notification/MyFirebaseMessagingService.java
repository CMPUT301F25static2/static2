package com.ualberta.eventlottery.notification;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ualberta.eventlottery.utils.UserManager;

/**
 * Handles incoming Firebase Cloud Messaging (FCM) messages and token updates.
 * Displays notifications when messages are received.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when an FCM message is received.
     * Extracts message data and shows a notification using {@link NotificationController}.
     *
     * @param remoteMessage the received FCM message
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload is empty");
            return;
        }

        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        NotificationController controller = new NotificationController(getApplicationContext());
        controller.displayNotification(title, body);
    }

    /**
     * Called when a new FCM registration token is generated.
     * Updates the user's token in Firebase.
     *
     * @param token the new FCM registration token
     */
    @Override
    public void onNewToken(String token) {
        String currentUserId = UserManager.getCurrentUserId();

        // Update Firestore with the new FCM token
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUserId)
                .update("fcmToken", token)
                .addOnSuccessListener(aVoid ->
                        Log.d("MyFirebaseMsgService", "Successfully updated FCM token for user: " + currentUserId))
                .addOnFailureListener(e ->
                        Log.e("MyFirebaseMsgService", "Error updating FCM token in Firestore", e));

    }
}
