package com.ualberta.eventlottery.ui.adminHome;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Lumbani
 * @version 0.0
 * This is a class that serves as the home screen for the admin.
 */
public class AdminHomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdminHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is admin home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
