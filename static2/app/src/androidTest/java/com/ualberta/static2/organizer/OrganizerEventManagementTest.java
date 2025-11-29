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
 * Android instrumentation tests for organizer event management functionality.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEventManagementTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the event list and summary statistics are displayed.
     */
    @Test
    public void testOrganizerCanViewEventList() {
        // Verify the event list is displayed
        onView(withId(R.id.lv_organzier_event_list)).check(matches(isDisplayed()));

        // Verify statistics are displayed
        onView(withId(R.id.tv_total_events)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_total_entrants)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_full_rate)).check(matches(isDisplayed()));
    }

    /**
     * Tests if an organizer can view the details of an event.
     */
    @Test
    public void testOrganizerCanViewEventDetails() {
        // Click on the first event in the list
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event details screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));
    }

    /**
     * US 02.05.02: Tests if an organizer can initiate the event draw process.
     */
    @Test
    public void testOrganizerCanInitiateEventDraw() {
        // Click on the draw button for the first event
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_draw)).perform(click());

        // Verify that the draw screen is opened
        onView(withId(R.id.fragment_organizer_draw)).check(matches(isDisplayed()));
    }

    /**
     * US 02.04.02: Tests updating an event poster from the details screen.
     */
    @Test
    public void testUpdateEventPoster() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Click on the event poster image to initiate the update process
        onView(withId(R.id.iv_event_poster_img)).perform(click());

    }
}
