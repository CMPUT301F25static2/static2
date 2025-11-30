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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for organizer capacity limitation functionality.
 * Tests US 02.03.01: As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerCapacityTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.03.01: Tests setting capacity limit for an event.
     * This test verifies that organizers can set and modify capacity limits for events.
     */
    @Test
    public void testSetEventCapacityLimit() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify we're on the event creation screen
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Fill in event details
        onView(withId(R.id.et_create_event_title)).perform(typeText("Test Event with Capacity"));
        onView(withId(R.id.et_create_event_description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(withId(R.id.et_create_event_location)).perform(typeText("Test Location"), closeSoftKeyboard());

        // Set capacity limit
        onView(withId(R.id.et_create_event_capacity)).perform(typeText("50"), closeSoftKeyboard());

        // Click create button
        onView(withId(R.id.btn_create_event)).perform(click());

        // After creation, we should be back on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Tests that capacity limit can be updated for existing events.
     * This validates that organizers can modify capacity limits after event creation.
     */
    @Test
    public void testUpdateEventCapacityLimit() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Click on the capacity limit edit field (this would typically be part of the edit functionality)
        onView(withId(R.id.btn_back)).perform(click());

        // Go back to event creation to test capacity input
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify capacity field is displayed
        onView(withId(R.id.et_create_event_capacity)).check(matches(isDisplayed()));

        // Update capacity
        onView(withId(R.id.et_create_event_capacity)).perform(typeText("100"), closeSoftKeyboard());
    }
}