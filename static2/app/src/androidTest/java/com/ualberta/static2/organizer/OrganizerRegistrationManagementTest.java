package com.ualberta.static2.organizer;

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
 * Android instrumentation tests for organizer registration management functionality.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerRegistrationManagementTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.02.01, US 02.02.02: Tests if the organizer can view registrations on the map.
     */
    @Test
    public void testOrganizerCanViewRegistrations() {
        // Click on an event to navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify that the map and entrant filters are displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.chip_group_entrant_status)).check(matches(isDisplayed()));
    }

    /**
     * US 02.06.05: Tests if the organizer can export event registrations to CSV.
     */
    @Test
    public void testOrganizerCanExportRegistrations() {
        // Click on the export button of the first event
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_export)).perform(click());


    }

    /**
     * US 02.02.01: Tests the functionality of registration status filtering.
     */
    @Test
    public void testOrganizerCanManageRegistrationStatus() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Click on the "Waiting List" chip to filter the view
        onView(withId(R.id.chip_waiting_list)).perform(click());

        // Click on the "All" chip to return to the default view
        onView(withId(R.id.chip_all_entrants)).perform(click());
    }

    /**
     * US 02.02.03: Tests toggling the geolocation requirement from the event details screen.
     */
    @Test
    public void testToggleGeolocationRequirement() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Click on the geolocation switch to disable it
        onView(withId(R.id.switch_event_geolocation_required)).perform(click());

        // Click the switch again to re-enable it
        onView(withId(R.id.switch_event_geolocation_required)).perform(click());
    }
}
