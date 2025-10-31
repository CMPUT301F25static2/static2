package com.ualberta.eventlottery.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> phone = new MutableLiveData<>("");

    // LiveData getters
    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }


    // Setters (currently local; later can push to Firebase)
    public void setName(String value) { name.setValue(value); }
    public void setEmail(String value) { email.setValue(value); }


    // Optional: load data from Firebase later
    public void loadProfileFromFirebase(String userId) {
        // TODO: query Firebase and update LiveData
    }
}
