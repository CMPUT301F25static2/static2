package com.ualberta.static2.entrant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.testutils.DatabaseCleaner;
import com.ualberta.static2.testutils.UserManagerRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class EntrantMainActivityWhiteBoxTest {

    @Rule
    public UserManagerRule userManagerRule = new UserManagerRule();

    private int latchAwaitMS = 30000;

    private static String TEST_EVENT_ID = "entrant-white-box-test-event-id";

    //US 01.01.01 As an entrant, I want to join the waiting list for a specific event
    @Test
    public void entrantCanJoinWaitingList() throws InterruptedException {
        String testEventId = TEST_EVENT_ID + "-entrantCanJoinWaitingList";

        DatabaseCleaner.cleanRegistrationsByEvent(testEventId, latchAwaitMS);

        Event event = createTestEvent(testEventId);

        RegistrationRepository registrationRepository = RegistrationRepository.getInstance();
        CountDownLatch registrationLatch = new CountDownLatch(1);
        TestRegistrationCallback registrationCallback = new TestRegistrationCallback(registrationLatch);

        registrationRepository.registerUser(event.getId(), UserManager.getCurrentUserId(), registrationCallback);
        assertTrue(registrationLatch.await(5000, TimeUnit.MILLISECONDS));

        Registration registration = registrationCallback.getRegistration();
        assertNotNull(registration);
        assertEquals(UserManager.getCurrentUserId(), registration.getEntrantId());
        assertTrue("Expecting WAITING status but got " + registration.getStatus().name(), EntrantRegistrationStatus.WAITING.equals(registration.getStatus()));

        CountDownLatch registrationListLatch = new CountDownLatch(1);
        TestRegistrationListCallback registrationListCallback = new TestRegistrationListCallback(registrationListLatch);
        registrationRepository.getRegistrationsByEvent(event.getId(), registrationListCallback);
        assertTrue("Timeout waiting for registration list callback", registrationListLatch.await(5000, TimeUnit.MILLISECONDS));

        List<Registration> registrations = registrationListCallback.getRegistrations();
        assertEquals(1, registrations.size());
        assertEquals(UserManager.getCurrentUserId(), registrations.get(0).getEntrantId());
        assertTrue("Expecting WAITING status but got " + registrations.get(0).getStatus().name(), EntrantRegistrationStatus.WAITING.equals(registrations.get(0).getStatus()));
    }

    //US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
    @Test
    public void entrantCanLeaveWaitingList() throws InterruptedException {
        String testEventId = TEST_EVENT_ID + "-entrantCanJoinWaitingList";

        DatabaseCleaner.cleanRegistrationsByEvent(testEventId, latchAwaitMS);

        Event event = createTestEvent(testEventId);

        RegistrationRepository registrationRepository = RegistrationRepository.getInstance();
        CountDownLatch registrationLatch = new CountDownLatch(1);
        TestRegistrationCallback registrationCallback = new TestRegistrationCallback(registrationLatch);

        // First we register the user for the event
        registrationRepository.registerUser(event.getId(), UserManager.getCurrentUserId(), registrationCallback);
        assertTrue(registrationLatch.await(5000, TimeUnit.MILLISECONDS));

        Registration registration = registrationCallback.getRegistration();
        assertNotNull(registration);
        assertEquals(UserManager.getCurrentUserId(), registration.getEntrantId());
        assertTrue("Expecting WAITING status but got " + registration.getStatus().name(), EntrantRegistrationStatus.WAITING.equals(registration.getStatus()));

        // Now we unregister
        CountDownLatch unregistrationLatch = new CountDownLatch(1);
        TestRegistrationCallback unregistrationCallback = new TestRegistrationCallback(unregistrationLatch);
        registrationRepository.unregisterUser(event.getId(), UserManager.getCurrentUserId(), unregistrationCallback);
        assertTrue(unregistrationLatch.await(5000, TimeUnit.MILLISECONDS));

        registration = unregistrationCallback.getRegistration();
        assertNull(registration);

        CountDownLatch registrationListLatch = new CountDownLatch(1);
        TestRegistrationListCallback registrationListCallback = new TestRegistrationListCallback(registrationListLatch);
        registrationRepository.getRegistrationsByEvent(event.getId(), registrationListCallback);
        assertTrue("Timeout waiting for registration list callback", registrationListLatch.await(5000, TimeUnit.MILLISECONDS));

        List<Registration> registrations = registrationListCallback.getRegistrations();
        assertEquals(0, registrations.size());
    }

    // US 01.01.03 As an entrant, I want to be able to see a list of events that I can join the waiting list for
    @Test
    public void entrantCanSeeJoinableEventsList() {
//        InstrumentationRegistry.getInstrumentation().getTargetContext();
//
//        String currentUserId = "user-123";
//        Date now = new Date();
//        Date openStart = new Date(now.getTime() - 60 * 60 * 1000); // 1h ago
//        Date openEnd = new Date(now.getTime() + 60 * 60 * 1000);   // in 1h
//        Date closedEnd = new Date(now.getTime() - 10 * 60 * 1000); // 10m ago
//
//        // Event A: joinable (open, not full, user not on waitlist)
//        Event eventA = new Event("A", "org", "Event A", "Desc");
//        eventA.setRegistrationStart(openStart);
//        eventA.setRegistrationEnd(openEnd);
//        eventA.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
//        eventA.setMaxWaitListSize(3);
//
//        // Event B: waitlist full (not joinable)
//        Event eventB = new Event("B", "org", "Event B", "Desc");
//        eventB.setRegistrationStart(openStart);
//        eventB.setRegistrationEnd(openEnd);
//        eventB.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
//        eventB.setMaxWaitListSize(1);
//        //eventB.addToWaitingList("someone-else"); // fill the waitlist
//
//        // Event C: registration closed (not joinable)
//        Event eventC = new Event("C", "org", "Event C", "Desc");
//        eventC.setRegistrationStart(openStart);
//        eventC.setRegistrationEnd(closedEnd);
//        eventC.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_CLOSED);
//        eventC.setMaxWaitListSize(3);
//
//        // Event D: user already on waitlist (not joinable)
//        Event eventD = new Event("D", "org", "Event D", "Desc");
//        eventD.setRegistrationStart(openStart);
//        eventD.setRegistrationEnd(openEnd);
//        eventD.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
//        eventD.setMaxWaitListSize(5);
//        //eventD.addToWaitingList(currentUserId);
//
//        List<Event> all = Arrays.asList(eventA, eventB, eventC, eventD);
//
//        // Compute joinable events for current user
//        List<Event> joinable = new ArrayList<>();
////        for (Event e : all) {
////            boolean canJoin = e.isRegistrationOpen()
////                    && !e.isWaitingListFull()
////                    && !e.getWaitListUserIds().contains(currentUserId);
////            if (canJoin) {
////                joinable.add(e);
////            }
//        }
//
//        // Assertions: Only Event A should be joinable
////        assertEquals(1, joinable.size());
////        assertTrue(joinable.contains(eventA));
////        assertFalse(joinable.contains(eventB));
////        assertFalse(joinable.contains(eventC));
////        assertFalse(joinable.contains(eventD));
    }

    private Event createTestEvent(String testEventId) throws InterruptedException {
        EventRepository eventRepository = EventRepository.getInstance();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Arrange: create event with open registration window
        Event event = new Event(testEventId, "organizer-1", "Sample Event", "Desc");
        Date now = new Date();
        event.setRegistrationStart(new Date(now.getTime() - 60 * 60 * 1000)); // started 1h ago
        event.setRegistrationEnd(new Date(now.getTime() + 60 * 60 * 1000));   // ends in 1h
        event.setRegistrationStatus(EventRegistrationStatus.REGISTRATION_OPEN);
        event.setMaxWaitListSize(5); // capacity
        event.setMaxAttendees(10);

        CountDownLatch latch = new CountDownLatch(1);

        EventRepository.OperationCallback callback = new EventRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                assertFalse(e.getMessage(), false);
            }
        };

        Uri posterUri = new Uri.Builder()
                .scheme("android.resource")
                .authority(context.getPackageName())
                .appendPath("drawable")
                .appendPath(context.getResources().getResourceEntryName(R.drawable.ic_self_improvement_24dp_000000_fill0_wght400_grad0_opsz24))
                .build();

        eventRepository.addEventWithPoster(event, posterUri, callback);
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        return event;
    }

    private static class TestRegistrationListCallback implements RegistrationRepository.RegistrationListCallback {
        private CountDownLatch latch;
        private List<Registration> registrations = new ArrayList<>();

        public TestRegistrationListCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        public List<Registration> getRegistrations() {
            return registrations;
        }

        @Override
        public void onSuccess(List<Registration> registrations) {
            this.registrations = registrations;
            this.latch.countDown();
        }

        @Override
        public void onFailure(Exception e) {
            // Let the await timeout
        }
    }

    private static class TestRegistrationCallback implements RegistrationRepository.RegistrationCallback {
        private CountDownLatch latch;

        private Registration registration;

        public TestRegistrationCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        public Registration getRegistration() {
            return registration;
        }

        @Override
        public void onSuccess(Registration registration) {
            this.registration = registration;
            this.latch.countDown();
        }

        @Override
        public void onFailure(Exception e) {
            // Let the await timeout
        }
    }

}
