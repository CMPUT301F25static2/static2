package com.ualberta.eventlottery.ui.adminLogs;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ualberta.eventlottery.model.NotificationLog;
import com.ualberta.eventlottery.notification.NotificationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for admin notification logs screen.
 * US 03.08.01: As an administrator, I want to review logs of all notifications sent to entrants by organizers.
 */
public class AdminLogViewModel extends ViewModel {
    private static final String TAG = "AdminLogViewModel";
    private final MutableLiveData<List<NotificationLog>> logs = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final FirebaseFirestore db;
    private List<NotificationLog> allLogs = new ArrayList<>();

    public AdminLogViewModel() {
        db = FirebaseFirestore.getInstance();
        loadNotificationLogs();
    }

    public LiveData<List<NotificationLog>> getLogs() {
        return logs;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Loads all notification logs from Firestore.
     */
    public void loadNotificationLogs() {
        isLoading.setValue(true);

        db.collection("notifications")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<NotificationLog> logList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            NotificationModel notification = document.toObject(NotificationModel.class);
                            NotificationLog log = new NotificationLog(notification);

                            // Fetch additional details (event title and organizer name)
                            enrichLogWithDetails(log, logList, queryDocumentSnapshots.size());

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing notification: " + document.getId(), e);
                        }
                    }

                    allLogs = logList;
                    logs.setValue(logList);
                    isLoading.setValue(false);
                    Log.d(TAG, "Loaded " + logList.size() + " notification logs");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading notification logs", e);
                    errorMessage.setValue("Failed to load logs: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    /**
     * Enriches a notification log with event and organizer details.
     */
    private void enrichLogWithDetails(NotificationLog log, List<NotificationLog> logList, int totalCount) {
        // Add log to list first
        logList.add(log);

        // Fetch event details if eventId exists
        if (log.getEventId() != null && !log.getEventId().isEmpty()) {
            db.collection("events")
                    .document(log.getEventId())
                    .get()
                    .addOnSuccessListener(eventDoc -> {
                        if (eventDoc.exists()) {
                            log.setEventTitle(eventDoc.getString("title"));

                            // Fetch organizer name
                            String organizerId = eventDoc.getString("organizerId");
                            if (organizerId != null) {
                                fetchOrganizerName(log, organizerId);
                            }
                        }

                        // Update UI after enriching
                        if (logList.size() == totalCount) {
                            logs.setValue(new ArrayList<>(logList));
                        }
                    })
                    .addOnFailureListener(e ->
                        Log.e(TAG, "Error fetching event details", e));
        }
    }

    /**
     * Fetches organizer name from users collection.
     */
    private void fetchOrganizerName(NotificationLog log, String organizerId) {
        db.collection("users")
                .document(organizerId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String name = userDoc.getString("name");
                        log.setOrganizerName(name != null ? name : "Unknown");
                        logs.setValue(new ArrayList<>(allLogs));
                    }
                })
                .addOnFailureListener(e ->
                    Log.e(TAG, "Error fetching organizer name", e));
    }

    /**
     * Filters logs based on search query.
     */
    public void filterLogs(String query) {
        if (query == null || query.trim().isEmpty()) {
            logs.setValue(allLogs);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<NotificationLog> filtered = new ArrayList<>();

        for (NotificationLog log : allLogs) {
            boolean matches = false;

            // Search in title
            if (log.getTitle() != null && log.getTitle().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }

            // Search in body
            if (log.getBody() != null && log.getBody().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }

            // Search in event title
            if (log.getEventTitle() != null && log.getEventTitle().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }

            // Search in organizer name
            if (log.getOrganizerName() != null && log.getOrganizerName().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }

            if (matches) {
                filtered.add(log);
            }
        }

        logs.setValue(filtered);
    }
}
