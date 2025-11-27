package com.ualberta.eventlottery.organizer;

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
 * Android instrumentation tests for organizer event poster functionality.
 * Tests US 02.04.01: As an organizer I want to upload an event poster to the event details page to provide visual information to entrants.
 * Tests US 02.04.02: As an organizer I want to update an event poster to provide visual information to entrants.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerPosterTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Tests uploading an event poster during event creation.
     */
    @Test
    public void testUploadEventPoster() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify we're on the event creation screen
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Verify that the poster upload area is displayed
        onView(withId(R.id.iv_event_poster)).check(matches(isDisplayed()));

        // Click on the poster image view to initiate the upload process
        // Note: Actual image selection would require mocking intents, so we'll just verify the click is possible
        onView(withId(R.id.iv_event_poster)).perform(click());
    }

    /**
     * Tests updating an event poster from the event details screen.
     */
    @Test
    public void testUpdateEventPoster() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify that the poster image view is displayed
        onView(withId(R.id.iv_event_poster_img)).check(matches(isDisplayed()));

        // Click on the poster image to initiate the update process
        // Note: Actual image selection would require mocking intents, so we'll just verify the click is possible
        onView(withId(R.id.iv_event_poster_img)).perform(click());
    }

    /**
     * Tests that poster upload functionality works during event creation.
     * This test verifies the UI elements for poster uploading are present and clickable.
     */
    @Test
    public void testPosterUploadElementsPresent() {
        // Click create event button
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify we're on the event creation screen
        onView(withId(R.id.main)).check(matches(isDisplayed()));

        // Verify all poster-related UI elements are displayed
        onView(withId(R.id.iv_event_poster)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_create_event)).check(matches(isDisplayed()));
    }
}