package com.ualberta.eventlottery;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.databinding.ActivityEventLotteryMainBinding;
import com.ualberta.eventlottery.notification.NotificationController;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityEventLotteryMainBinding binding;

    private boolean isAdmin;
    private static final String CHANNEL_ID = "demo_channel_id";
    private FirebaseAuth mAuth;
    private static String TAG = "Event Lottery";

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityEventLotteryMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_event_lottery_main);
//        NavigationUI.setupWithNavController(binding.navView, navController);
//
//        Class activity = EntrantMainActivity.class;
//
//        String userRole = "entrant"; // Change to admin or organizer to go to the admin or organizer screens
//        if (userRole.compareTo("admin") == 0){
//            activity = AdminMainActivity.class;
//        } else{
//            activity = EntrantMainActivity.class;
//        }
//        Intent myIntent = new Intent(MainActivity.this, activity);
//        MainActivity.this.startActivity(myIntent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupClickListeners();

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
                Intent intent = new Intent(MainActivity.this, EntrantMainActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST1", "Button Clicked");
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.w("FCM", "Fetching FCM token failed", task.getException());
                                return;
                            }

                            // Get the new FCM registration token
                            String token = task.getResult();
                            Log.d("FCM", "Current FCM token: " + token);

                            // Call the Firebase callable function
                            Log.d("TEST!", "About to call function");
                            NotificationController notificationController = new NotificationController(MainActivity.this);
                            List<String> tokens = List.of("eie-QAWGST6_yVFxEpnFRM:APA91bG5vyLOWvjOI8AViq54jvzOQznaIgY6OVb4RRGHKm1pm8izSnIAHhs1NCqnv-sLEuP1bE2mbnEyxagPjfHeogZ3cS070Q9RXxL84e7GnjVf9EeBVsU", "efELRSh8TmCeQc0-MqScos:APA91bHARSxu69dc7rjAeZngQsO6RbL6P51zjzD0r5ptCLuj990GRfDgoe3lZ7p_CLDzl55Do-fR8DtoZgHeyp7muwRN2E62UQtjyOECwP4Bvq8nBGQOo64");
                            notificationController.sendNotification(tokens, "Title", "Body", "event123");
                            Log.d("TEST1", "Fucntion should be called");
                        });
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