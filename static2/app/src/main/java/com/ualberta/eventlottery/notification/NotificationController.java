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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.static2.R;
import com.ualberta.eventlottery.model.Event;

import java.util.ArrayList;
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

    //display incoming fcm notifications to users
    public void displayNotification(String title, String body, String eventId) {
        // TODO: implement functionality where clicking on notification takes you to event page
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
    public void sendNotification(String title, String body, String eventId, List<String> recipientIdList) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Save the notification record
        NotificationModel notification = new NotificationModel(title, body, eventId, recipientIdList);
        notification.fetchSenderIdAndSave();

        // Prepare tasks to fetch all users
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String recipientId : recipientIdList) {
            tasks.add(db.collection("users").document(recipientId).get());
        }

        // Wait until all user documents are fetched
        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(results -> {
                    List<String> tokens = new ArrayList<>();

                    for (Object result : results) {
                        DocumentSnapshot userDoc = (DocumentSnapshot) result;
                        if (userDoc.exists()) {
                            String fcmToken = userDoc.getString("fcmToken");
                            if (fcmToken != null && !fcmToken.isEmpty()) {
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

                    // Prepare notification payload
                    Map<String, Object> data = new HashMap<>();
                    data.put("title", title);
                    data.put("body", body);
                    data.put("eventId", eventId);
                    // Send notification
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
