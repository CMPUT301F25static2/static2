package com.ualberta.eventlottery.ui.adminLogs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * This is a class that serves as the logs screen for the admin.
 */
public class AdminLogViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdminLogViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is admin logs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
