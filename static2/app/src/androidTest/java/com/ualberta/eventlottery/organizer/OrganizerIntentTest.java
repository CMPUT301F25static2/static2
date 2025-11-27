package com.ualberta.eventlottery.organizer;

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
 * Android instrumentation tests for organizer intent-based functionality.
 * Tests the intent flows and navigation between organizer screens.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerIntentTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the OrganizerMainActivity launches successfully.
     */
    @Test
    public void testOrganizerMainActivityLaunch() {
        // Check that the main organizer container is displayed.
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * Tests navigation from the organizer home to the event creation screen.
     */
    @Test
    public void testOrganizerNavigationToEventCreation() {
        // Click on the create event button.
        onView(withId(R.id.btn_create_event)).perform(click());

        // Verify that the event creation screen is displayed.
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    /**
     * Tests navigation from the organizer home to the event details screen.
     */
    @Test
    public void testOrganizerNavigationToEventDetails() {
        // Click on the first event in the list.
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed.
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));
    }

    /**
     * Tests navigation from the organizer home to the event draw screen.
     */
    @Test
    public void testOrganizerNavigationToEventDraw() {
        // Click on the draw button for the first event in the list.
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_draw)).perform(click());

        // Verify that the draw screen is displayed.
        onView(withId(R.id.et_number_to_draw)).check(matches(isDisplayed()));
    }

    /**
     * Tests navigation to the QR code screen from the event details screen.
     */
    @Test
    public void testNavigationToQrCodeScreen() {
        // Click on the first event in the list to go to details.
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Click on the QR code button.
        onView(withId(R.id.btn_event_show_qrcode)).perform(click());

        // Verify that the QR code screen is displayed.
        onView(withId(R.id.iv_event_qrcode)).check(matches(isDisplayed()));
    }
}
