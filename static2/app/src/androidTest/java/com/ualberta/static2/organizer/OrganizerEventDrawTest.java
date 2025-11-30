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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for organizer event drawing functionality.
 * Tests the lottery draw feature that allows organizers to select winners.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerEventDrawTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    /**
     * Sets up the test environment before each test.
     * Ensures the organizer home screen is displayed.
     */
    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.05.02: Tests if the organizer can access the event draw functionality.
     */
    @Test
    public void testOrganizerCanAccessEventDrawFunctionality() {
        // Click on the draw button of the first event in the list
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_draw)).perform(click());

        // Verify that the draw screen is displayed
        onView(withId(R.id.fragment_organizer_draw)).check(matches(isDisplayed()));
    }

    /**
     * Verifies that the event draw screen displays the correct information.
     */
    @Test
    public void testEventDrawScreenDisplaysCorrectInformation() {
        // Click on the draw button of the first event in the list
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_draw)).perform(click());

        // Verify that the draw screen is displayed
        onView(withId(R.id.fragment_organizer_draw)).check(matches(isDisplayed()));

        // Verify key UI elements are displayed
        onView(withId(R.id.btn_draw)).check(matches(isDisplayed()));
    }

    /**
     * Tests the input validation for the number of winners to draw.
     */
    @Test
    public void testEventDrawInputValidation() {
        // Click on the draw button of the first event in the list
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_draw)).perform(click());

        // Verify that the draw screen is displayed
        onView(withId(R.id.fragment_organizer_draw)).check(matches(isDisplayed()));

        // Enter an invalid number of winners (e.g., a large number)
        onView(withId(R.id.et_number_to_draw)).perform(typeText("1000"), closeSoftKeyboard());

        // Verify that the draw button shows an error message or is disabled
        onView(withId(R.id.btn_draw)).check(matches(withText("Invalid number")));
    }

    /**
     * US 02.05.02: Simulates performing a winner draw.
     */
    @Test
    public void testPerformDraw() {
        // Click on the draw button of the first event
        DataInteraction dataInteraction = onData(anything())
                .inAdapterView(withId(R.id.lv_organzier_event_list))
                .atPosition(0);
        dataInteraction.onChildView(withId(R.id.btn_draw)).perform(click());

        // Enter a valid number of winners to draw
        onView(withId(R.id.et_number_to_draw)).perform(typeText("1"), closeSoftKeyboard());

        // Click the draw button
        onView(withId(R.id.btn_draw)).perform(click());

        // After the draw, a confirmation or updated view should be shown.
    }
}
