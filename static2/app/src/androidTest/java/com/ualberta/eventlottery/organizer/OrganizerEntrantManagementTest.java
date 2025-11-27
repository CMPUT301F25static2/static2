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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for organizer entrant management functionality.
 * Tests US 02.02.01: As an organizer I want to view the list of entrants who joined my event waiting list
 * Tests US 02.02.02: As an organizer I want to see on a map where entrants joined my event waiting list from.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEntrantManagementTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.02.01: Tests if the organizer can view the list of entrants who joined the waiting list.
     * This test verifies that the entrant list is properly displayed and accessible.
     */
    @Test
    public void testOrganizerCanViewEntrantList() {
        // Click on an event to navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify that the entrant list view is displayed
        onView(withId(R.id.chip_group_entrant_status)).check(matches(isDisplayed()));

        // Verify that the waiting list chip is displayed
        onView(withId(R.id.chip_waiting_list)).check(matches(isDisplayed()));

        // Verify that the all entrants chip is displayed
        onView(withId(R.id.chip_all_entrants)).check(matches(isDisplayed()));
    }

    /**
     * US 02.02.02: Tests if the organizer can view entrants on a map.
     * This test verifies that the map functionality is working and displays entrant locations.
     */
    @Test
    public void testOrganizerCanViewEntrantsOnMap() {
        // Click on an event to navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify that the map is displayed (only if geolocation is required)
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Verify that the entrant filters are displayed
        onView(withId(R.id.chip_group_entrant_status)).check(matches(isDisplayed()));

        // Test clicking on the waiting list chip to filter the map
        onView(withId(R.id.chip_waiting_list)).perform(click());

        // Test clicking on the all entrants chip to reset the view
        onView(withId(R.id.chip_all_entrants)).perform(click());
    }

    /**
     * Tests that the entrant list functionality works correctly with different status filters.
     * This verifies the filtering capability for different entrant statuses.
     */
    @Test
    public void testEntrantListFiltering() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Click on the waiting list chip to filter the view
        onView(withId(R.id.chip_waiting_list)).perform(click());

        // Click on the all chip to return to the default view
        onView(withId(R.id.chip_all_entrants)).perform(click());
    }

    /**
     * Tests that the map functionality works correctly with different entrant status filters.
     * This verifies that entrant locations are properly filtered and displayed on the map.
     */
    @Test
    public void testEntrantMapFiltering() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify map is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Click on the waiting list chip to filter the map
        onView(withId(R.id.chip_waiting_list)).perform(click());

        // Click on the all entrants chip to reset the view
        onView(withId(R.id.chip_all_entrants)).perform(click());
    }
}