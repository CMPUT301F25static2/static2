package com.ualberta.eventlottery.ui.adminUsers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
