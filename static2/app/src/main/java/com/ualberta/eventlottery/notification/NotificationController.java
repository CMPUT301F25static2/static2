package com.ualberta.eventlottery.notification;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.static2.R;
import com.ualberta.eventlottery.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controls app notifications, including sending Firebase Cloud Messaging (FCM) notifications
 * and displaying them locally on the device.
 */
public class NotificationController {

    private final Context context;
    private final String CHANNEL_ID = "notification channel";

    /**
     * Creates a new NotificationController and sets up the notification channel.
     * @param context the application or activity context
     */
    public NotificationController(Context context) {
        this.context = context;
        createNotificationChannel();
        NotificationModel.initialize(context);
    }

    /**
     * Displays a local notification to the user.
     * Opens {@link } when the notification is tapped.
     *
     * @param title   the title of the notification
     * @param body    the message body
     */
    public void displayNotification(String title, String body) {
        Intent intent = new Intent(context, EntrantMainActivity.class);
        intent.putExtra("open_notifications", true); // Flag to open NotificationsFragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(context).notify(new Random().nextInt(), builder.build());
    }

    /**
     * Sends a push notification to a list of recipients via Firebase Cloud Messaging.
     *
     * @param title           the title of the notification
     * @param body            the message body
     * @param eventId         the related event ID
     * @param recipientIdList list of user IDs to receive the notification
     */
    public void sendNotification(String title, String body, String eventId, List<String> recipientIdList, String notificationType) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        NotificationModel notification = new NotificationModel(title, body, eventId, recipientIdList, notificationType);
        notification.fetchSenderIdAndSave();

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String recipientId : recipientIdList) {
            tasks.add(db.collection("users").document(recipientId).get());
        }

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> {
                    List<String> tokens = new ArrayList<>();
                    for (Object result : results) {
                        DocumentSnapshot userDoc = (DocumentSnapshot) result;
                        if (userDoc.exists()) {
                            Boolean notificationsEnabled = userDoc.getBoolean("notificationsEnabled");
                            String fcmToken = userDoc.getString("fcmToken");
                            if (Boolean.TRUE.equals(notificationsEnabled) && fcmToken != null && !fcmToken.isEmpty()) {
                                tokens.add(fcmToken);
                            }
                        } else {
                            Log.d("Firestore", "No such user!");
                        }
                    }

                    if (tokens.isEmpty()) {
                        Log.e("FCM", "No tokens available — cannot send notification.");
                        return;
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("title", title);
                    data.put("body", body);
                    data.put("eventId", eventId);

                    if (tokens.size() == 1) {
                        data.put("token", tokens.get(0));
                        functions.getHttpsCallable("sendNotification").call(data);
                    } else {
                        data.put("tokens", tokens);
                        functions.getHttpsCallable("sendMultipleNotifications").call(data);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user tokens", e));
    }

    /**
     * Creates the app's notification channel for Android 8.0 and above.
     * Ensures notifications are properly categorized.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Lottery Notifications";
            String description = "Notifications for events and entrants";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
