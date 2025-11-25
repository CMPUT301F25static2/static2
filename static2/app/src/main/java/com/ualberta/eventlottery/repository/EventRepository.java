package com.ualberta.eventlottery.repository;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;

import java.io.ByteArrayOutputStream;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repository class for managing Event data operations with Firebase Firestore.
 * This class follows the Singleton pattern to provide a single instance
 * for handling all event-related database operations including CRUD operations
 *
 * @author TeamName
 * @version 1.0
 */
public class EventRepository {
    private static EventRepository instance;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static final String COLLECTION_EVENTS = "events";
    private static final String STORAGE_PATH_POSTERS = "event_posters/";
    static final String STORAGE_PATH_QR_CODES = "event_qr_codes/";

    // Callback interfaces

    /**
     * Callback interface for single Event operations.
     */
    public interface EventCallback {
        /**
         * Called when the event operation is successful.
         *
         * @param event the retrieved or processed Event object
         */
        void onSuccess(Event event);

        /**
         * Called when the event operation fails.
         *
         * @param e the exception that caused the failure
         */
        void onFailure(Exception e);
    }

    /**
     * Callback interface for multiple Event operations.
     */
    public interface EventListCallback {
        /**
         * Called when the event list operation is successful.
         *
         * @param events the list of retrieved Event objects
         */
        void onSuccess(List<Event> events);

        /**
         * Called when the event list operation fails.
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
     * Callback interface for poster update operations that return the new poster URL.
     */
    public interface PosterUpdateCallback {
        /**
         * Called when the poster update is successful.
         *
         * @param posterUrl the new poster URL
         */
        void onSuccess(String posterUrl);

        /**
         * Called when the poster update fails.
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
     * Initializes Firebase Firestore instance.
     */
    private EventRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Gets the singleton instance of EventRepository.
     *
     * @return the singleton EventRepository instance
     */
    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    /**
     * Converts a Firestore DocumentSnapshot to an Event object.
     *
     * @param document the Firestore document snapshot to convert
     * @return the converted Event object, or null if conversion fails
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Event documentToEvent(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        Event event = new Event();
        event.setId(document.getId());
        event.setTitle(document.getString("title"));
        event.setDescription(document.getString("description"));
        event.setMaxAttendees(document.getLong("maxAttendees").intValue());
        event.setCategory(document.getString("category"));
        event.setOrganizerId(document.getString("organizerId"));

        // Date fields
        event.setStartTime(document.getDate("eventStart"));
        event.setEndTime(document.getDate("eventEnd"));
        event.setRegistrationStart(document.getDate("registrationStart"));
        event.setRegistrationEnd(document.getDate("registrationEnd"));

        String dailyStartTimeStr = document.getString("dailyStartTime");
        String dailyEndTimeStr = document.getString("dailyEndTime");

        event.setPosterUrl(document.getString("posterUrl"));

        if (dailyStartTimeStr != null) {
            try {
                LocalTime startTime = LocalTime.parse(dailyStartTimeStr);
                event.setDailyStartTime(startTime);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }

        if (dailyEndTimeStr != null) {
            try {
                LocalTime endTime = LocalTime.parse(dailyEndTimeStr);
                event.setDailyEndTime(endTime);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }

        // status fields
        String eventStatus = document.getString("eventStatus");
        if (eventStatus != null) {
            event.setEventStatus(EventStatus.valueOf(eventStatus));
        }

        String registrationStatus = document.getString("registrationStatus");
        if (registrationStatus != null) {
            event.setRegistrationStatus(EventRegistrationStatus.valueOf(registrationStatus));
        }

        // current attendees count
        Long confirmedAttendees = document.getLong("confirmedAttendees");
        if (confirmedAttendees != null) {
            event.setConfirmedAttendees(confirmedAttendees.intValue());
        }



        // User lists
        List<String> confirmedUserIds = (List<String>) document.get("confirmedUserIds");
        if (confirmedUserIds != null) {
            event.setConfirmedUserIds(confirmedUserIds);
        }


        List<String> waitListUserIds = (List<String>) document.get("waitListUserIds");
        if (waitListUserIds != null) {
            event.setWaitListUserIds(waitListUserIds);
        }

        List<String> registeredUserIds = (List<String>) document.get("registeredUserIds");
        if (registeredUserIds != null) {
            event.setRegisteredUserIds(registeredUserIds);
        }

        // location required
        Boolean locationRequired = document.getBoolean("locationRequired");
        event.setLocationRequired(locationRequired != null ? locationRequired : true);

        return event;
    }

    /**
     * Static method to convert a Firestore DocumentSnapshot to an Event object.
     *
     * @param document the Firestore document snapshot to convert
     * @return the converted Event object, or null if conversion fails
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Event fromDocument(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }

        Event event = new Event();
        try {
            event.setId(document.getId());
            event.setOrganizerId(document.getString("organizerId"));
            event.setTitle(document.getString("title"));
            event.setDescription(document.getString("description"));
            event.setCategory(document.getString("category"));
            Double price = document.getDouble("price");
            if (price != null) {
                event.setPrice(price);
            }
            Long sessionDuration = document.getLong("sessionDuration");
            if (sessionDuration != null) {
                event.setSessionDuration(sessionDuration.intValue());
            }
            event.setLocation(document.getString("location"));
            event.setLocationRequired(document.getBoolean("locationRequired") != null ? document.getBoolean("locationRequired") : true);
            event.setLocationUrl(document.getString("locationUrl"));
            event.setPosterUrl(document.getString("posterUrl"));
            event.setQrCodeUrl(document.getString("qrCodeUrl"));

            event.setCreatedAt(document.getDate("createdAt"));

            String eventStatus = document.getString("eventStatus");
            if (eventStatus != null) {
                event.setEventStatus(EventStatus.valueOf(eventStatus));
            }
            String eventRegistrationStatus = document.getString("registrationStatus");
            if (eventRegistrationStatus != null) {
                event.setRegistrationStatus(EventRegistrationStatus.valueOf(eventRegistrationStatus));
            }

            event.setRegistrationStart(document.getDate("registrationStart"));
            event.setRegistrationEnd(document.getDate("registrationEnd"));

            event.setStartTime(document.getDate("startTime"));
            event.setEndTime(document.getDate("endTime"));
            String dailyStartTimeStr = document.getString("dailyStartTime");
            if (dailyStartTimeStr != null) {
                LocalTime dailyStartTime = LocalTime.parse(dailyStartTimeStr);
                event.setDailyStartTime(dailyStartTime);
            }

            Long maxAttendees = document.getLong("maxAttendees");
            if (maxAttendees != null) {
                event.setMaxAttendees(maxAttendees.intValue());
            }
            Long maxWaitListSize = document.getLong("maxWaitListSize");
            if (maxWaitListSize != null) {
                event.setMaxWaitListSize(maxWaitListSize.intValue());
            }
            List<String> waitListUserIds = (List<String>) document.get("waitListUserIds");
            event.setWaitListUserIds(waitListUserIds);
            if (waitListUserIds != null) {
                event.setCurrentWaitListSize(waitListUserIds.size());
            }

            List<String> confirmedUserIds = (List<String>) document.get("confirmedUserIds");
            if (confirmedUserIds != null) {
                event.setConfirmedUserIds(confirmedUserIds);
            }

            List<String> registeredUserIds = (List<String>) document.get("registeredUserIds");
            if (registeredUserIds != null) {
                event.setRegisteredUserIds(registeredUserIds);
            }
        } catch (Exception e) {
            Log.e("EventLottery", "failed to convert document to event", e);
        }

        return event;
    }

    /**
     * Converts an Event object to a Firestore data map.
     *
     * @param event the Event object to convert
     * @return a Map containing the event data for Firestore storage
     */
    private Map<String, Object> eventToMap(Event event) {
        Map<String, Object> eventMap = new HashMap<>();
//        eventMap.put("id", event.getId());
        eventMap.put("title", event.getTitle());
        eventMap.put("description", event.getDescription());
        eventMap.put("maxAttendees", event.getMaxAttendees());
        eventMap.put("category", event.getCategory());
        eventMap.put("organizerId", event.getOrganizerId());
        eventMap.put("eventStart", event.getStartTime());
        eventMap.put("eventEnd", event.getEndTime());
        eventMap.put("registrationStart", event.getRegistrationStart());
        eventMap.put("registrationEnd", event.getRegistrationEnd());
        eventMap.put("dailyStartTime", event.getDailyStartTime() != null ? event.getDailyStartTime().toString() : null);
        eventMap.put("dailyEndTime", event.getDailyEndTime() != null ? event.getDailyEndTime().toString() : null);
        eventMap.put("currentAttendees", event.getConfirmedCount());
        eventMap.put("confirmedUserIds", event.getConfirmedUserIds());
        eventMap.put("waitListUserIds", event.getWaitListUserIds());
        eventMap.put("registeredUserIds", event.getRegisteredUserIds());
        eventMap.put("eventStatus", event.getEventStatus() != null ? event.getEventStatus().toString() : null);
        eventMap.put("registrationStatus", event.getRegistrationStatus() != null ? event.getRegistrationStatus().toString() : null);
        eventMap.put("posterUrl", event.getPosterUrl());
        eventMap.put("qrCodeUrl", event.getQrCodeUrl());
        eventMap.put("location", event.getLocation());
        eventMap.put("locationRequired", event.isLocationRequired());
        eventMap.put("locationUrl", event.getLocationUrl());
        eventMap.put("createdAt", new Date());
        eventMap.put("updatedAt", new Date());

        return eventMap;
    }

    /**
     * Updates event status based on current time and registration period.
     * Automatically determines if an event is UPCOMING, ONGOING, or CLOSED based on the current system time and event timing.
     *
     * @param event the Event object to update status for
     */
    private void updateEventStatus(Event event) {
        if (event == null) {
            return;
        }

        Date now = new Date();

        // Update event status based on current time
        if (event.getStartTime() != null && event.getEndTime() != null) {
            Date start = event.getStartTime();
            Date end = event.getEndTime();

            if (now.before(start)) {
                event.setEventStatus(EventStatus.UPCOMING);
            } else if (now.after(start) && now.before(end)) {
                event.setEventStatus(EventStatus.ONGOING);
            } else if (now.after(end)) {
                event.setEventStatus(EventStatus.CLOSED);
            }
        }

        // Update registration status
        if (event.getRegistrationStart() != null && event.getRegistrationEnd() != null) {
            Date regStart = event.getRegistrationStart();
            Date regEnd = event.getRegistrationEnd();

            if (now.after(regEnd)) {
                // Case 1: Registration period has ended.
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
            } else if (now.after(regStart) && now.before(regEnd)) {
                // Case 2: We are within the registration period.
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
            }

        }

        // If event is full, set registration to closed
        if (event.isEventFull()) {
            event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        }
    }

    /**
     * Finds an event by its unique identifier.
     *
     * @param eventId the unique identifier of the event to find
     * @param callback the callback to handle the result of the operation
     */
    public void findEventById(String eventId, EventCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .document(eventId)
                .get()
                .addOnSuccessListener(document -> {
                    Event event = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        event = documentToEvent(document);
                    }
                    if (event != null) {
                        updateEventStatus(event);
                        callback.onSuccess(event);
                    } else {
                        callback.onFailure(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves all events from the database.
     *
     * @param callback the callback to handle the list of events
     */
    public void getAllEvents(EventListCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Event event = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            event = documentToEvent(document);
                        }
                        if (event != null) {
                            updateEventStatus(event);
                            events.add(event);
                        }
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }


    public LiveData<Event> getEventById(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {

            MutableLiveData<Event> nullEventData = new MutableLiveData<>();
            nullEventData.setValue(null);
            return nullEventData;
        }
        // Create a reference to the specific document in the events collection
        DocumentReference docRef = db.collection(COLLECTION_EVENTS).document(eventId);


        return new EventLiveData(docRef);
    }

    /**
     * Retrieves events by organizer ID.
     *
     * @param organizerId the unique identifier of the organizer
     * @param callback the callback to handle the list of events
     */
    public void getEventsByOrganizer(String organizerId, EventListCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .whereEqualTo("organizerId", organizerId)
//                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Event event = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            event = documentToEvent(document);
                        }
                        if (event != null) {
                            updateEventStatus(event);
                            events.add(event);
                        }
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }



    public void addEventWithPoster(Event event, Uri imageUri, OperationCallback callback) {
        String posterFilename = UUID.randomUUID().toString();
        StorageReference posterFileRef = storage.getReference().child(STORAGE_PATH_POSTERS + posterFilename);

        // Upload poster file and get its URL
        posterFileRef.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return posterFileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                event.setPosterUrl(downloadUri.toString());

                // Add the event to Firestore (with a null qrCodeUrl for now)
                db.collection(COLLECTION_EVENTS).add(eventToMap(event))
                        .addOnSuccessListener(documentReference -> {
                            String eventId = documentReference.getId();

                            // Generate QR, get its URL, and then perform a single update
                            generateAndUploadQrCode(documentReference, eventId, callback);
                        })
                        .addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Generates a QR code, uploads it, and performs a final, consolidated update
     * to the event document with both the qrCodeUrl and the eventId.
     * THIS IS THE CORRECTED LOGIC TO AVOID RACE CONDITIONS.
     *
     * @param documentReference The reference to the newly created event document.
     * @param eventId The unique ID of the event.
     * @param callback The final callback for the entire operation.
     */
    private void generateAndUploadQrCode(DocumentReference documentReference, String eventId, OperationCallback callback) {
        try {
            // 1. Define the content for the QR code
            String qrContent = "eventlottery://event?id=" + eventId;

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 512, 512);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // 2. Create a reference in Firebase Storage
            String qrCodeFilename = eventId + "_promo.png";
            StorageReference qrCodeRef = storage.getReference().child(STORAGE_PATH_QR_CODES + qrCodeFilename);

            // 3. Upload the QR code and get its URL
            qrCodeRef.putBytes(data).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return qrCodeRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    String qrCodeUrl = downloadUrl.toString();

                    // 4. FINAL, CONSOLIDATED UPDATE: Update the document with BOTH id and qrCodeUrl
                    Map<String, Object> finalUpdates = new HashMap<>();
                    finalUpdates.put("id", eventId);
                    finalUpdates.put("qrCodeUrl", qrCodeUrl);

                    documentReference.update(finalUpdates)
                            .addOnSuccessListener(aVoid -> callback.onSuccess()) // The whole process is now successful
                            .addOnFailureListener(callback::onFailure);

                } else {
                    // QR upload failed, but the event exists. We still call onFailure for the QR step.
                    Log.e("EventRepo", "Failed to upload QR code. Event created without QR URL.", task.getException());
                    callback.onFailure(task.getException());
                }
            });

        } catch (Exception e) {
            Log.e("EventRepo", "Error generating QR code bitmap", e);
            callback.onFailure(e);
        }
    }






    /**
     * Updates an existing event in the database.
     *
     * @param updatedEvent the Event object with updated information
     * @param callback the callback to handle the operation result
     */
    public void updateEvent(Event updatedEvent, BooleanCallback callback) {
        updateEventStatus(updatedEvent);

        Map<String, Object> eventData = eventToMap(updatedEvent);
        db.collection(COLLECTION_EVENTS)
                .document(updatedEvent.getId())
                .update(eventData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Updates the poster URL for a specific event.
     * This method uploads the new image to Firebase Storage and updates the event's posterUrl field.
     *
     * @param eventId the unique identifier of the event to update
     * @param imageUri the URI of the new image to upload
     * @param callback the callback to handle the operation result
     */
    public void updateEventPoster(String eventId, Uri imageUri, OperationCallback callback) {
        // Generate a unique filename for the image
        String imageName = "event_poster_" + eventId + "_" + UUID.randomUUID().toString();
        StorageReference posterFileRef = storage.getReference().child(STORAGE_PATH_POSTERS + imageName);

        // Upload the image to Firebase Storage and get its download URL
        posterFileRef.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return posterFileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String posterUrl = downloadUri.toString();

                // Update the event document with the new poster URL
                db.collection(COLLECTION_EVENTS)
                        .document(eventId)
                        .update("posterUrl", posterUrl)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            } else {
                // Handle upload failure
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Updates an event's poster image and returns the new poster URL via callback.
     *
     * @param eventId the ID of the event to update
     * @param imageUri the URI of the new image to upload
     * @param callback callback to handle success/failure with the new poster URL
     */
    public void updateEventPoster(String eventId, Uri imageUri, PosterUpdateCallback callback) {
        // Generate a unique filename for the image
        String imageName = "event_poster_" + eventId + "_" + UUID.randomUUID().toString();
        StorageReference posterFileRef = storage.getReference().child(STORAGE_PATH_POSTERS + imageName);

        // Upload the image to Firebase Storage and get its download URL
        posterFileRef.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return posterFileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String posterUrl = downloadUri.toString();

                // Update the event document with the new poster URL
                db.collection(COLLECTION_EVENTS)
                        .document(eventId)
                        .update("posterUrl", posterUrl)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(posterUrl))
                        .addOnFailureListener(e -> callback.onFailure(e));
            } else {
                // Handle upload failure
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Deletes an event from the database by its ID.
     *
     * @param eventId the unique identifier of the event to delete
     * @param callback the callback to handle the operation result
     */
    public void deleteEvent(String eventId, BooleanCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Gets a LiveData object for events with open registration.
     *
     * @return EventListLiveData object for observing available events
     */
    public EventListLiveData getAvailableEvents() {
        // Create a query for events with open registration
        Query openRegistrationQuery = db.collection(COLLECTION_EVENTS)
                .whereEqualTo("registrationStatus", EventRegistrationStatus.REGISTRATION_OPEN.toString());

        return new EventListLiveData(openRegistrationQuery);
    }

    /**
     * Retrieves events with open registration.
     *
     * @param callback the callback to handle the list of available events
     */
    public void getEventsWithOpenRegistration(EventListCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Event event = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            event = documentToEvent(document);
                        }
                        if (event != null) {
                            updateEventStatus(event);
                            if (event.getRegistrationStatus() == EventRegistrationStatus.REGISTRATION_OPEN) {
                                events.add(event);
                            }
                        }
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }


    /**
     * Retrieves all events matching a list of event IDs.
     * @param eventIds List of event document IDs.
     * @param callback Callback to handle the list of events or failure.
     */
    public void getEventsByIds(List<String> eventIds, EventListCallback callback) {
        if (eventIds == null || eventIds.isEmpty()) {
            callback.onSuccess(new ArrayList<>()); // Return empty list if no IDs provided
            return;
        }

        db.collection(COLLECTION_EVENTS)
                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), eventIds)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Event event = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            event = fromDocument(document);
                        }
                        if (event != null) {
                            // updateEventStatus(event);
                            events.add(event);
                        }
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
