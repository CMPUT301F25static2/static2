package com.ualberta.eventlottery.repository;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        Calendar calendar = Calendar.getInstance();

        // example 1: Registration open + upcoming event
        Event event1 = new Event();
        event1.setId("1");
        event1.setTitle("Morning Yoga Session");
        event1.setDescription("Relaxing morning yoga for all levels");
        event1.setMaxAttendees(50);
        event1.setCategory("Health & Wellness");

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2); // starts in 2 days
        event1.setStartTime(calendar.getTime());
        calendar.add(Calendar.HOUR, 2);
        event1.setEndTime(calendar.getTime());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1); // registration started 1 day ago
        event1.setRegistrationStart(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 4); // registration ends in 3 days
        event1.setRegistrationEnd(calendar.getTime());

        // example 2: Registration closed + ongoing event
        Event event2 = new Event();
        event2.setId("2");
        event2.setTitle("Tech Conference 2024");
        event2.setDescription("Annual technology conference");
        event2.setMaxAttendees(200);
        event2.setCategory("Technology");

        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1); // started 1 hour ago
        event2.setStartTime(calendar.getTime());
        calendar.add(Calendar.HOUR, 3); // ends in 2 hours
        event2.setEndTime(calendar.getTime());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7); // registration started 7 days ago
        event2.setRegistrationStart(calendar.getTime());
        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -2); // registration ended 2 hours ago
        event2.setRegistrationEnd(calendar.getTime());

        // example 3: finished event
        Event event3 = new Event();
        event3.setId("3");
        event3.setTitle("Charity Run");
        event3.setDescription("5K run for charity");
        event3.setMaxAttendees(100);
        event3.setCategory("Sports");

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7); // started 7 days ago
        event3.setStartTime(calendar.getTime());
        calendar.add(Calendar.HOUR, 3);
        event3.setEndTime(calendar.getTime());

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -14); // registration started 14 days ago
        event3.setRegistrationStart(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 7); // registration ended 7 days ago
        event3.setRegistrationEnd(calendar.getTime());

        eventCache.add(event1);
        eventCache.add(event2);
        eventCache.add(event3);

        // update status for all events during initialization
        updateAllEventsStatus();
    }

    private void updateEventStatus(Event event) {
        if (event == null) {
            return;
        }

        Date now = new Date();

        // update event status based on current time
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

        // update event registration time based on current time
        if (event.getRegistrationStart() != null && event.getRegistrationEnd() != null) {
            Date regStart = event.getRegistrationStart();
            Date regEnd = event.getRegistrationEnd();

            if (now.before(regStart)) {
                // registration hasn't began
                event.setEventStatus(EventStatus.UPCOMING);
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
            } else if (now.after(regStart) && now.before(regEnd)) {
                // within registration time, check and set event status
                if (!event.isEventFull()) {
                    event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
                } else {
                    event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
                }
            } else if (now.after(regEnd)) {
                // registration has ended
                event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
            }
        }

        // if event is ongoing or closed, set registration to closed
//        if (event.getEventStatus() == EventStatus.ONGOING ||
//                event.getEventStatus() == EventStatus.CLOSED) {
//            event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
//        }

        // if event is full, set registration to closed
        if (event.isEventFull()) {
            event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        }
    }

    private void updateAllEventsStatus() {
        for (Event event : eventCache) {
            updateEventStatus(event);
        }
    }

    public void refreshEventsStatus() {
        updateAllEventsStatus();
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
        updateAllEventsStatus();
        return new ArrayList<>(eventCache);
    }


    public List<Event> getEventsByOrganizer(String organizerId) {
        updateAllEventsStatus();
        List<Event> result = new ArrayList<>();
        for (Event event : eventCache) {
            if (event.getOrganizerId() != null && event.getOrganizerId().equals(organizerId)) {
                result.add(event);
            }
        }
        return result;
    }


    public void addEvent(Event event) {
        updateEventStatus(event);
        eventCache.add(event);
    }


    public boolean updateEvent(Event updatedEvent) {
//        updateEventStatus(updatedEvent);
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