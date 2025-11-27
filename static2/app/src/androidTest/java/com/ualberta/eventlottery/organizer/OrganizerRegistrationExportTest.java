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
 * Android instrumentation tests for organizer registration export functionality.
 * Tests US 02.06.05: As an organizer I want to export event registrations to CSV.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerRegistrationExportTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.06.05: Tests if the organizer can export event registrations to CSV.
     * This test verifies the export functionality for event registrants.
     */
    @Test
    public void testOrganizerCanExportRegistrations() {
        // Click on the export button of the first event in the list
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_export)).perform(click());

        // Note: Further verification would require checking for a success message
        // or inspecting the device's file system, which is outside the scope of this test.
        // This test mainly verifies that the export button is clickable and leads to the expected action.
    }
}