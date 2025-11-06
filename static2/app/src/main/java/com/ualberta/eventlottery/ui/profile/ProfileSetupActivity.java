package com.ualberta.eventlottery.ui.profile;

import android.content.Intent;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ualberta.eventlottery.admin.AdminMainActivity;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhoneNumber;
    private String fcmToken;
    private Button btnSaveProfile;
    private FirebaseFirestore db;

    private RadioGroup radioUserType;
    private RadioButton radioEntrant, radioOrganizer, radioAdmin;

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

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setFCMTokenAndSaveProfile(); }
        });
    }
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
        User userProfile = new User(userId, name, email, phoneNumber, this.fcmToken, userType, "");
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

}