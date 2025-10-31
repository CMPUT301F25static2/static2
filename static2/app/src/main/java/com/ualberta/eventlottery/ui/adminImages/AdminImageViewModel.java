package com.ualberta.eventlottery.ui.adminImages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
