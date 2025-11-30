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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for organizer registration period functionality.
 * Tests US 02.01.04: As an organizer, I want to set a registration period.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerRegistrationPeriodTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.01.04: Tests setting the registration period for an event.
     * This test verifies that organizers can set both registration start and end times.
     */
    @Test
    public void testSetRegistrationPeriod() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify we're on the event creation screen
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Fill in event details
        onView(withId(R.id.et_create_event_title)).perform(typeText("Test Event for Registration Period"));
        onView(withId(R.id.et_create_event_description)).perform(typeText("Test Description"), closeSoftKeyboard());
        onView(withId(R.id.et_create_event_location)).perform(typeText("Test Location"), closeSoftKeyboard());
        onView(withId(R.id.et_create_event_capacity)).perform(typeText("50"), closeSoftKeyboard());

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

        // Click create button
        onView(withId(R.id.btn_create_event)).perform(click());

        // After creation, we should be back on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }
}