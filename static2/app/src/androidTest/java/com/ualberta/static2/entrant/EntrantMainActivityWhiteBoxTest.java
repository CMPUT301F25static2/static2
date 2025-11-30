package com.ualberta.static2.entrant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventRegistrationStatus;
import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EventListLiveData;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.utils.UserManager;
import com.ualberta.static2.R;
import com.ualberta.static2.testutils.DatabaseCleaner;
import com.ualberta.static2.testutils.UserManagerRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Whitebox testing for Entrant user stories.
 * Stories Tested:
 *  * US-01.01.01, 02, 03
 */
@RunWith(MockitoJUnitRunner.class)
public class EntrantMainActivityWhiteBoxTest {

    @Rule
    public UserManagerRule userManagerRule = new UserManagerRule();

    // Google search term: java junit testing LiveData example
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    // Google search term: java junit testing LiveData example
    @Mock
    Observer<List<Event>> mockEventListLiveDataObserver;

    private int latchAwaitMS = 30000;

    private static String TEST_EVENT_DESCRIPTION = "entrant-white-box-test-event";

    /**
     * Testing US 01.01.01 As an entrant, I want to join the waiting list for a specific event
     * @throws InterruptedException
     */
    @Test
    public void entrantCanJoinWaitingList() throws InterruptedException {
        String testEventDescription = TEST_EVENT_DESCRIPTION + "-entrantCanJoinWaitingList";

        Event event = createTestEvent(testEventDescription, EventRegistrationStatus.REGISTRATION_OPEN);

        try {
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
        } finally {
            DatabaseCleaner.cleanEvent(event.getId(), latchAwaitMS);
        }
    }

    /**
     * Testing US 01.01.02 As an entrant, I want to leave the waiting list for a specific event
     * @throws InterruptedException
     */
    @Test
    public void entrantCanLeaveWaitingList() throws InterruptedException {
        String testEventDescription = TEST_EVENT_DESCRIPTION + "-entrantCanJoinWaitingList";

        Event event = createTestEvent(testEventDescription, EventRegistrationStatus.REGISTRATION_OPEN);

        try {
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
        } finally {
            DatabaseCleaner.cleanEvent(event.getId(), latchAwaitMS);
        }
    }

    /**
     * Testing US 01.01.03 As an entrant, I want to be able to see a list of events that I can join the waiting list for
     * @throws InterruptedException
     */
    @Test
    public void entrantCanSeeJoinableEventsList() throws InterruptedException {
        String testEventDescription = TEST_EVENT_DESCRIPTION + "-entrantCanJoinWaitingList";

        Event event1 = createTestEvent(testEventDescription + "-1", EventRegistrationStatus.REGISTRATION_OPEN);
        Event event2 = createTestEvent(testEventDescription + "-2", EventRegistrationStatus.REGISTRATION_OPEN);
        Event event3 = createTestEvent(testEventDescription + "-3", EventRegistrationStatus.REGISTRATION_CLOSED);

        try {
            EventRepository eventRepository = EventRepository.getInstance();
            EventListLiveData availableEvents = eventRepository.getAvailableEvents();

            CountDownLatch latch = new CountDownLatch(1);

            // Google search term: java junit testing LiveData example
            availableEvents.observeForever(mockEventListLiveDataObserver);

            // Give time for the repository to fill the available events list
            latch.await(5000, TimeUnit.MILLISECONDS);

            List<Event> result = availableEvents.getValue();
            List<String> expectedIds = new ArrayList<>();
            for (Event event : result) {
                assertNotEquals(event3.getId(), event.getId());
                if (event.getId().compareTo(event1.getId()) == 0 ||
                    event.getId().compareTo(event2.getId()) == 0) {
                    expectedIds.add(event.getId());
                }
            }
            assertEquals(2, expectedIds.size());
        } finally {
            DatabaseCleaner.cleanEvent(event1.getId(), latchAwaitMS);
            DatabaseCleaner.cleanEvent(event2.getId(), latchAwaitMS);
            DatabaseCleaner.cleanEvent(event3.getId(), latchAwaitMS);
        }
    }

    /**
     * Creates a test event with given description and registration status
     * @param testEventDescription
     * @param registrationStatus
     * @return
     * @throws InterruptedException
     */
    private Event createTestEvent(String testEventDescription, EventRegistrationStatus registrationStatus) throws InterruptedException {
        EventRepository eventRepository = EventRepository.getInstance();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Arrange: create event with open registration window
        // The id here doesn't matter because it will be set by the EventRepository when the
        // event is added to the db.
        Event event = new Event("test-event", "organizer-1", "Test Event", testEventDescription);
        Date now = new Date();
        event.setRegistrationStart(new Date(now.getTime() - 60 * 60 * 1000)); // started 1h ago
        event.setRegistrationEnd(new Date(now.getTime() + 60 * 60 * 1000));   // ends in 1h
        event.setRegistrationStatus(registrationStatus);
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

    /**
     * Registration list callback for testing.
     * It counts down the provided latch on success.
     */
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

    /**
     * Registration callback for testing.
     * It counts down the provided latch on sucess.
     */
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
