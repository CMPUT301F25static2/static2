package com.ualberta.static2.organizer;

import android.content.Intent;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.static2.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for organizer geolocation functionality.
 * Tests US 02.02.03: As an organizer I want to enable or disable the geolocation requirement for my event.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerGeolocationTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Tests enabling/disabling the geolocation requirement for an event.
     */
    @Test
    public void testToggleGeolocationRequirement() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify that the geolocation requirement toggle is displayed
        onView(withId(R.id.btn_event_update_geolocation)).check(matches(isDisplayed()));

        // Click on the geolocation switch to disable it
        onView(withId(R.id.switch_event_geolocation_required)).perform(click());

        // Click the switch again to re-enable it
        onView(withId(R.id.switch_event_geolocation_required)).perform(click());
    }

    /**
     * Tests that geolocation requirement can be set during event creation.
     * This validates that organizers can set geolocation requirement when creating new events.
     */
    @Test
    public void testSetGeolocationRequirementDuringCreation() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify we're on the event creation screen
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Verify that the geolocation requirement toggle is displayed
        onView(withId(R.id.switch_geolocation_required)).check(matches(isDisplayed()));

        // Toggle the geolocation switch to disable it
        onView(withId(R.id.switch_geolocation_required)).perform(click());

        // Toggle the switch back to enable it
        onView(withId(R.id.switch_geolocation_required)).perform(click());
    }
}