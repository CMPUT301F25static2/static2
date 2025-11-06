package com.ualberta.eventlottery.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static EventRepository instance;
    private List<Event> eventCache;
    private FirebaseFirestore db;

    private EventRepository() {
        eventCache = new ArrayList<>();
        initializeSampleData();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    private void initializeSampleData() {
        //TODO: initialize data from database
        Event event1 = new Event();
        event1.setId("1");
        event1.setTitle("Morning Yoga Session");
        event1.setEventStatus(EventStatus.UPCOMING);
        event1.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        event1.setDescription("Relaxing morning yoga for all levels");
        event1.setMaxAttendees(50);
        event1.setCategory("Health & Wellness");

        Event event2 = new Event();
        event2.setId("2");
        event2.setTitle("Tech Conference 2024");
        event2.setEventStatus(EventStatus.ONGOING);
        event2.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        event2.setDescription("Annual technology conference");
        event2.setMaxAttendees(200);
        event2.setCategory("Technology");

        Event event3 = new Event();
        event3.setId("3");
        event3.setTitle("Charity Run");
        event3.setEventStatus(EventStatus.CLOSED);
        event3.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        event3.setDescription("5K run for charity");
        event3.setMaxAttendees(100);
        event3.setCategory("Sports");

        eventCache.add(event1);
        eventCache.add(event2);
        eventCache.add(event3);
    }


    public Event findEventById(String eventId) {
        for (Event event : eventCache) {
            if (event.getId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(eventCache);
    }

    public EventListLiveData getAvailableEvents(){
        CollectionReference eventsRef = db.collection("events");

        // https://firebase.google.com/docs/firestore/query-data/queries
        return new EventListLiveData(eventsRef.whereEqualTo("eventRegistrationStatus","REGISTRATION_OPEN"));
    }

    public List<Event> getEventsByOrganizer(String organizerId) {
        List<Event> result = new ArrayList<>();
        for (Event event : eventCache) {
            if (event.getOrganizerId() != null && event.getOrganizerId().equals(organizerId)) {
                result.add(event);
            }
        }
        return result;

        // If reading from database use below:
        // CollectionReference eventsRef = db.collection("events");
        //
        // return new EventListLiveData(eventsRef.whereEqualTo("organizerId",organizerId));
    }


    public void addEvent(Event event) {
        eventCache.add(event);
    }


    public boolean updateEvent(Event updatedEvent) {
        for (int i = 0; i < eventCache.size(); i++) {
            if (eventCache.get(i).getId().equals(updatedEvent.getId())) {
                eventCache.set(i, updatedEvent);
                return true;
            }
        }
        return false;
    }

    public boolean deleteEvent(String eventId) {
        for (int i = 0; i < eventCache.size(); i++) {
            if (eventCache.get(i).getId().equals(eventId)) {
                eventCache.remove(i);
                return true;
            }
        }
        return false;
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
        } catch (Exception e) {
            Log.e("EventLottery", "failed to convert document to event", e);
        }

        return event;
    }
}