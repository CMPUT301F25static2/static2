package com.ualberta.eventlottery.organizer;

import android.content.Intent;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.ui.organizer.organizerEventCreate.OrganizerEventCreateFragment;
import com.ualberta.static2.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Android instrumentation tests for organizer event creation functionality.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEventCreationTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.01.01: Tests if an organizer can create a new event.
     */
    @Test
    public void testOrganizerCanCreateEvent() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify we're on the event creation screen
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Fill in event details
        onView(withId(R.id.et_create_event_title)).perform(typeText("Test Event"));
        onView(withId(R.id.et_create_event_description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(withId(R.id.et_create_event_location)).perform(typeText("Test Location"), closeSoftKeyboard());
        onView(withId(R.id.et_create_event_capacity)).perform(typeText("50"), closeSoftKeyboard());

        // Click create button
        onView(withId(R.id.btn_create_event)).perform(click());

        // After creation, we should be back on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that event creation fails if required fields are empty.
     */
    @Test
    public void testOrganizerEventCreationValidation() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Try to create event without filling in any fields
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify that a validation error message is shown (e.g., for the title)
        onView(withText("Please enter event title")).check(matches(isDisplayed()));
    }

    /**
     * US 02.01.04: Tests setting the registration period for an event.
     */
    @Test
    public void testSetRegistrationPeriod() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Click on the registration start date to open the date picker
        onView(withId(R.id.tv_registration_start_Date)).perform(click());
        onView(withText("OK")).perform(click()); // Confirm date

        // Click on the registration start time to open the time picker
        onView(withId(R.id.tv_registration_start_time)).perform(click());
        onView(withText("OK")).perform(click()); // Confirm time

        // Click on the registration end date
        onView(withId(R.id.tv_registration_end_date)).perform(click());
        onView(withText("OK")).perform(click()); // Confirm date

        // Click on the registration end time
        onView(withId(R.id.tv_registration_end_time)).perform(click());
        onView(withText("OK")).perform(click()); // Confirm time
    }

    /**
     * US 02.04.01: Tests initiating the event poster upload process.
     */
    @Test
    public void testUploadEventPoster() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Click on the poster image view to open the image chooser
        onView(withId(R.id.iv_event_poster)).perform(click());

        // Further testing would require mocking the image picker intent, so we stop here.
    }

    /**
     * US 02.02.03: Tests toggling the geolocation requirement during event creation.
     */
    @Test
    public void testToggleGeolocationOnCreation() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Click the geolocation switch to disable it
        onView(withId(R.id.switch_geolocation_required)).perform(click());

        // Click it again to re-enable it
        onView(withId(R.id.switch_geolocation_required)).perform(click());
    }
}
