package com.ualberta.eventlottery.ui.adminImages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * This is a class that serves as the images screen for the admin.
 */
public class AdminImageViewModel extends ViewModel {
        private final MutableLiveData<String> mText;

        public AdminImageViewModel() {
            mText = new MutableLiveData<>();
            mText.setValue("This is admin all images fragment");
        }

        public LiveData<String> getText() {
            return mText;
        }


}
