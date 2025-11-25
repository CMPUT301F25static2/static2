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

/**
 * ViewModel that manages and observes notification data for the current user.
 * Listens to Firestore updates in real-time and provides notifications to the UI.
 */
public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<NotificationModel>> notifications = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;

    /**
     * Initializes the ViewModel and starts listening to Firestore for notification updates.
     */
    public NotificationsViewModel() {
        listenToNotifications();
    }

    /**
     * Sets up a Firestore listener that retrieves all notifications
     * where the current user's ID appears in the recipient list.
     * Updates the LiveData when data changes.
     */
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

                        notifications.setValue(notificationList);
                    }
                });
    }

    /**
     * Returns a LiveData list of notifications for the current user.
     *
     * @return LiveData containing notification objects
     */
    public LiveData<List<NotificationModel>> getNotifications() {
        return notifications;
    }

    /**
     * Removes the Firestore listener when the ViewModel is cleared.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
