package com.ualberta.eventlottery.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ualberta.static2.R;
import com.ualberta.eventlottery.model.Event;

/**
 * Controller for handling notification logic.
 * Acts as the mediator between FirebaseMessagingService, the model, and the view.
 */
public class NotificationController {

    private final Context context;
    private final String CHANNEL_ID = "organizer notification channel";

    public NotificationController(Context context) {
        this.context = context;
        createNotificationChannel();
        NotificationModel.initialize(context);
    }

    /**
     * Handles an incoming FCM message.
     * Creates a NotificationModel, saves it, and displays it.
     */
    public void handleIncomingNotification(String title, String message, Event event, String entrantId) {
        NotificationModel notification = new NotificationModel(message, event, entrantId);
        notification.save();
        displayNotification(title, message);
    }

    /** Displays a system notification (UI side). */
    private void displayNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // replace with actual icon
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        // 🔒 Android 13+: Must have POST_NOTIFICATIONS permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission not granted — skip showing notification safely
                android.util.Log.w("NotificationController",
                        "POST_NOTIFICATIONS permission not granted. Notification not shown.");
                return;
            }
        }

        try {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            android.util.Log.e("NotificationController",
                    "Failed to post notification due to missing permission: " + e.getMessage());
        }
    }


    /** Clears all system notifications and model cache. */
    public void clearAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll();
        NotificationModel.clearAll();
    }

    /** Creates notification channel for Android 8.0+ */
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
