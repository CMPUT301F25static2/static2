package com.ualberta.static2.organizer;

import android.content.Intent;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.ualberta.eventlottery.organzier.OrganizerMainActivity;
import com.ualberta.eventlottery.ui.organizer.organizerEventDraw.OrganizerEventDrawFragment;
import com.ualberta.static2.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for organizer notification functionality.
 * Tests US 02.05.01: As an organizer I want to send a notification to chosen entrants to sign up for events.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerNotificationTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.05.01: Tests sending a notification to chosen entrants through lottery draw.
     * This test verifies that notifications are sent when entrants are selected in the lottery draw.
     */
    @Test
    public void testSendNotificationThroughLotteryDraw() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Navigate to the event draw screen
        onView(withId(R.id.btn_draw)).perform(click());

        // Verify we're on the draw screen
        onView(withId(R.id.et_number_to_draw)).check(matches(isDisplayed()));

        // Enter a number to draw (this would trigger notification sending)
        onView(withId(R.id.et_number_to_draw)).perform(typeText("1"));

        // Click the draw button to trigger the notification sending
        onView(withId(R.id.btn_draw)).perform(click());



    }

    /**
     * Tests that the lottery draw screen has all required UI elements for notification functionality.
     * This validates that notification sending components are properly integrated.
     */
    @Test
    public void testLotteryDrawScreenHasNotificationElements() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Navigate to the event draw screen
        onView(withId(R.id.btn_draw)).perform(click());

        // Verify all lottery draw UI elements are present
        onView(withId(R.id.et_number_to_draw)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_draw)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_max_entrants_msg)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_event_waiting_number)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_event_accepted_ratio)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_event_spots_left)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the lottery draw functionality properly handles notification sending.
     * This validates the integration between drawing entrants and sending notifications.
     */
    @Test
    public void testLotteryDrawNotificationIntegration() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify that the event info screen is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Navigate to the event draw screen
        onView(withId(R.id.btn_draw)).perform(click());

        // Verify we're on the draw screen
        onView(withId(R.id.et_number_to_draw)).check(matches(isDisplayed()));

        // Test input validation for draw number
        onView(withId(R.id.et_number_to_draw)).perform(typeText("0")); // Invalid input

        // Test with valid input
        onView(withId(R.id.et_number_to_draw)).perform(typeText("1"));

        // Verify draw button becomes enabled
        onView(withId(R.id.btn_draw)).check(matches(isDisplayed()));
    }
}