package com.ualberta.static2.organizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ualberta.eventlottery.model.Event;
import com.ualberta.eventlottery.model.EventStatus;
import com.ualberta.eventlottery.repository.EventRepository;
import com.ualberta.eventlottery.ui.organizer.organizerEventInfo.OrganizerEventInfoFragment;
import com.ualberta.static2.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests for Organizer Event Info Fragment using the specific event data:
 * Event ID: "C2q3dV7r6KPmJW4uzs96"
 * Title: "test qr"
 * Location: "test"
 * Price: 100
 * Max Attendees: 100
 * Registration Status: "REGISTRATION_OPEN"
 * Event Status: "UPCOMING"
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerEventInfoFragmentTest {

    private static final String TEST_EVENT_ID = "C2q3dV7r6KPmJW4uzs96";
    private FragmentScenario<OrganizerEventInfoFragment> scenario;
    private EventRepository eventRepository;

    @Before
    public void setUp() {
        eventRepository = EventRepository.getInstance();
        
        // Launch the OrganizerEventInfoFragment with our specific event ID
        scenario = FragmentScenario.launchInContainer(
                OrganizerEventInfoFragment.class,
                OrganizerEventInfoFragment.newInstance(TEST_EVENT_ID).getArguments(),
                R.style.Theme_Static2
        );
        
        // Give time for the fragment to initialize and load data
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test that the fragment loads correctly with our specific event data
     */
    @Test
    public void testFragmentLoads() {
        // Wait for UI to load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Check that the fragment view is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));
        
        // Check that key UI elements are displayed
        onView(withId(R.id.tv_event_title)).check(matches(isDisplayed()));
        onView(withId(R.id.iv_event_poster_img)).check(matches(isDisplayed()));
    }

    /**
     * Test event information display with the specific event data
     */
    @Test
    public void testEventInformationDisplay() {
        // Wait for UI to load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Check that event title is displayed correctly
        onView(withId(R.id.tv_event_title)).check(matches(withText("test qr")));
        onView(withId(R.id.tv_event_update_title)).check(matches(withText("test qr")));
        
        // Check that event status is displayed correctly
        onView(withId(R.id.tv_event_update_status)).check(matches(withText("UPCOMING")));
        
        // Check that event price is displayed correctly
        onView(withId(R.id.tv_event_update_price)).check(matches(withText("100.0")));
    }

    /**
     * Test UI elements for event editing functionality
     */
    @Test
    public void testEventEditingElements() {
        // Wait for UI to load
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Check that edit buttons are displayed
        onView(withId(R.id.btn_event_update_title)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_event_update_price)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_event_update_status)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_event_update_end_time)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_event_update_registry_end_time)).check(matches(isDisplayed()));
    }

    /**
     * Test event data loading from repository
     */
    @Test
    public void testEventDataLoading() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Event[] loadedEvent = new Event[1];
        
        // Load event data directly from repository
        eventRepository.findEventById(TEST_EVENT_ID, new EventRepository.EventCallback() {
            @Override
            public void onSuccess(Event event) {
                loadedEvent[0] = event;
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                latch.countDown();
            }
        });
        
        // Wait for data to load
        boolean completed = latch.await(15, TimeUnit.SECONDS);
        assertEquals("Event data should load successfully", true, completed);
        assertNotNull("Event should not be null", loadedEvent[0]);
        
        // Verify event properties match our test data
        assertEquals("Event ID should match", TEST_EVENT_ID, loadedEvent[0].getId());
        assertEquals("Event title should match", "test qr", loadedEvent[0].getTitle());
        assertEquals("Event price should match", 100.0, loadedEvent[0].getPrice(), 0.01);
        assertEquals("Event status should match", EventStatus.UPCOMING, loadedEvent[0].getEventStatus());
    }
}