package com.ualberta.eventlottery.ui.notifications;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.ualberta.eventlottery.notification.NotificationModel;
import com.ualberta.eventlottery.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<NotificationModel>> notifications = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;

    public NotificationsViewModel() {
        listenToNotifications();
    }

    private void listenToNotifications() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = UserManager.getCurrentUserId();

        listenerRegistration = db.collection("notifications")
                .whereArrayContains("recipientIdList", currentUserId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("NotificationsVM", "Firestore listener error", e);
                        return;
                    }

                    if (snapshots != null) {
                        Log.d("NotificationsVM", "Retrieved " + snapshots.size() + " documents from Firestore");

                        List<NotificationModel> notificationList = new ArrayList<>();
                        for (var doc : snapshots.getDocuments()) {
                            NotificationModel notification = doc.toObject(NotificationModel.class);
                            if (notification != null) {
                                notificationList.add(notification);
                                Log.d("NotificationsVM", "Document ID: " + doc.getId() + ", data: " + doc.getData());
                            }
                        }
                        // Update LiveData
                        notifications.setValue(notificationList);
                    }
                });
    }

    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
