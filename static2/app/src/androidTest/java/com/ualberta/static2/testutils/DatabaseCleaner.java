package com.ualberta.static2.testutils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseCleaner {
    public static void cleanEvent(String eventId, int awaitMS) throws InterruptedException {
        cleanRegistrationsByEvent(eventId, awaitMS);

        EventRepository eventRepository = EventRepository.getInstance();

        CountDownLatch latch = new CountDownLatch(1);
        EventRepository.BooleanCallback callback = new EventRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean result) {
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                assertTrue("Failed to delete event " + eventId, false);
            }
        };
        eventRepository.deleteEvent(eventId, callback);
        assertTrue(latch.await(awaitMS, TimeUnit.MILLISECONDS));
    }

    public static void cleanRegistrationsByEvent(String eventId, int awaitMS) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        RegistrationRepository registrationRepository = RegistrationRepository.getInstance();

        RegistrationListCallback registrationListCallback = new RegistrationListCallback(latch);
        registrationRepository.getRegistrationsByEvent(eventId, registrationListCallback);
        assertTrue("Timeout cleaning registrations for event " + eventId, latch.await(awaitMS, TimeUnit.MILLISECONDS));

        List<Registration> registrations = registrationListCallback.getRegistrations();

        CountDownLatch deleteRegistrationLatch = new CountDownLatch(registrations.size());
        RegistrationRepository.BooleanCallback deleteRegistrationCallback = new RegistrationRepository.BooleanCallback() {
            @Override
            public void onSuccess(boolean result) {
                deleteRegistrationLatch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                // Let the await timeout
                assertTrue("Failed to delete registration: " + e.getMessage(), false);
            }
        };
        for (Registration registration : registrations) {
            registrationRepository.deleteRegistration(registration.getId(), deleteRegistrationCallback);
        }
        assertTrue("Timeout waiting for " + registrations.size() + " registrations to be deleted", deleteRegistrationLatch.await(10000, TimeUnit.MILLISECONDS));
    }

    private static class RegistrationListCallback implements RegistrationRepository.RegistrationListCallback {
        private List<Registration> registrations = new ArrayList<>();
        private CountDownLatch latch;

        public RegistrationListCallback(CountDownLatch latch) {
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

        }
    }
}
