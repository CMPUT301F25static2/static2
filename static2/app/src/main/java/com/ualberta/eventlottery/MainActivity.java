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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.ui.profile.ProfileSetupActivity;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EventLottery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askNotificationPermission();
        setupAuthentication();
    }

    private void setupAuthentication() {
        UserManager.initializeUser(new UserManager.InitCallback() {
            @Override
            public void onSuccess(String userId) {
                Log.d(TAG, "User initialization successful: " + userId);
                checkUserProfile(userId);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, "User initialization failed", exception);
                Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserProfile(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // User profile exists, direct to the correct activity
                            directUser(document);
                        } else {
                            // No profile found, direct to profile setup
                            startActivity(new Intent(MainActivity.this, ProfileSetupActivity.class));
                            finish(); // Finish MainActivity so user can't go back
                        }
                    } else {
                        // Handle failure
                        Log.e(TAG, "Failed to fetch user profile", task.getException());
                        Toast.makeText(MainActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void directUser(DocumentSnapshot document) {
        String userType = document.getString("userType");
        if (userType != null) {
            switch (userType) {
                case "organizer":
                    startActivity(new Intent(this, OrganizerMainActivity.class));
                    break;
                case "admin":
                    startActivity(new Intent(this, AdminMainActivity.class));
                    break;
                default:
                    // Default to entrant if userType is not specified or something else
                    startActivity(new Intent(this, EntrantMainActivity.class));
                    break;
            }
        } else {
            // If userType is not set, default to entrant
            startActivity(new Intent(this, EntrantMainActivity.class));
        }
        finish(); // Finish MainActivity
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
