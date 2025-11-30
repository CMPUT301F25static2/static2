package com.ualberta.static2.testutils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.ualberta.eventlottery.model.Registration;
import com.ualberta.eventlottery.repository.RegistrationRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseCleaner {
    public static void cleanRegistrationsByEvent(String eventId, int awaitMS) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        RegistrationRepository registrationRepository = RegistrationRepository.getInstance();

        RegistrationRepository.RegistrationListCallback callback = new RegistrationRepository.RegistrationListCallback() {
            @Override
            public void onSuccess(List<Registration> registrations) {
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
                try {
                    assertTrue("Timeout waiting for " + registrations.size() + " registrations to be deleted", deleteRegistrationLatch.await(10000, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                // Let the await timeout
            }
        };
        registrationRepository.getRegistrationsByEvent(eventId, callback);
        assertTrue("Timeout cleaning registrations for event " + eventId, latch.await(awaitMS, TimeUnit.MILLISECONDS));
    }
}
