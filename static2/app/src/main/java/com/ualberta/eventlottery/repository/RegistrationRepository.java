package com.ualberta.eventlottery.repository;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for managing Registration operations with Firebase Firestore
 * Uses callback pattern for asynchronous operations
 */
public class RegistrationRepository {
    private static RegistrationRepository instance;
    private FirebaseFirestore db;
    private static final String COLLECTION_REGISTRATIONS = "registrations";

    public interface RegistrationCallback {
        void onSuccess(Registration registration);
        void onFailure(Exception e);
    }

    public interface RegistrationListCallback {
        void onSuccess(List<Registration> registrations);
        void onFailure(Exception e);
    }

    public interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface BooleanCallback {
        void onSuccess(boolean result);
        void onFailure(Exception e);
    }

    public interface CountCallback {
        void onSuccess(int count);
        void onFailure(Exception e);
    }

    private RegistrationRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized RegistrationRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationRepository();
        }
        return instance;
    }

    /**
     * Converts Firestore document to Registration object
     */
    private Registration documentToRegistration(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        Registration registration = new Registration();
        registration.setId(document.getId());
        registration.setEventId(document.getString("eventId"));
        registration.setEntrantId(document.getString("entrantId"));

        // Status field
        String status = document.getString("status");
        if (status != null) {
            registration.setStatus(EntrantRegistrationStatus.valueOf(status));
        }

        // Date fields
        registration.setRegisteredAt(document.getDate("registeredAt"));
        registration.setRespondedAt(document.getDate("respondedAt"));
        registration.setCancelledAt(document.getDate("cancelledAt"));

        return registration;
    }

    /**
     * Converts Registration object to Firestore data map
     */
    private Map<String, Object> registrationToMap(Registration registration) {
        Map<String, Object> registrationMap = new HashMap<>();
        registrationMap.put("eventId", registration.getEventId());
        registrationMap.put("entrantId", registration.getEntrantId());
        registrationMap.put("status", registration.getStatus() != null ? registration.getStatus().name() : null);
        registrationMap.put("registeredAt", registration.getRegisteredAt());
        registrationMap.put("respondedAt", registration.getRespondedAt());
        registrationMap.put("cancelledAt", registration.getCancelledAt());
        registrationMap.put("updatedAt", new Date());

        return registrationMap;
    }

    /**
     * Finds a registration by its ID
     */
    public void findRegistrationById(String registrationId, RegistrationCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .document(registrationId)
                .get()
                .addOnSuccessListener(document -> {
                    Registration registration = documentToRegistration(document);
                    if (registration != null) {
                        callback.onSuccess(registration);
                    } else {
                        callback.onFailure(new Exception("Registration not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Finds registration by event ID and user ID
     */
    public void findRegistrationByEventAndUser(String eventId, String userId, RegistrationCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("entrantId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Registration registration = documentToRegistration(querySnapshot.getDocuments().get(0));
                        callback.onSuccess(registration);
                    } else {
                        callback.onSuccess(null); // No registration found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all registrations for a specific event
     */
    public void getRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Registration registration = documentToRegistration(document);
                        if (registration != null) {
                            registrations.add(registration);
                        }
                    }
                    callback.onSuccess(registrations);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all registrations for a specific entrant
     */
    public void getRegistrationsByEntrant(String entrantId, RegistrationListCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("entrantId", entrantId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Registration registration = documentToRegistration(document);
                        if (registration != null) {
                            registrations.add(registration);
                        }
                    }
                    callback.onSuccess(registrations);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves registrations by status for a specific event
     */
    public void getRegistrationsByStatus(String eventId, EntrantRegistrationStatus status, RegistrationListCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", status.name())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Registration registration = documentToRegistration(document);
                        if (registration != null) {
                            registrations.add(registration);
                        }
                    }
                    callback.onSuccess(registrations);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves registrations by entrant and status
     */
    public void getRegistrationsByEntrantAndStatus(String entrantId, EntrantRegistrationStatus status, RegistrationListCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("entrantId", entrantId)
                .whereEqualTo("status", status.name())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Registration registration = documentToRegistration(document);
                        if (registration != null) {
                            registrations.add(registration);
                        }
                    }
                    callback.onSuccess(registrations);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all registrations
     */
    public void getAllRegistrations(RegistrationListCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registration> registrations = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Registration registration = documentToRegistration(document);
                        if (registration != null) {
                            registrations.add(registration);
                        }
                    }
                    callback.onSuccess(registrations);
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Adds a new registration
     */
    public void addRegistration(Registration registration, OperationCallback callback) {
        if (registration.getId() == null || registration.getId().isEmpty()) {
            // Create new document with auto-generated ID
            String newId = db.collection(COLLECTION_REGISTRATIONS).document().getId();
            registration.setId(newId);
        }

        // Set registration date if not already set
        if (registration.getRegisteredAt() == null) {
            registration.setRegisteredAt(new Date());
        }

        Map<String, Object> registrationData = registrationToMap(registration);
        db.collection(COLLECTION_REGISTRATIONS)
                .document(registration.getId())
                .set(registrationData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Updates an existing registration
     */
    public void updateRegistration(Registration updatedRegistration, BooleanCallback callback) {
        Map<String, Object> registrationData = registrationToMap(updatedRegistration);
        db.collection(COLLECTION_REGISTRATIONS)
                .document(updatedRegistration.getId())
                .update(registrationData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Deletes a registration by ID
     */
    public void deleteRegistration(String registrationId, BooleanCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .document(registrationId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Gets count of registrations by status for an event
     */
    public void getRegistrationCountByStatus(String eventId, EntrantRegistrationStatus status, CountCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", status.name())
                .get()
                .addOnSuccessListener(querySnapshot -> callback.onSuccess(querySnapshot.size()))
                .addOnFailureListener(callback::onFailure);
    }



    /**
     * Retrieves waiting registrations for an event
     */
    public void getWaitingRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        getRegistrationsByStatus(eventId, EntrantRegistrationStatus.WAITING, callback);
    }

    /**
     * Retrieves selected registrations for an event
     */
    public void getSelectedRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        getRegistrationsByStatus(eventId, EntrantRegistrationStatus.SELECTED, callback);
    }

    /**
     * Retrieves confirmed registrations for an event
     */
    public void getConfirmedRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        getRegistrationsByStatus(eventId, EntrantRegistrationStatus.CONFIRMED, callback);
    }

    /**
     * Checks if user is registered for an event
     */
    public void isUserRegisteredForEvent(String eventId, String userId, BooleanCallback callback) {
        findRegistrationByEventAndUser(eventId, userId, new RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                callback.onSuccess(registration != null);
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


}