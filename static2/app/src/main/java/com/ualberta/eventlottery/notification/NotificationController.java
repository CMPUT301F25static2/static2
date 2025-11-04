package com.ualberta.eventlottery.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.functions.FirebaseFunctions;
import com.ualberta.static2.R;
import com.ualberta.eventlottery.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationController {

    private final Context context;
    private final String CHANNEL_ID = "notification channel";

    public NotificationController(Context context) {
        this.context = context;
        createNotificationChannel();
        NotificationModel.initialize(context);
    }

    //handle incoming fcm messages
    public void receiveNotification(String title, String body, Event event, String entrantId) {
        // TODO: save notification to firebase and generate unique id
        NotificationModel notification = new NotificationModel(1,title, body,2, event, entrantId);
        Log.d("TEST1", notification.getBody());
        //notification.save()
        displayNotification(title, body);

    }

    //display notification to user
    private void displayNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // replace with actual icon
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context).notify(1, builder.build());


    }
    public void sendNotification(
            //List<String> entrants,
            List<String> tokens,
            String title,
            String body,
            String eventId

    ) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        // TODO: use entrants to retrieve FCM token from db
        // for entrant in entrants: retrieve token from db
        //tokens.add(token)

        Map<String, Object> data = new HashMap<>();
        //data.put("tokens",tokens")
        data.put("tokens", tokens);
        data.put("title", title);
        data.put("body", body);
        data.put("eventId", eventId);

        //call firebase function
        if (tokens.isEmpty()) {
            // Implement error call or logging
            Log.e("FCM", "No tokens available — cannot send notification.");
        } else if (tokens.size() == 1) {
            // Send a single notification
            functions
                    .getHttpsCallable("sendNotification")
                    .call(data);
        } else {
            // Send multiple notifications
            functions
                    .getHttpsCallable("sendMultipleNotifications")
                    .call(data);
        }
    }

    //create notification channel
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
