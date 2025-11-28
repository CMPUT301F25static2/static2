package com.ualberta.static2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.platform.app.InstrumentationRegistry;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivityEntrantTest {


    //US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
    @Test
    public void entrantCanLeaveWaitingList() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Arrange: create event with open registration window
        Event event = new Event("event-1", "organizer-1", "Sample Event", "Desc");
        Date now = new Date();
        event.setRegistrationStart(new Date(now.getTime() - 60 * 60 * 1000)); // started 1h ago
        event.setRegistrationEnd(new Date(now.getTime() + 60 * 60 * 1000));   // ends in 1h
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        event.setMaxWaitListSize(5); // capacity
        event.setMaxAttendees(10);

        String userId = "user-123";

        // Create registration representing the user's waiting status
        Registration registration = new Registration("reg-1", event.getId(), userId);
        assertEquals(EntrantRegistrationStatus.WAITING, registration.getStatus());

        // Act 1: Add user to waiting list
        boolean added = event.addToWaitingList(userId);
        assertTrue("User should be added to waiting list", added);
        assertEquals(1, event.getWaitListCount());
        assertTrue(event.getWaitListUserIds().contains(userId));

        // Act 2: User chooses to leave waiting list
        boolean removed = event.removeFromWaitingList(userId);
        if (removed) {
            registration.setStatus(EntrantRegistrationStatus.CANCELLED);
            registration.setCancelledAt(new Date());
        }

        // Assert removal effects
        assertTrue("User should be removed from waiting list", removed);
        assertEquals("Wait list count should decrement", 0, event.getWaitListCount());
        assertFalse("Wait list should no longer contain user", event.getWaitListUserIds().contains(userId));
        assertEquals("Registration status should be CANCELLED after leaving", EntrantRegistrationStatus.CANCELLED, registration.getStatus());

        // Act 3: Attempt to remove same user again
        boolean removedAgain = event.removeFromWaitingList(userId);
        assertFalse("Second removal attempt should fail", removedAgain);
        assertEquals(0, event.getWaitListCount());

        // Edge Case: Removing a user that was never in the list
        boolean removedNonExistent = event.removeFromWaitingList("non-existent-user");
        assertFalse("Removing non-existent user should return false", removedNonExistent);
        assertEquals(0, event.getWaitListCount());
    }

    // US 01.01.03 As an entrant, I want to be able to see a list of events that I can join the waiting list for
    @Test
    public void entrantCanSeeJoinableEventsList() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        String currentUserId = "user-123";
        Date now = new Date();
        Date openStart = new Date(now.getTime() - 60 * 60 * 1000); // 1h ago
        Date openEnd = new Date(now.getTime() + 60 * 60 * 1000);   // in 1h
        Date closedEnd = new Date(now.getTime() - 10 * 60 * 1000); // 10m ago

        // Event A: joinable (open, not full, user not on waitlist)
        Event eventA = new Event("A", "org", "Event A", "Desc");
        eventA.setRegistrationStart(openStart);
        eventA.setRegistrationEnd(openEnd);
        eventA.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        eventA.setMaxWaitListSize(3);

        // Event B: waitlist full (not joinable)
        Event eventB = new Event("B", "org", "Event B", "Desc");
        eventB.setRegistrationStart(openStart);
        eventB.setRegistrationEnd(openEnd);
        eventB.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        eventB.setMaxWaitListSize(1);
        eventB.addToWaitingList("someone-else"); // fill the waitlist

        // Event C: registration closed (not joinable)
        Event eventC = new Event("C", "org", "Event C", "Desc");
        eventC.setRegistrationStart(openStart);
        eventC.setRegistrationEnd(closedEnd);
        eventC.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
        eventC.setMaxWaitListSize(3);

        // Event D: user already on waitlist (not joinable)
        Event eventD = new Event("D", "org", "Event D", "Desc");
        eventD.setRegistrationStart(openStart);
        eventD.setRegistrationEnd(openEnd);
        eventD.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        eventD.setMaxWaitListSize(5);
        eventD.addToWaitingList(currentUserId);

        List<Event> all = Arrays.asList(eventA, eventB, eventC, eventD);

        // Compute joinable events for current user
        List<Event> joinable = new ArrayList<>();
        for (Event e : all) {
            boolean canJoin = e.isRegistrationOpen()
                    && !e.isWaitingListFull()
                    && !e.getWaitListUserIds().contains(currentUserId);
            if (canJoin) {
                joinable.add(e);
            }
        }

        // Assertions: Only Event A should be joinable
        assertEquals(1, joinable.size());
        assertTrue(joinable.contains(eventA));
        assertFalse(joinable.contains(eventB));
        assertFalse(joinable.contains(eventC));
        assertFalse(joinable.contains(eventD));
    }



    //US 01.01.01 As an entrant, I want to join the waiting list for a specific event
    @Test
    public void entrantCanJoinWaitingList() {
        InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Arrange: create event with open registration window
        Event event = new Event("event-1", "organizer-1", "Sample Event", "Desc");
        Date now = new Date();
        event.setRegistrationStart(new Date(now.getTime() - 60 * 60 * 1000)); // started 1h ago
        event.setRegistrationEnd(new Date(now.getTime() + 60 * 60 * 1000));   // ends in 1h
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        event.setMaxWaitListSize(5); // capacity
        event.setMaxAttendees(10);

        String userId = "user-123";

        // Act: User attempts to join waiting list
        boolean added = event.addToWaitingList(userId);

        // Assert: User is added to waiting list
        assertTrue("User should be added to waiting list", added);
        assertEquals(1, event.getWaitListCount());
        assertTrue(event.getWaitListUserIds().contains(userId));

        // Edge Case: Attempt to add same user again
        boolean addedAgain = event.addToWaitingList(userId);
        assertFalse("User should not be added again to waiting list", addedAgain);
        assertEquals(1, event.getWaitListCount());
    }

}
