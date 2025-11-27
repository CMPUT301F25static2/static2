package com.ualberta.static2.organizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.model.EventRegistrationStatus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.time.LocalTime;

public class EventModelTest {

    private Event event;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        event = new Event();
    }

    @Test
    public void testEventConstructor() {
        assertNotNull(event.getCreatedAt());
        assertEquals(EventRegistrationStatus.REGISTRATION_OPEN, event.getRegistrationStatus());
        assertEquals(0, event.getCurrentWaitListSize());
        assertEquals(0, event.getConfirmedAttendees());
    }

    @Test
    public void testEventConstructorWithParameters() {
        Event event2 = new Event("eventId123", "orgId123", "Test Event", "Test Description");

        assertEquals("eventId123", event2.getId());
        assertEquals("orgId123", event2.getOrganizerId());
        assertEquals("Test Event", event2.getTitle());
        assertEquals("Test Description", event2.getDescription());
        assertNotNull(event2.getCreatedAt());
    }

    @Test
    public void testGettersAndSetters() {
        // Test setters
        event.setId("eventId123");
        event.setOrganizerId("orgId123");
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setCategory("TestCategory");
        event.setPrice(25.50);
        event.setDailyStartTime(9, 30);
        event.setDailyEndTime(17, 30);
        event.setSessionDuration(60);
        event.setCreatedAt(new Date(1234567890000L));
        event.setStartTime(new Date(1234567890000L));
        event.setEndTime(new Date(1234567890000L));
        event.setRegistrationStart(new Date(1234567890000L));
        event.setRegistrationEnd(new Date(1234567890000L));
        event.setLocation("Test Location");
        event.setLocationRequired(true);
        event.setPosterUrl("http://example.com/poster.jpg");
        event.setQrCodeUrl("http://example.com/qrcode.png");
        event.setLocationUrl("http://example.com/location");
        event.setEventStatus(EventStatus.UPCOMING);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        event.setMaxAttendees(100);
        event.setMaxWaitListSize(20);
        event.setCurrentWaitListSize(5);
        event.setConfirmedAttendees(50);

        // Test getters
        assertEquals("eventId123", event.getId());
        assertEquals("orgId123", event.getOrganizerId());
        assertEquals("Test Event", event.getTitle());
        assertEquals("Test Description", event.getDescription());
        assertEquals("TestCategory", event.getCategory());
        assertEquals(25.50, event.getPrice(), 0.01);
        assertEquals(9, event.getDailyStartTime().getHour());
        assertEquals(30, event.getDailyStartTime().getMinute());
        assertEquals(17, event.getDailyEndTime().getHour());
        assertEquals(30, event.getDailyEndTime().getMinute());
        assertEquals(60, event.getSessionDuration());
        assertEquals(new Date(1234567890000L), event.getCreatedAt());
        assertEquals(new Date(1234567890000L), event.getStartTime());
        assertEquals(new Date(1234567890000L), event.getEndTime());
        assertEquals(new Date(1234567890000L), event.getRegistrationStart());
        assertEquals(new Date(1234567890000L), event.getRegistrationEnd());
        assertEquals("Test Location", event.getLocation());
        assertTrue(event.isLocationRequired());
        assertEquals("http://example.com/poster.jpg", event.getPosterUrl());
        assertEquals("http://example.com/qrcode.png", event.getQrCodeUrl());
        assertEquals("http://example.com/location", event.getLocationUrl());
        assertEquals(EventStatus.UPCOMING, event.getEventStatus());
        assertEquals(EventRegistrationStatus.REGISTRATION_CLOSED, event.getRegistrationStatus());
        assertEquals(100, event.getMaxAttendees());
        assertEquals(20, event.getMaxWaitListSize());
        assertEquals(5, event.getCurrentWaitListSize());
        assertEquals(50, event.getConfirmedAttendees());
    }

    @Test
    public void testIsRegistrationOpen_RegistrationClosed() {
        Date now = new Date();
        Date past = new Date(now.getTime() - 1000000);
        Date future = new Date(now.getTime() + 1000000);

        event.setRegistrationStart(past);
        event.setRegistrationEnd(future);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);

        assertFalse(event.isRegistrationOpen());
    }

    @Test
    public void testIsRegistrationOpen_RegistrationOpenButPastDeadline() {
        Date now = new Date();
        Date past = new Date(now.getTime() - 1000000);
        Date pastDeadline = new Date(now.getTime() - 100000);

        event.setRegistrationStart(past);
        event.setRegistrationEnd(pastDeadline);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);

        assertFalse(event.isRegistrationOpen());
    }

    @Test
    public void testIsRegistrationOpen_RegistrationOpenAndActive() {
        Date now = new Date();
        Date past = new Date(now.getTime() - 1000000);
        Date future = new Date(now.getTime() + 1000000);

        event.setRegistrationStart(past);
        event.setRegistrationEnd(future);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);

        assertTrue(event.isRegistrationOpen());
    }

    @Test
    public void testUpdateRegistrationStatusBasedOnDeadline() {
        Date now = new Date();
        Date past = new Date(now.getTime() - 1000000);
        Date pastDeadline = new Date(now.getTime() - 100000);

        event.setRegistrationEnd(pastDeadline);
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);

        event.updateRegistrationStatusBasedOnDeadline();

        assertEquals(EventRegistrationStatus.REGISTRATION_CLOSED, event.getRegistrationStatus());
    }

    @Test
    public void testIsWaitingListFull() {
        event.setMaxWaitListSize(10);
        event.setCurrentWaitListSize(10);

        assertTrue(event.isWaitingListFull());

        event.setCurrentWaitListSize(9);
        assertFalse(event.isWaitingListFull());

        event.setMaxWaitListSize(0);
        assertFalse(event.isWaitingListFull());
    }

    @Test
    public void testIsEventFull() {
        event.setMaxAttendees(100);
        event.setConfirmedAttendees(100);

        assertTrue(event.isEventFull());

        event.setConfirmedAttendees(99);
        assertFalse(event.isEventFull());
    }
}