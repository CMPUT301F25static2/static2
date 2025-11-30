package com.ualberta.eventlottery.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;

/**
 * Activity for setting up a user's profile for the first time.
 */
public class ProfileSetupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhoneNumber;
    private String fcmToken;
    private boolean notificationsEnabled = false;
    private Button btnSaveProfile;
    private FirebaseFirestore db;

    private RadioGroup radioUserType;
    private RadioButton radioEntrant, radioOrganizer, radioAdmin;

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with
     * a Bundle containing the activity's previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     *                           null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        radioUserType = findViewById(R.id.radio_user_type);
        radioEntrant = findViewById(R.id.radio_entrant);
        radioOrganizer = findViewById(R.id.radio_organizer);
        radioAdmin = findViewById(R.id.radio_admin);

        askNotificationPermission();

        // Set click listener
        btnSaveProfile.setOnClickListener(v -> setFCMTokenAndSaveProfile());
    }


    /**
     * Saves the user's profile information to Firestore.
     */
    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone number is required");
            etPhoneNumber.requestFocus();
            return;
        }

        // Get the current user ID
        String userId = UserManager.getCurrentUserId();

        String userType = getUserType();

        // Create user profile
        User userProfile = new User(userId, name, email, phoneNumber, this.fcmToken, userType, "", this.notificationsEnabled) ;
        Log.d("FCM", "Get method:"+userProfile.getFcmToken());


        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileSetupActivity.this,
                            "Profile saved successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to EntrantMainActivity
//                    Intent intent = new Intent(ProfileSetupActivity.this, EntrantMainActivity.class);
//                    startActivity(intent);
//                    finish();
                    navigateToUserHome(userType);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileSetupActivity.this,
                            "Failed to save profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Fetches the FCM token and then saves the user's profile.
     */
    private void setFCMTokenAndSaveProfile() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        Toast.makeText(this, "Failed to get FCM token", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get the new FCM registration token
                    this.fcmToken = task.getResult();
                    Log.d("FCM", "Fetched FCM token: " + fcmToken);

                    // Now that we have the token, save the profile
                    saveProfile();
                });
    }

    /**
     * Navigates to the appropriate home screen based on the user's type.
     *
     * @param userType The user's type (e.g., "entrant", "organizer", "admin").
     */
    private void navigateToUserHome(String userType) {
        Intent intent;

        switch (userType) {
            case "organizer":
                intent = new Intent(ProfileSetupActivity.this, OrganizerMainActivity.class);
                break;
            case "admin":
                intent = new Intent(ProfileSetupActivity.this, AdminMainActivity.class);
                break;
            case "entrant":
            default:
                intent = new Intent(ProfileSetupActivity.this, EntrantMainActivity.class);
                break;
        }

        startActivity(intent);
        finish();
    }

    /**
     * Gets the selected user type from the radio buttons.
     *
     * @return The selected user type as a string.
     */
    private String getUserType() {
        int selectedId = radioUserType.getCheckedRadioButtonId();

        if (selectedId == R.id.radio_organizer) {
            return "organizer";
        } else if (selectedId == R.id.radio_admin) {
            return "admin";
        } else {
            return "entrant";
        }
    }
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Load saved state
            notificationsEnabled = getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("notifications_enabled", false);

            // Check if we already asked before
            boolean askedBefore = getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("notification_prompt_shown", false);

            if (askedBefore) {
                return; // Do nothing if permission was already asked
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                // Permission already granted
                notificationsEnabled = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

            // Update activity attribute
            notificationsEnabled = granted;

            // Save flag so we don't show again
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("notification_prompt_shown", true)
                    .putBoolean("notifications_enabled", granted)
                    .apply();

            if (granted) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Notifications Enabled")
                        .setMessage("Notifications are now enabled. You can change this anytime in App Settings.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Notifications Disabled")
                        .setMessage("Notifications were denied. You can enable them later in App Settings.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }


}
