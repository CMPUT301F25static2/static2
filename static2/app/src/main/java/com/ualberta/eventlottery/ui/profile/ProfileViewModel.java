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

import java.util.HashMap;
import java.util.Map;

/**
 * ViewModel for the Profile screen.
 */
public class ProfileViewModel extends ViewModel {

    private static final String TAG = "ProfileViewModel";
    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> phone = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> favoriteRecCenter = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>(null);

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // LiveData getters
    /**
     * Returns the LiveData for the user's name.
     *
     * @return The LiveData for the user's name.
     */
    public LiveData<String> getName() { return name; }
    /**
     * Returns the LiveData for the user's email.
     *
     * @return The LiveData for the user's email.
     */
    public LiveData<String> getEmail() { return email; }
    /**
     * Returns the LiveData for the user's phone number.
     *
     * @return The LiveData for the user's phone number.
     */
    public LiveData<String> getPhone() { return phone; }
    /**
     * Returns the LiveData for the loading state.
     *
     * @return The LiveData for the loading state.
     */
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    /**
     * Returns the LiveData for the error message.
     *
     * @return The LiveData for the error message.
     */
    public LiveData<String> getErrorMessage() { return errorMessage; }
    /**
     * Returns the LiveData for the user's favorite rec center.
     *
     * @return The LiveData for the user's favorite rec center.
     */
    public LiveData<String> getFavoriteRecCenter() { return favoriteRecCenter; }
    /**
     * Returns the LiveData for the user's notification permissions.
     *
     * @return The LiveData for the user's notification permissions.
     */
    public LiveData<Boolean> getNotificationsEnabled() { return notificationsEnabled; }



    /**
     * Sets the user's name.
     *
     * @param value The new name.
     */
    public void setName(String value) { name.setValue(value); }
    /**
     * Sets the user's email.
     *
     * @param value The new email.
     */
    public void setEmail(String value) { email.setValue(value); }
    /**
     * Sets the user's phone number.
     *
     * @param value The new phone number.
     */
    public void setPhone(String value) { phone.setValue(value); }

    /**
     * Sets the user's favorite rec center.
     *
     * @param value The new favorite rec center.
     */
    public void setFavoriteRecCenter(String value) {
        favoriteRecCenter.setValue(value);
    }
    /**
     * Sets the user's notification permission.
     *
     * @param value The new notification permission.
     */
    public void setNotificationsEnabled(Boolean value) {
        notificationsEnabled.setValue(value);
    }

    /**
     * Loads the user's profile from Firebase.
     *
     * @param userId The ID of the user whose profile is to be loaded.
     */
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
                            notificationsEnabled.setValue(profile.getNotificationsEnabled());
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

    /**
     * Saves the user's profile to Firebase.
     * Changed to prevent overwriting of FCM Tokens
     *
     * @param userId The ID of the user whose profile is to be saved.
     */
    public void saveProfileToFirebase(String userId) {
        isLoading.setValue(true);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name.getValue());
        updates.put("email", email.getValue());
        updates.put("phone", phone.getValue());
        updates.put("favRecCenter", favoriteRecCenter.getValue());
        updates.put("notificationsEnabled", notificationsEnabled.getValue());

        db.collection("users")
                .document(userId)
                .update(updates)
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
