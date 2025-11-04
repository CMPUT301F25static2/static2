package com.ualberta.eventlottery.repository;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.RegistrationStatus;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static EventRepository instance;
    private List<Event> eventCache;

    private EventRepository() {
        eventCache = new ArrayList<>();
        initializeSampleData();
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
        event1.setRegistrationStatus(RegistrationStatus.REGISTRATION_OPEN);
        event1.setDescription("Relaxing morning yoga for all levels");
        event1.setMaxAttendees(50);
        event1.setCategory("Health & Wellness");

        Event event2 = new Event();
        event2.setId("2");
        event2.setTitle("Tech Conference 2024");
        event2.setEventStatus(EventStatus.ONGOING);
        event2.setRegistrationStatus(RegistrationStatus.REGISTRATION_CLOSED);
        event2.setDescription("Annual technology conference");
        event2.setMaxAttendees(200);
        event2.setCategory("Technology");

        Event event3 = new Event();
        event3.setId("3");
        event3.setTitle("Charity Run");
        event3.setEventStatus(EventStatus.CLOSED);
        event3.setRegistrationStatus(RegistrationStatus.REGISTRATION_CLOSED);
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


    public List<Event> getEventsByOrganizer(String organizerId) {
        List<Event> result = new ArrayList<>();
        for (Event event : eventCache) {
            if (event.getOrganizerId() != null && event.getOrganizerId().equals(organizerId)) {
                result.add(event);
            }
        }
        return result;
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


}