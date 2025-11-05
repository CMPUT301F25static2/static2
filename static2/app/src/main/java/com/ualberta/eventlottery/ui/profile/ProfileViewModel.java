package com.ualberta.eventlottery.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.entrant.EntrantMainActivity;
import com.ualberta.eventlottery.model.User;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = "ProfileViewModel";
    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> phone = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> favoriteRecCenter = new MutableLiveData<>("");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // LiveData getters
    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPhone() { return phone; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getFavoriteRecCenter() { return favoriteRecCenter; }


    public void setName(String value) { name.setValue(value); }
    public void setEmail(String value) { email.setValue(value); }
    public void setPhone(String value) { phone.setValue(value); }

    public void setFavoriteRecCenter(String value) {
        favoriteRecCenter.setValue(value);
    }

    // Load data from Firebase
    public void loadProfileFromFirebase(String userId) {
        isLoading.setValue(true);

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    isLoading.setValue(false);
                    if (documentSnapshot.exists()) {
                        User profile = documentSnapshot.toObject(User.class);
                        if (profile != null) {
                            name.setValue(profile.getName());
                            email.setValue(profile.getEmail());
                            phone.setValue(profile.getPhone());
                            favoriteRecCenter.setValue(profile.getFavRecCenter());
                            Log.d(TAG, "Profile loaded successfully");
                        }
                    } else {
                        Log.d(TAG, "No profile found for user");
                    }
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Failed to load profile: " + e.getMessage());
                    Log.e(TAG, "Error loading profile", e);
                });
    }

    // Save data to Firebase
    public void saveProfileToFirebase(String userId) {
        isLoading.setValue(true);

        User profile = new User(
                userId,
                name.getValue(),
                email.getValue(),
                phone.getValue(),
                favoriteRecCenter.getValue()
        );



        db.collection("users")
                .document(userId)
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Profile saved successfully");
                    Log.d(TAG, "Profile saved successfully");

                    loadProfileFromFirebase(userId);
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Failed to save profile: " + e.getMessage());
                    Log.e(TAG, "Error saving profile", e);
                });
    }


}