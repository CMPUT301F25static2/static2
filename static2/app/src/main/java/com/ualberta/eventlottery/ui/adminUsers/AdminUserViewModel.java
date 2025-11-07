package com.ualberta.eventlottery.ui.adminUsers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * This is a class that serves as the user search screen for the admin.
 */
public class AdminUserViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdminUserViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is admin user search fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
