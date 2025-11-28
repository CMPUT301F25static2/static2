package com.ualberta.static2.organizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.Organizer;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrganizerBlackBoxTest {

    @Mock
    private EventRepository mockEventRepo;

    @Mock
    private RegistrationRepository mockRegRepo;

    private Organizer organizer;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        organizer = new Organizer("userId123", "John Doe", "john@example.com", "Test Org", "fcmToken123");
    }

    @Test
    public void testOrganizerCanCreateEvent() {
        // This test verifies the overall workflow of creating an event
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setOrganizerId("userId123");
        event.setStartTime(new Date());
        event.setEndTime(new Date());
        event.setRegistrationStart(new Date());
        event.setRegistrationEnd(new Date());
        event.setMaxAttendees(100);
        event.setPrice(25.50);

        // Simulate that the organizer creates an event
        // In a real implementation, this would call the EventService
        // For this blackbox test, we're verifying the organizer can properly set up event data

        assertEquals("Test Event", event.getTitle());
        assertEquals("Test Description", event.getDescription());
        assertEquals("userId123", event.getOrganizerId());
        assertEquals(100, event.getMaxAttendees());
        assertEquals(25.50, event.getPrice(), 0.01);
    }

    @Test
    public void testOrganizerEventManagement() {
        // Test that organizer can manage events through the system
        organizer.addCreatedEvent("event1");
        organizer.addCreatedEvent("event2");

        assertTrue(organizer.isEventOrganizer("event1"));
        assertTrue(organizer.isEventOrganizer("event2"));
        assertFalse(organizer.isEventOrganizer("event3"));

        assertEquals(2, organizer.getCreatedEventsCount());
    }

    @Test
    public void testEventLifecycle() {
        // Test event lifecycle from creation to status updates
        Event event = new Event();
        event.setId("eventId123");
        event.setTitle("Test Event");
        event.setOrganizerId("userId123");
        event.setStartTime(new Date(System.currentTimeMillis() - 1000000)); // Past
        event.setEndTime(new Date(System.currentTimeMillis() + 1000000)); // Future
        event.setRegistrationStart(new Date(System.currentTimeMillis() - 1000000)); // Past
        event.setRegistrationEnd(new Date(System.currentTimeMillis() + 1000000)); // Future
        event.setMaxAttendees(100);
        event.setConfirmedAttendees(50);
        event.setMaxWaitListSize(20);
        event.setCurrentWaitListSize(5);

        // Verify initial state
        assertEquals(EventStatus.ONGOING, event.getEventStatus());
        assertEquals(EventRegistrationStatus.REGISTRATION_OPEN, event.getRegistrationStatus());
        assertFalse(event.isEventFull());
        assertFalse(event.isWaitingListFull());

        // Update event to simulate end time passed
        event.setEndTime(new Date(System.currentTimeMillis() - 1000000)); // Past
        event.updateRegistrationStatusBasedOnDeadline();

        // Verify final state
        assertEquals(EventStatus.CLOSED, event.getEventStatus());
        assertEquals(EventRegistrationStatus.REGISTRATION_CLOSED, event.getRegistrationStatus());
    }

    @Test
    public void testRegistrationManagement() {
        // Test that organizer can manage event registrations
        Registration registration1 = new Registration();
        registration1.setId("reg1");
        registration1.setEventId("event1");
        registration1.setEntrantId("entrant1");
        registration1.setStatus(EntrantRegistrationStatus.WAITING);

        Registration registration2 = new Registration();
        registration2.setId("reg2");
        registration2.setEventId("event1");
        registration2.setEntrantId("entrant2");
        registration2.setStatus(EntrantRegistrationStatus.SELECTED);

        // Verify registration states
        assertEquals(EntrantRegistrationStatus.WAITING, registration1.getStatus());
        assertEquals(EntrantRegistrationStatus.SELECTED, registration2.getStatus());
    }

    @Test
    public void testOrganizerStatistics() {
        // Test that organizer can track statistics
        organizer.addCreatedEvent("event1");
        organizer.addCreatedEvent("event2");
        organizer.addCreatedEvent("event3");

        organizer.addJoinedEvent("event4");
        organizer.addJoinedEvent("event5");

        assertEquals(3, organizer.getCreatedEventsCount());
        assertEquals(2, organizer.getJoinedEventsCount());

        // Verify that the organizer correctly tracks their events
        assertTrue(organizer.isEventOrganizer("event1"));
        assertTrue(organizer.isEventOrganizer("event2"));
        assertTrue(organizer.isEventOrganizer("event3"));
        assertFalse(organizer.isEventOrganizer("event4"));
        assertFalse(organizer.isEventOrganizer("event5"));
    }

    @Test
    public void testEventFullAndWaitingListCapacity() {
        // Test event capacity management
        Event event = new Event();
        event.setMaxAttendees(10);
        event.setConfirmedAttendees(10); // Full
        event.setMaxWaitListSize(5);
        event.setCurrentWaitListSize(5); // Full

        assertTrue(event.isEventFull());
        assertTrue(event.isWaitingListFull());

        // Test partial capacity
        event.setConfirmedAttendees(8);
        event.setCurrentWaitListSize(3);

        assertFalse(event.isEventFull());
        assertFalse(event.isWaitingListFull());
    }

    @Test
    public void testRegistrationStatusManagement() {
        // Test registration status transitions
        Registration registration = new Registration();
        registration.setStatus(EntrantRegistrationStatus.WAITING);

        // Simulate status transitions
        registration.setStatus(EntrantRegistrationStatus.SELECTED);
        assertEquals(EntrantRegistrationStatus.SELECTED, registration.getStatus());

        registration.setStatus(EntrantRegistrationStatus.CONFIRMED);
        assertEquals(EntrantRegistrationStatus.CONFIRMED, registration.getStatus());

        registration.setStatus(EntrantRegistrationStatus.CANCELLED);
        assertEquals(EntrantRegistrationStatus.CANCELLED, registration.getStatus());
    }

    @Test
    public void testOrganizerEventCreationFlow() {
        // End-to-end test for organizer event creation flow
        // This simulates the sequence of operations an organizer would perform

        // 1. Organizer creates event
        Event event = organizer.createEvent(
            "Test Event",
            "Test Description",
            new Date(System.currentTimeMillis() - 1000000),
            new Date(System.currentTimeMillis() + 1000000),
            new Date(System.currentTimeMillis() - 1000000),
            new Date(System.currentTimeMillis() + 1000000),
            100,
            25.50
        );

        // 2. Organizer adds event to their created events list
        // Note: In the real implementation, this would be handled by the service layer
        // For this test, we're just verifying the event creation works
        assertNotNull(event);
        assertEquals("Test Event", event.getTitle());
        assertEquals("Test Description", event.getDescription());
        assertEquals("userId123", event.getOrganizerId());
    }
}