package com.ualberta.eventlottery.repository;

import android.location.Location;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.service.LocationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for managing Registration data operations with Firebase Firestore.
 * This class follows the Singleton pattern to provide a single instance
 * for handling all registrtaion-related database operations including CRUD operations
 *
 * @author static2
 * @version 1.0
 */
public class RegistrationRepository {
    private static RegistrationRepository instance;
    private FirebaseFirestore db;
    private static final String COLLECTION_REGISTRATIONS = "registrations";

    // Callback interfaces

    /**
     * Callback interface for single Registration operations.
     */
    public interface RegistrationCallback {
        /**
         * Called when the registration operation is successful.
         *
         * @param registration the retrieved or processed Registration object
         */
        void onSuccess(Registration registration);

        /**
         * Called when the registration operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for multiple Registration operations.
     */
    public interface RegistrationListCallback {
        /**
         * Called when the registration list operation is successful.
         *
         * @param registrations the list of retrieved Registration objects
         */
        void onSuccess(List<Registration> registrations);

        /**
         * Called when the registration list operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for basic operations without return values.
     */
    public interface OperationCallback {
        /**
         * Called when the operation is successful.
         */
        void onSuccess();

        /**
         * Called when the operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for operations that return a boolean result.
     */
    public interface BooleanCallback {
        /**
         * Called when the operation is successful.
         *
         * @param result the boolean result of the operation
         */
        void onSuccess(boolean result);

        /**
         * Called when the operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for count operations.
     */
    public interface CountCallback {
        /**
         * Called when the count operation is successful.
         *
         * @param count the integer count result
         */
        void onSuccess(int count);

        /**
         * Called when the count operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Private constructor for Singleton pattern.
     * Initializes Firebase Firestore instance.
     */
    private RegistrationRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Gets the singleton instance of RegistrationRepository.
     *
     * @return the singleton RegistrationRepository instance
     */
    public static synchronized RegistrationRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationRepository();
        }
        return instance;
    }

    /**
     * Converts a Firestore DocumentSnapshot to a Registration object.
     *
     * @param document the Firestore document snapshot to convert
     * @return the converted Registration object, or null if conversion fails
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

        // Location fields
        Double latitude = document.getDouble("latitude");
        Double longitude = document.getDouble("longitude");
        String locationAddress = document.getString("locationAddress");

        registration.setLatitude(latitude);
        registration.setLongitude(longitude);
        registration.setLocationAddress(locationAddress);

        return registration;
    }

    /**
     * Converts a Registration object to a Firestore data map.
     *
     * @param registration the Registration object to convert
     * @return a Map containing the registration data for Firestore
     */
    private Map<String, Object> registrationToMap(Registration registration) {
        Map<String, Object> registrationMap = new HashMap<>();
        registrationMap.put("id", registration.getId());
        registrationMap.put("eventId", registration.getEventId());
        registrationMap.put("entrantId", registration.getEntrantId());
        registrationMap.put("status", registration.getStatus() != null ? registration.getStatus().name() : null);
        registrationMap.put("registeredAt", registration.getRegisteredAt());
        registrationMap.put("respondedAt", registration.getRespondedAt());
        registrationMap.put("cancelledAt", registration.getCancelledAt());
        registrationMap.put("latitude", registration.getLatitude());
        registrationMap.put("longitude", registration.getLongitude());
        registrationMap.put("locationAddress", registration.getLocationAddress());
        registrationMap.put("updatedAt", new Date());

        return registrationMap;
    }

    /**
     * Finds a registration by its unique identifier.
     *
     * @param registrationId the unique identifier of the registration to find
     * @param callback the callback to handle the result of the operation
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
     * Finds a registration by event ID and user ID.
     *
     * @param eventId the unique identifier of the event
     * @param userId the unique identifier of the user
     * @param callback the callback to handle the result of the operation
     */
    public void findRegistrationByEventAndUser(String eventId, String userId, RegistrationCallback callback) {
        queryRegistrationByEventAndUser(eventId, userId)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Registration registration = querySnapshot.getDocuments().get(0).toObject(Registration.class);
                        callback.onSuccess(registration);
                    } else {
                        callback.onSuccess(null); // No registration found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all registrations for a specific event.
     *
     * @param eventId the unique identifier of the event
     * @param callback the callback to handle the list of registrations
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
     * Retrieves all registrations for a specific entrant.
     *
     * @param entrantId the unique identifier of the entrant
     * @param callback the callback to handle the list of registrations
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
     * Retrieves registrations by status for a specific event.
     *
     * @param eventId the unique identifier of the event
     * @param status the registration status to filter by
     * @param callback the callback to handle the list of registrations
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
     * Retrieves registrations by entrant and status.
     *
     * @param entrantId the unique identifier of the entrant
     * @param status the registration status to filter by
     * @param callback the callback to handle the list of registrations
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
     * Retrieves all registrations from the database.
     *
     * @param callback the callback to handle the list of registrations
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
     * Adds a new registration to the database.
     *
     * @param registration the Registration object to add
     * @param callback the callback to handle the operation result
     */
    public void addRegistration(Registration registration, OperationCallback callback) {
        if (registration.getId() == null || registration.getId().isEmpty()) {
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
     * Updates an existing registration in the database.
     *
     * @param updatedRegistration the Registration object with updated information
     * @param callback the callback to handle the operation result
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
     * Deletes a registration from the database by its ID.
     *
     * @param registrationId the unique identifier of the registration to delete
     * @param callback the callback to handle the operation result
     */
    public void deleteRegistration(String registrationId, BooleanCallback callback) {
        db.collection(COLLECTION_REGISTRATIONS)
                .document(registrationId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Creates a query for registration count by status for an event.
     *
     * @param eventId the unique identifier of the event
     * @param status the registration status to count
     * @return a Query object for the specified criteria
     */
    private Query queryRegistrationCountByStatus(String eventId, EntrantRegistrationStatus status) {
        return db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", status.name());
    }

    /**
     * Gets count of registrations by status for an event.
     *
     * @param eventId the unique identifier of the event
     * @param status the registration status to count
     * @param callback the callback to handle the count result
     */
    public void getRegistrationCountByStatus(String eventId, EntrantRegistrationStatus status, CountCallback callback) {
        queryRegistrationCountByStatus(eventId, status)
                .get()
                .addOnSuccessListener(querySnapshot -> callback.onSuccess(querySnapshot.size()))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Sets up a real-time listener for registration count by status.
     *
     * @param eventId the unique identifier of the event
     * @param status the registration status to monitor
     * @param callback the callback to handle count updates
     */
    public void watchRegistrationCountByStatus(String eventId, EntrantRegistrationStatus status, CountCallback callback) {
        queryRegistrationCountByStatus(eventId, status)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (querySnapshot != null) {
                        callback.onSuccess(querySnapshot.size());
                    }
                    if (error != null) {
                        callback.onFailure(error);
                    }
                });
    }

    /**
     * Retrieves waiting registrations for an event.
     *
     * @param eventId the unique identifier of the event
     * @param callback the callback to handle the list of waiting registrations
     */
    public void getWaitingRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        getRegistrationsByStatus(eventId, EntrantRegistrationStatus.WAITING, callback);
    }

    /**
     * Retrieves selected registrations for an event.
     *
     * @param eventId the unique identifier of the event
     * @param callback the callback to handle the list of selected registrations
     */
    public void getSelectedRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        getRegistrationsByStatus(eventId, EntrantRegistrationStatus.SELECTED, callback);
    }

    /**
     * Retrieves confirmed registrations for an event.
     *
     * @param eventId the unique identifier of the event
     * @param callback the callback to handle the list of confirmed registrations
     */
    public void getConfirmedRegistrationsByEvent(String eventId, RegistrationListCallback callback) {
        getRegistrationsByStatus(eventId, EntrantRegistrationStatus.CONFIRMED, callback);
    }

    /**
     * Checks if a user is registered for an event.
     *
     * @param eventId the unique identifier of the event
     * @param userId the unique identifier of the user
     * @param callback the callback to handle the boolean result
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

    /**
     * Creates a query for registration by event and user.
     *
     * @param eventId the unique identifier of the event
     * @param userId the unique identifier of the user
     * @return a Task containing the query result
     */
    private Task<QuerySnapshot> queryRegistrationByEventAndUser(String eventId, String userId) {
        return db.collection(COLLECTION_REGISTRATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("entrantId", userId)
                .limit(1)
                .get();
    }

    /**
     * Registers a user for an event.
     * Creates a new registration with WAITING status if the user is not already registered.
     *
     * @param eventId the unique identifier of the event
     * @param userId the unique identifier of the user
     * @param callback the callback to handle the registration result
     */
    public void registerUser(String eventId, String userId, RegistrationCallback callback) {
        findRegistrationByEventAndUser(eventId, userId, new RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    // Already registered so nothing to do.
                    callback.onSuccess(registration);
                } else {
                    final Registration newRegistration = new Registration();
                    DocumentReference docRef = db.collection(COLLECTION_REGISTRATIONS).document();
                    newRegistration.setId(docRef.getId());
                    newRegistration.setEventId(eventId);
                    newRegistration.setEntrantId(userId);
                    newRegistration.setStatus(EntrantRegistrationStatus.WAITING);
                    newRegistration.setRegisteredAt(new Date());

                    docRef.set(newRegistration)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(newRegistration))
                            .addOnFailureListener(callback::onFailure);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Registers a user for an event with location data.
     * Creates a new registration with WAITING status and location information if user is not already registered.
     *
     * @param eventId unique identifier of the event
     * @param userId unique identifier of the user
     * @param location the user's current location (null if geolocation not required)
     * @param callback callback to handle the registration result
     */
    public void registerUser(String eventId, String userId, Location location, RegistrationCallback callback) {
        findRegistrationByEventAndUser(eventId, userId, new RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    // Already registered so nothing to do.
                    callback.onSuccess(registration);
                } else {
                    final Registration newRegistration = new Registration();
                    DocumentReference docRef = db.collection(COLLECTION_REGISTRATIONS).document();
                    newRegistration.setId(docRef.getId());
                    newRegistration.setEventId(eventId);
                    newRegistration.setEntrantId(userId);
                    newRegistration.setStatus(EntrantRegistrationStatus.WAITING);
                    newRegistration.setRegisteredAt(new Date());

                    // Add location data if provided
                    if (location != null) {
                        newRegistration.setLatitude(location.getLatitude());
                        newRegistration.setLongitude(location.getLongitude());

                        // Store a simple coordinate string as location address for now
                        // The full address will be generated in the UI layer
                        String coordinateString = String.format("Lat: %.6f, Lng: %.6f",
                                location.getLatitude(), location.getLongitude());
                        newRegistration.setLocationAddress(coordinateString);
                    }

                    docRef.set(newRegistration)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(newRegistration))
                            .addOnFailureListener(callback::onFailure);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Removes the user's registration record for the specified event.
     *
     * @param eventId the unique identifier of the event
     * @param userId the unique identifier of the user
     * @param callback the callback to handle the unregistration result
     */
    public void unregisterUser(String eventId, String userId, RegistrationCallback callback) {
        findRegistrationByEventAndUser(eventId, userId, new RegistrationCallback() {
            @Override
            public void onSuccess(Registration registration) {
                if (registration != null) {
                    queryRegistrationByEventAndUser(eventId, userId)
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                    db.collection(COLLECTION_REGISTRATIONS)
                                            .document(doc.getId())
                                            .delete()
                                            .addOnSuccessListener(v -> {
                                                callback.onSuccess(null);
                                            })
                                            .addOnFailureListener(callback::onFailure);
                                } else {
                                    // Not registered yet so nothing to do
                                    callback.onSuccess(null);
                                }
                            })
                            .addOnFailureListener(callback::onFailure);
                } else {
                    // Not yet registered so nothing to do.
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });

    }
    public void acceptInvitation(String registrationId, RegistrationCallback callback) {
        if (registrationId == null || registrationId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid registrationId"));
            return;
        }

        DocumentReference regRef = db.collection("registrations").document(registrationId);

        regRef.update(
                "status", "CONFIRMED",
                "respondedAt", new Date()
        ).addOnSuccessListener(unused ->
                regRef.get().addOnSuccessListener(doc -> {
                    Registration updated = doc.toObject(Registration.class);
                    callback.onSuccess(updated);
                })
        ).addOnFailureListener(callback::onFailure);
    }

}