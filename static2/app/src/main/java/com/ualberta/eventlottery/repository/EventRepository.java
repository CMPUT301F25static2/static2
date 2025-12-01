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

public class EventRepository {
    private static EventRepository instance;
    public FirebaseFirestore db;
    public FirebaseStorage storage;
    private static final String COLLECTION_EVENTS = "events";
    private static final String STORAGE_PATH_POSTERS = "event_posters/";
    static final String STORAGE_PATH_QR_CODES = "event_qr_codes/";

    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }

    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }

    public interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface PosterUpdateCallback {
        void onSuccess(String posterUrl);
        void onFailure(Exception e);
    }

    public interface BooleanCallback {
        void onSuccess(boolean result);
        void onFailure(Exception e);
    }

    public EventRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event documentToEvent(DocumentSnapshot document) {
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
        Double price = document.getDouble("price");
        event.setPrice(price != null ? price : 0.0);

        event.setStartTime(document.getDate("eventStart"));
        event.setEndTime(document.getDate("eventEnd"));
        event.setRegistrationStart(document.getDate("registrationStart"));
        event.setRegistrationEnd(document.getDate("registrationEnd"));

        String dailyStartTimeStr = document.getString("dailyStartTime");
        String dailyEndTimeStr = document.getString("dailyEndTime");

        event.setPosterUrl(document.getString("posterUrl"));
        event.setQrCodeUrl(document.getString("qrCodeUrl"));


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

        String eventStatus = document.getString("eventStatus");
        if (eventStatus != null) {
            event.setEventStatus(EventStatus.valueOf(eventStatus));
        }

        String registrationStatus = document.getString("registrationStatus");
        if (registrationStatus != null) {
            event.setRegistrationStatus(EventRegistrationStatus.valueOf(registrationStatus));
        }

        Long confirmedAttendees = document.getLong("confirmedAttendees");
        if (confirmedAttendees != null) {
            event.setConfirmedAttendees(confirmedAttendees.intValue());
        }

        Boolean locationRequired = document.getBoolean("locationRequired");
        event.setLocationRequired(locationRequired != null ? locationRequired : true);


        return event;
    }

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
        } catch (Exception e) {
            Log.e("EventLottery", "failed to convert document to event", e);
        }

        return event;
    }

    public Map<String, Object> eventToMap(Event event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", event.getTitle());
        eventMap.put("description", event.getDescription());
        eventMap.put("maxAttendees", event.getMaxAttendees());
        eventMap.put("category", event.getCategory());
        eventMap.put("organizerId", event.getOrganizerId());
        eventMap.put("eventStart", event.getStartTime());
        eventMap.put("eventEnd", event.getEndTime());
        eventMap.put("price", event.getPrice());
        eventMap.put("registrationStart", event.getRegistrationStart());
        eventMap.put("registrationEnd", event.getRegistrationEnd());
        eventMap.put("dailyStartTime", event.getDailyStartTime() != null ? event.getDailyStartTime().toString() : null);
        eventMap.put("dailyEndTime", event.getDailyEndTime() != null ? event.getDailyEndTime().toString() : null);
        eventMap.put("confirmedAttendees", event.getConfirmedAttendees());
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

    public void updateEventStatus(Event event) {
        if (event == null) {
            return;
        }

        Date now = new Date();

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

        if (event.getRegistrationStart() != null && event.getRegistrationEnd() != null) {
            Date regStart = event.getRegistrationStart();
            Date regEnd = event.getRegistrationEnd();

            if (now.after(regEnd)) {
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
            } else if (now.after(regStart) && now.before(regEnd)) {
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
            }

        }

        if (event.isEventFull()) {
            event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        }
    }

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
        DocumentReference docRef = db.collection(COLLECTION_EVENTS).document(eventId);


        return new EventLiveData(docRef);
    }

    public void getEventsByOrganizer(String organizerId, EventListCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .whereEqualTo("organizerId", organizerId)
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

        posterFileRef.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return posterFileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                event.setPosterUrl(downloadUri.toString());

                db.collection(COLLECTION_EVENTS).add(eventToMap(event))
                        .addOnSuccessListener(documentReference -> {
                            String eventId = documentReference.getId();
                            event.setId(eventId);
                            generateAndUploadQrCode(documentReference, eventId, callback);
                        })
                        .addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    private void generateAndUploadQrCode(DocumentReference documentReference, String eventId, OperationCallback callback) {
        try {
            String qrContent = "eventlottery://com.ualberta.eventlottery/viewevent?eventId=" + eventId;

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrContent, BarcodeFormat.QR_CODE, 512, 512);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            String qrCodeFilename = eventId + "_promo.png";
            StorageReference qrCodeRef = storage.getReference().child(STORAGE_PATH_QR_CODES + qrCodeFilename);

            qrCodeRef.putBytes(data).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return qrCodeRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();
                    String qrCodeUrl = downloadUrl.toString();

                    Map<String, Object> finalUpdates = new HashMap<>();
                    finalUpdates.put("id", eventId);
                    finalUpdates.put("qrCodeUrl", qrCodeUrl);

                    documentReference.update(finalUpdates)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(callback::onFailure);

                } else {
                    Log.e("EventRepo", "Failed to upload QR code. Event created without QR URL.", task.getException());
                    callback.onFailure(task.getException());
                }
            });

        } catch (Exception e) {
            Log.e("EventRepo", "Error generating QR code bitmap", e);
            callback.onFailure(e);
        }
    }

    public void updateEvent(Event updatedEvent, BooleanCallback callback) {
        updateEventStatus(updatedEvent);

        Map<String, Object> eventData = eventToMap(updatedEvent);
        db.collection(COLLECTION_EVENTS)
                .document(updatedEvent.getId())
                .update(eventData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    public void updateEventPoster(String eventId, Uri imageUri, OperationCallback callback) {
        String imageName = "event_poster_" + eventId + "_" + UUID.randomUUID().toString();
        StorageReference posterFileRef = storage.getReference().child(STORAGE_PATH_POSTERS + imageName);

        posterFileRef.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return posterFileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String posterUrl = downloadUri.toString();

                db.collection(COLLECTION_EVENTS)
                        .document(eventId)
                        .update("posterUrl", posterUrl)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public void updateEventPoster(String eventId, Uri imageUri, PosterUpdateCallback callback) {
        String imageName = "event_poster_" + eventId + "_" + UUID.randomUUID().toString();
        StorageReference posterFileRef = storage.getReference().child(STORAGE_PATH_POSTERS + imageName);

        posterFileRef.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return posterFileRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String posterUrl = downloadUri.toString();

                db.collection(COLLECTION_EVENTS)
                        .document(eventId)
                        .update("posterUrl", posterUrl)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(posterUrl))
                        .addOnFailureListener(e -> callback.onFailure(e));
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public void deleteEvent(String eventId, BooleanCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    public EventListLiveData getAvailableEvents() {
        Query openRegistrationQuery = db.collection(COLLECTION_EVENTS)
                .whereEqualTo("registrationStatus", EventRegistrationStatus.REGISTRATION_OPEN.toString());

        return new EventListLiveData(openRegistrationQuery);
    }

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


    public void getEventsByIds(List<String> eventIds, EventListCallback callback) {
        if (eventIds == null || eventIds.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
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
                            events.add(event);
                        }
                    }
                    callback.onSuccess(events);
                })
                .addOnFailureListener(callback::onFailure);
    }
}