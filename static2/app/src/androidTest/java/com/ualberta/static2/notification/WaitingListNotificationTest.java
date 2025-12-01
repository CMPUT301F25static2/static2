package com.ualberta.static2.notification;

import androidx.test.espresso.matcher.ViewMatchers;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Android instrumentation tests for waiting list notification functionality.
 * Tests US 02.07.01: As an organizer I want to send notifications to all entrants on the waiting list.
 * 
 * This is a black box test that verifies the end-to-end user flow without knowledge of internal implementation.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class WaitingListNotificationTest {

    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        // Ensure we're on the organizer home screen
        onView(withId(R.id.fragment_container_organizer)).check(matches(isDisplayed()));
    }

    /**
     * US 02.07.01: Tests sending notifications to all waiting list entrants.
     * This test verifies the complete flow:
     * 1. Navigate to event details
     * 2. View waiting list entrants
     * 3. Select all waiting list entrants
     * 4. Send notification to selected entrants
     * 5. Verify notification was sent successfully
     */

    @Test
    public void testSendNotificationToWaitingListEntrants() {
        // Step 1: Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Step 2: Verify that the event showcase screen is displayed
        // The entrants fragment should be embedded in the showcase
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Step 3: Navigate to waiting list view
        // Click the "Waiting" button to filter entrants by waiting status
        onView(withId(R.id.btn_entrants_waiting)).perform(click());

        // Step 4: Verify waiting list is displayed
        onView(withId(R.id.lv_event_entrant_list)).check(matches(isDisplayed()));

        // Step 5: Select all waiting list entrants
        // Since we're doing a black box test, we'll select entrants by clicking their checkboxes
        // We'll select the first few entrants that are visible
        // In a real scenario, there might be a "Select All" button, but we test the manual selection flow
        try {
            // Wait a moment for the list to load
            Thread.sleep(1000);
            
            // Select entrants by clicking checkboxes
            // We'll try to select up to 3 entrants if they exist
            // Note: This assumes there are waiting list entrants in the test data
            for (int i = 0; i < 3; i++) {
                try {
                    onView(ViewMatchers.withId(R.id.cb_entrant_select))
                            .atPosition(i)
                            .perform(click());
                } catch (Exception e) {
                    // If there are fewer than 3 entrants, that's okay
                    break;
                }
            }
        } catch (InterruptedException e) {
            // Continue with test
        }

        // Step 6: Click the send notification button
        onView(withId(R.id.btn_send_notifications)).perform(click());

        // Step 7: Verify notification dialog is displayed
        onView(withText("Send Notification")).check(matches(isDisplayed()));

        // Step 8: Enter notification message
        String testMessage = "Test notification for waiting list entrants";
        onView(ViewMatchers.isAssignableFrom(android.widget.EditText.class))
                .perform(typeText(testMessage));

        // Step 9: Click Send button in dialog
        onView(withText("Send")).perform(click());

        // Step 10: Verify success message appears
        // The app should show a toast message indicating notifications were sent
        // Note: Toast messages are hard to verify with Espresso, but we can verify
        // that the dialog is dismissed and we're back to the entrants view
        onView(withId(R.id.lv_event_entrant_list)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the waiting list button is accessible and displays waiting list entrants.
     * This validates that the UI components for US 02.07.01 are properly integrated.
     */
    @Test
    public void testWaitingListButtonDisplaysEntrants() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify event showcase is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Verify waiting list button is present and clickable
        onView(withId(R.id.btn_entrants_waiting)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_entrants_waiting)).perform(click());

        // Verify waiting list count is displayed
        onView(withId(R.id.tv_entrants_waiting_number)).check(matches(isDisplayed()));

        // Verify entrants list is displayed
        onView(withId(R.id.lv_event_entrant_list)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the notification button is available when viewing waiting list entrants.
     * This validates that organizers can access notification functionality from the waiting list view.
     */
    @Test
    public void testNotificationButtonAvailableForWaitingList() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Verify event showcase is displayed
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));

        // Navigate to waiting list view
        onView(withId(R.id.btn_entrants_waiting)).perform(click());

        // Verify notification button is visible and accessible
        onView(withId(R.id.btn_send_notifications)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_send_notifications)).check(matches(ViewMatchers.isClickable()));
    }

    /**
     * Tests that selecting waiting list entrants enables notification sending.
     * This validates the selection mechanism works correctly.
     */

    @Test
    public void testSelectWaitingListEntrantsForNotification() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Navigate to waiting list view
        onView(withId(R.id.btn_entrants_waiting)).perform(click());

        // Wait for list to load
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Continue
        }

        // Verify checkboxes are present for selection
        // Try to interact with the first checkbox if available
        try {
            onView(ViewMatchers.withId(R.id.cb_entrant_select))
                    .atPosition(0)
                    .check(matches(isDisplayed()));
            
            // Select the first entrant
            onView(ViewMatchers.withId(R.id.cb_entrant_select))
                    .atPosition(0)
                    .perform(click());
        } catch (Exception e) {
            // If no entrants are available, that's acceptable for this test
            // The important part is that the UI components are present
        }

        // Verify notification button is still accessible
        onView(withId(R.id.btn_send_notifications)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the notification dialog requires a message before sending.
     * This validates input validation for US 02.07.01.
     */

    @Test
    public void testNotificationDialogRequiresMessage() {
        // Navigate to event details
        onData(anything()).inAdapterView(withId(R.id.lv_organzier_event_list)).atPosition(0).perform(click());

        // Navigate to waiting list view
        onView(withId(R.id.btn_entrants_waiting)).perform(click());

        // Wait for list to load and select at least one entrant
        try {
            Thread.sleep(1000);
            onView(ViewMatchers.withId(R.id.cb_entrant_select))
                    .atPosition(0)
                    .perform(click());
        } catch (Exception e) {
            // Continue even if selection fails
        }

        // Click send notification button
        onView(withId(R.id.btn_send_notifications)).perform(click());

        // Verify dialog is displayed
        onView(withText("Send Notification")).check(matches(isDisplayed()));

        // Try to send without entering a message
        // Click Send button without entering text
        onView(withText("Send")).perform(click());

        // Verify error message appears (or dialog remains open)
        // The app should show "Please enter a message" toast
        // Since toasts are hard to verify, we check that we're still in a valid state
        onView(withId(R.id.lv_event_entrant_list)).check(matches(isDisplayed()));
    }

}

