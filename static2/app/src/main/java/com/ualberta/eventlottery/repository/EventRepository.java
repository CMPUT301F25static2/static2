package com.ualberta.eventlottery.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepository {
    private static EventRepository instance;
    private FirebaseFirestore db;
    private static final String COLLECTION_EVENTS = "events";

    // callback interfaces
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

    public interface BooleanCallback {
        void onSuccess(boolean result);
        void onFailure(Exception e);
    }

    private EventRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    /**
     * Converts Firestore document to Event object
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
        event.setEventStart(document.getDate("eventStart"));
        event.setEventEnd(document.getDate("eventEnd"));
        event.setRegistrationStart(document.getDate("registrationStart"));
        event.setRegistrationEnd(document.getDate("registrationEnd"));

        String dailyStartTimeStr = document.getString("dailyStartTime");
        String dailyEndTimeStr = document.getString("dailyEndTime");

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
        Long currentAttendees = document.getLong("currentAttendees");
        if (currentAttendees != null) {
            event.setCurrentAttendees(currentAttendees.intValue());
        }

        return event;
    }

    /**
     * Converts Event object to Firestore data map
     */
    private Map<String, Object> eventToMap(Event event) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", event.getTitle());
        eventMap.put("description", event.getDescription());
        eventMap.put("maxAttendees", event.getMaxAttendees());
        eventMap.put("category", event.getCategory());
        eventMap.put("organizerId", event.getOrganizerId());
        eventMap.put("eventStart", event.getEventStart());
        eventMap.put("eventEnd", event.getEventEnd());
        eventMap.put("registrationStart", event.getRegistrationStart());
        eventMap.put("registrationEnd", event.getRegistrationEnd());
        eventMap.put("dailyStartTime", event.getDailyStartTime() != null ? event.getDailyStartTime().toString() : null);
        eventMap.put("dailyEndTime", event.getDailyEndTime() != null ? event.getDailyEndTime().toString() : null);
        eventMap.put("currentAttendees", event.getCurrentAttendees());
        eventMap.put("eventStatus", event.getEventStatus() != null ? event.getEventStatus().toString() : null);
        eventMap.put("registrationStatus", event.getRegistrationStatus() != null ? event.getRegistrationStatus().toString() : null);
        eventMap.put("updatedAt", new Date());


        return eventMap;
    }

    /**
     * Updates event status based on current time and registration period
     */
    private void updateEventStatus(Event event) {
        if (event == null) {
            return;
        }

        Date now = new Date();

        // Update event status based on current time
        if (event.getEventStart() != null && event.getEventEnd() != null) {
            Date start = event.getEventStart();
            Date end = event.getEventEnd();

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

            if (now.before(regStart)) {
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
            } else if (now.after(regStart) && now.before(regEnd)) {
                if (!event.isEventFull()) {
                    event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
                } else {
                    event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
                }
            } else if (now.after(regEnd)) {
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
            }
        }

        // If event is full, set registration to closed
        if (event.isEventFull()) {
            event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        }
    }

    /**
     * Finds an event by its ID
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
     * Retrieves all events from the database
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

    /**
     * Retrieves events by organizer ID
     */
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

    /**
     * Adds a new event to the database
     */
    public void addEvent(Event event, OperationCallback callback) {
        updateEventStatus(event);

        if (event.getId() == null || event.getId().isEmpty()) {
            String newId = db.collection(COLLECTION_EVENTS).document().getId();
            event.setId(newId);
        }

        Map<String, Object> eventData = eventToMap(event);
        db.collection(COLLECTION_EVENTS)
                .document(event.getId())
                .set(eventData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Updates an existing event
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
     * Deletes an event by ID
     */
    public void deleteEvent(String eventId, BooleanCallback callback) {
        db.collection(COLLECTION_EVENTS)
                .document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Retrieves events with open registration
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
}