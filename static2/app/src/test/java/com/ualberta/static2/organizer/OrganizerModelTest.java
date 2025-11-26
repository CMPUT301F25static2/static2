package com.ualberta.static2.organizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ualberta.eventlottery.model.Organizer;
import com.ualberta.eventlottery.model.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

public class OrganizerModelTest {

    private Organizer organizer;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        organizer = new Organizer("userId123", "John Doe", "john@example.com", "Test Org", "fcmToken123");
    }

    @Test
    public void testOrganizerConstructor() {
        assertEquals("userId123", organizer.getUserId());
        assertEquals("John Doe", organizer.getName());
        assertEquals("john@example.com", organizer.getEmail());
        assertEquals("Test Org", organizer.getOrganizationName());
        assertEquals("fcmToken123", organizer.getFcmToken());
    }

    @Test
    public void testGettersAndSetters() {
        // Test setters
        organizer.setOrganizationName("New Organization");
        organizer.setContactInfo("contact@test.org");

        // Test getters
        assertEquals("New Organization", organizer.getOrganizationName());
        assertEquals("contact@test.org", organizer.getContactInfo());
    }

    @Test
    public void testAddCreatedEvent_Success() {
        assertTrue(organizer.addCreatedEvent("event1"));
        assertTrue(organizer.addCreatedEvent("event2"));
        assertFalse(organizer.addCreatedEvent("event1")); // Duplicate
        assertFalse(organizer.addCreatedEvent(null)); // Null event ID
        assertFalse(organizer.addCreatedEvent("")); // Empty event ID

        List<String> createdEvents = organizer.getCreatedEventIds();
        assertEquals(2, createdEvents.size());
        assertTrue(createdEvents.contains("event1"));
        assertTrue(createdEvents.contains("event2"));
    }

    @Test
    public void testRemoveCreatedEvent_Success() {
        organizer.addCreatedEvent("event1");
        organizer.addCreatedEvent("event2");

        assertTrue(organizer.removeCreatedEvent("event1"));
        assertFalse(organizer.removeCreatedEvent("event1")); // Already removed
        assertFalse(organizer.removeCreatedEvent("nonexistent")); // Doesn't exist

        List<String> createdEvents = organizer.getCreatedEventIds();
        assertEquals(1, createdEvents.size());
        assertEquals("event2", createdEvents.get(0));
    }

    @Test
    public void testAddJoinedEvent_Success() {
        assertTrue(organizer.addJoinedEvent("event1"));
        assertTrue(organizer.addJoinedEvent("event2"));
        assertFalse(organizer.addJoinedEvent("event1")); // Duplicate
        assertFalse(organizer.addJoinedEvent(null)); // Null event ID
        assertFalse(organizer.addJoinedEvent("")); // Empty event ID

        List<String> joinedEvents = organizer.getJoinedEventIds();
        assertEquals(2, joinedEvents.size());
        assertTrue(joinedEvents.contains("event1"));
        assertTrue(joinedEvents.contains("event2"));
    }

    @Test
    public void testRemoveJoinedEvent_Success() {
        organizer.addJoinedEvent("event1");
        organizer.addJoinedEvent("event2");

        assertTrue(organizer.removeJoinedEvent("event1"));
        assertFalse(organizer.removeJoinedEvent("event1")); // Already removed
        assertFalse(organizer.removeJoinedEvent("nonexistent")); // Doesn't exist

        List<String> joinedEvents = organizer.getJoinedEventIds();
        assertEquals(1, joinedEvents.size());
        assertEquals("event2", joinedEvents.get(0));
    }

    @Test
    public void testIsEventOrganizer() {
        organizer.addCreatedEvent("event1");
        organizer.addCreatedEvent("event2");

        assertTrue(organizer.isEventOrganizer("event1"));
        assertTrue(organizer.isEventOrganizer("event2"));
        assertFalse(organizer.isEventOrganizer("event3"));
        assertFalse(organizer.isEventOrganizer(null));
    }

    @Test
    public void testGetCreatedEventsCount() {
        assertEquals(0, organizer.getCreatedEventsCount());

        organizer.addCreatedEvent("event1");
        assertEquals(1, organizer.getCreatedEventsCount());

        organizer.addCreatedEvent("event2");
        assertEquals(2, organizer.getCreatedEventsCount());
    }

    @Test
    public void testGetJoinedEventsCount() {
        assertEquals(0, organizer.getJoinedEventsCount());

        organizer.addJoinedEvent("event1");
        assertEquals(1, organizer.getJoinedEventsCount());

        organizer.addJoinedEvent("event2");
        assertEquals(2, organizer.getJoinedEventsCount());
    }
}