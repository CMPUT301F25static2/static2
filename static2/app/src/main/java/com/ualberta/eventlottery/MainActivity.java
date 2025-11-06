package com.ualberta.eventlottery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.credentials.exceptions.domerrors.NotFoundError;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.auth.FirebaseAuth;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.notification.NotificationController;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.ui.profile.ProfileViewModel;
import com.ualberta.eventlottery.ui.profile.ProfileSetupActivity;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityEventLotteryMainBinding binding;

    private boolean isAdmin;
    private static final String CHANNEL_ID = "demo_channel_id";
    private FirebaseAuth mAuth;
    private static String TAG = "Event Lottery";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Only initialize the view after User initialization below

        // TODO: store into user profile
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get the new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM", "Current FCM token: " + token);
                });


        askNotificationPermission();
        UserManager.initializeUser(this, new UserManager.InitCallback() {
            @Override
            public void onSuccess(String userId) {
                Log.d(TAG, "userInitialization:success:userId=" + userId);
                setContentView(R.layout.activity_main);
                setupClickListeners();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(MainActivity.this, "User initialization failed.",
                        Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_main);
                setupClickListeners();
            }
        });
    }

    private void setupClickListeners() {

        findViewById(R.id.btn_entrant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = UserManager.getCurrentUserId();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Profile exists, go directly to EntrantMainActivity
                                Intent intent = new Intent(MainActivity.this, EntrantMainActivity.class);
                                startActivity(intent);
                            } else {
                                // Show dialog that profile is required
                                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Profile Required")
                                        .setMessage("You need to set up your profile before continuing as an entrant.")
                                        .setPositiveButton("Set Up Profile", (dialog, which) -> {
                                            Intent intent = new Intent(MainActivity.this, ProfileSetupActivity.class);
                                            startActivity(intent);
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });
        findViewById(R.id.btn_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> entrants = new ArrayList<>();
                entrants.add("Z1F5ZglK8zUmygwpZQ4UWUi6L3Q2");
                NotificationController notificationController = new NotificationController(MainActivity.this);
                notificationController.sendNotification("Test", "Body", "s5Zl7eiTfGqXrw9HQtHu", entrants);

            }
        });
        findViewById(R.id.btn_admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_organizer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrganizerMainActivity.class);
                startActivity(intent);
            }
        });


    }

    // Check for POST_NOTIFICATIONS permission
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

}