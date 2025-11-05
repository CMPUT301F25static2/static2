package com.ualberta.eventlottery.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhoneNumber;
    private Button btnSaveProfile;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
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

        // Create user profile
        User userProfile = new User(userId, name, email, phoneNumber);

        // Save to Firestore
        db.collection("users")
                .document(userId)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileSetupActivity.this,
                            "Profile saved successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to EntrantMainActivity
                    Intent intent = new Intent(ProfileSetupActivity.this, EntrantMainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileSetupActivity.this,
                            "Failed to save profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}