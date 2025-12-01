package com.ualberta.static2.organizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.model.EntrantRegistrationStatus;
import com.ualberta.eventlottery.repository.RegistrationRepository;
import com.ualberta.eventlottery.ui.organizer.fragment.EntrantsFragment;
import com.ualberta.static2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for Organizer Event Showcase Fragment using the specific event data:
 * Event ID: "C2q3dV7r6KPmJW4uzs96"
 * Title: "test qr"
 * Location: "test"
 * Price: 100
 * Max Attendees: 100
 * Registration Status: "REGISTRATION_OPEN"
 * Event Status: "UPCOMING"
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerEventShowcaseFragmentTest {

    private static final String TEST_EVENT_ID = "C2q3dV7r6KPmJW4uzs96";
    private FragmentScenario<EntrantsFragment> scenario;
    private RegistrationRepository registrationRepository;

    @Before
    public void setUp() {
        registrationRepository = RegistrationRepository.getInstance();
        
        // Launch the EntrantsFragment with our specific event ID
        scenario = FragmentScenario.launchInContainer(
                EntrantsFragment.class,
                EntrantsFragment.newInstance(TEST_EVENT_ID).getArguments(),
                R.style.Theme_Static2 // Using the correct app theme to fix SwitchMaterial inflation issue
        );
        
        // Give time for the fragment to initialize and load data
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    /**
     * Test that the fragment loads correctly with our specific event data
     */
    @Test
    public void testFragmentLoads() {
        // Check that the fragment view is displayed
//        onView(withId(R.id.lv_event_entrant_list)).check(matches(isDisplayed()));

        // Check that status buttons are displayed
        onView(withId(R.id.btn_entrants_confirmed)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_entrants_waiting)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_entrants_selected)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_entrants_cancelled)).check(matches(isDisplayed()));
    }

    /**
     * Test switching between different registration statuses
     */
    @Test
    public void testStatusSwitching() {
        // Initially, the confirmed button should be selected
        onView(withId(R.id.btn_entrants_confirmed)).check(matches(isDisplayed()));
        
        // Click on waiting button
        onView(withId(R.id.btn_entrants_waiting)).perform(scrollTo(), click());
        
        // Click on selected button
        onView(withId(R.id.btn_entrants_selected)).perform(scrollTo(), click());
        
        // Click on cancelled button
        onView(withId(R.id.btn_entrants_cancelled)).perform(scrollTo(), click());
        
        // Return to confirmed
        onView(withId(R.id.btn_entrants_confirmed)).perform(scrollTo(), click());
    }

    /**
     * Test that registration counts are loaded properly
     */
    @Test
    public void testRegistrationCountsLoaded() throws InterruptedException {
        // Give some time for data to load
        Thread.sleep(3000);
        
        // We can't predict exact counts, but we can verify that the count views exist
        onView(withId(R.id.tv_event_entrants_confirmed_number)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_entrants_waiting_number)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_entrants_selected_number)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_entrants_cancelled_number)).check(matches(isDisplayed()));
    }

    /**
     * Test UI elements for sending notifications
     */
    @Test
    public void testNotificationElements() {
        // Check that notification-related UI elements are present
        onView(withId(R.id.btn_select_all)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_send_notifications)).check(matches(isDisplayed()));
    }

    /**
     * Test that we can interact with the select all functionality
     */
    @Test
    public void testSelectAllFunctionality() {
        // Give time for data to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Click the select all button
        onView(withId(R.id.btn_select_all)).perform(click());
        
        // Click it again to deselect
        onView(withId(R.id.btn_select_all)).perform(click());
    }

    /**
     * Test that all registration status buttons respond to clicks
     */
    @Test
    public void testAllRegistrationStatusButtonsClickable() {
        // Test Confirmed button
        onView(withId(R.id.btn_entrants_confirmed)).perform(scrollTo(), click());
        onView(withId(R.id.btn_entrants_confirmed)).check(matches(isDisplayed()));
        
        // Test Waiting button
        onView(withId(R.id.btn_entrants_waiting)).perform(scrollTo(), click());
        onView(withId(R.id.btn_entrants_waiting)).check(matches(isDisplayed()));
        
        // Test Selected button
        onView(withId(R.id.btn_entrants_selected)).perform(scrollTo(), click());
        onView(withId(R.id.btn_entrants_selected)).check(matches(isDisplayed()));
        
        // Test Cancelled button
        onView(withId(R.id.btn_entrants_cancelled)).perform(scrollTo(), click());
        onView(withId(R.id.btn_entrants_cancelled)).check(matches(isDisplayed()));
    }
    
    /**
     * Test fetching registration data for each status
     */
    @Test
    public void testFetchRegistrationDataByStatus() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(4); // 4 statuses to test
        final AtomicInteger totalRegistrations = new AtomicInteger(0);
        
        // Test getting confirmed registrations
        registrationRepository.getRegistrationsByStatus(TEST_EVENT_ID, EntrantRegistrationStatus.CONFIRMED, 
            new RegistrationRepository.RegistrationListCallback() {
                @Override
                public void onSuccess(java.util.List<com.ualberta.eventlottery.model.Registration> registrations) {
                    totalRegistrations.addAndGet(registrations.size());
                    latch.countDown();
                }
                
                @Override
                public void onFailure(Exception e) {
                    latch.countDown();
                }
            });
        
        // Test getting waiting registrations
        registrationRepository.getRegistrationsByStatus(TEST_EVENT_ID, EntrantRegistrationStatus.WAITING, 
            new RegistrationRepository.RegistrationListCallback() {
                @Override
                public void onSuccess(java.util.List<com.ualberta.eventlottery.model.Registration> registrations) {
                    totalRegistrations.addAndGet(registrations.size());
                    latch.countDown();
                }
                
                @Override
                public void onFailure(Exception e) {
                    latch.countDown();
                }
            });
        
        // Test getting selected registrations
        registrationRepository.getRegistrationsByStatus(TEST_EVENT_ID, EntrantRegistrationStatus.SELECTED, 
            new RegistrationRepository.RegistrationListCallback() {
                @Override
                public void onSuccess(java.util.List<com.ualberta.eventlottery.model.Registration> registrations) {
                    totalRegistrations.addAndGet(registrations.size());
                    latch.countDown();
                }
                
                @Override
                public void onFailure(Exception e) {
                    latch.countDown();
                }
            });
        
        // Test getting cancelled registrations
        registrationRepository.getRegistrationsByStatus(TEST_EVENT_ID, EntrantRegistrationStatus.CANCELLED, 
            new RegistrationRepository.RegistrationListCallback() {
                @Override
                public void onSuccess(java.util.List<com.ualberta.eventlottery.model.Registration> registrations) {
                    totalRegistrations.addAndGet(registrations.size());
                    latch.countDown();
                }
                
                @Override
                public void onFailure(Exception e) {
                    latch.countDown();
                }
            });
        
        // Wait for all callbacks to complete (with timeout)
        boolean completed = latch.await(10, TimeUnit.SECONDS);
    }
}